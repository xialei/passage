package com.aug3.storage.passage.server.handler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aug3.storage.passage.thrift.SObject;

public class HafsHandler implements RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(HafsHandler.class);
	
	@Override
	public boolean putObject(String bucketName, String key, byte[] data) {

		File f = new File(validateObjKey(bucketName, key));
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * sugguest to use service to get file
	 */
	@Override
	public SObject getObject(String bucketName, String key) {

		SObject sObj = new SObject();

		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(validateObjKey(bucketName, key)));

			bos = new ByteArrayOutputStream(1024);

			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = bis.read(temp)) != -1) {
				bos.write(temp, 0, size);
			}

			sObj.setKey(key);
			sObj.setData(bos.toByteArray());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e2) {
					log.error("", e2);
					bos = null;
				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e2) {
					log.error("", e2);
					bis = null;
				}
			}
		}

		return sObj;

	}

	@Override
	public List<SObject> listObject(String bucketName, List<String> key) {
		List<SObject> list = new ArrayList<SObject>();
		for (String k : key) {
			list.add(getObject(bucketName, k));
		}
		return list;
	}

	@Override
	public boolean deleteObject(String bucketName, String key) {
		File f = new File(validateObjKey(bucketName, key));
		if (f.exists()) {
			f.delete();
		}
		return true;
	}

	@Override
	public boolean isObjectInBucket(String bucketName, String key) {
		File f = new File(validateObjKey(bucketName, key));
		if (f.exists()) {
			return true;
		} else {
			return false;
		}
	}

	private String validateObjKey(String bucketName, String key) {
		bucketName = bucketName.replace("\\", File.separator).replace("/", File.separator);
		if (bucketName.endsWith(File.separator)) {
			bucketName = bucketName.substring(0, bucketName.length() - 1);
		}
		key = key.replace("\\", File.separator).replace("/", File.separator);
		if (!key.startsWith(File.separator)) {
			key = File.separator + key;
		}
		return bucketName + key;
	}

}
