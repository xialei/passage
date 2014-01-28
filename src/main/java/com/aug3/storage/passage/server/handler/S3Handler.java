package com.aug3.storage.passage.server.handler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aug3.storage.passage.thrift.SObject;
import com.aug3.storage.passage.util.ConfigManager;

public class S3Handler implements RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(S3Handler.class);

	private static String accessKey = ConfigManager.getProperty("s3.accessKey");
	private static String secretKey = ConfigManager.getProperty("s3.secretKey");

	private AWSCredentials awsCredentials;
	private RestS3Service s3Service;

	public S3Handler() {
		awsCredentials = new AWSCredentials(accessKey, secretKey);
		try {
			s3Service = new RestS3Service(awsCredentials);
		} catch (S3ServiceException e) {
			log.error("", e);
		}
	}

	@Override
	public boolean putObject(String bucketName, String key, byte[] data) {
		bucketName = validateBucketName(bucketName);
		key = validateKey(key);

		S3Object fileObj = null;
		try {
			fileObj = new S3Object(key, data);
			fileObj.setBucketName(bucketName);

			s3Service.putObject(bucketName, fileObj);
		} catch (S3ServiceException e) {
			log.error("", e);
		} catch (NoSuchAlgorithmException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}
		return true;
	}

	@Override
	public SObject getObject(String bucketName, String key) {
		bucketName = validateBucketName(bucketName);
		key = validateKey(key);

		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;

		SObject sObj = new SObject();

		try {
			S3Object fileObj = s3Service.getObject(bucketName, key);
			bis = new BufferedInputStream(fileObj.getDataInputStream());
			bos = new ByteArrayOutputStream(1024);

			byte[] temp = new byte[1024];
			int len;
			while ((len = bis.read(temp)) != -1) {
				bos.write(temp, 0, len);
			}

			sObj.setKey(key);
			sObj.setData(bos.toByteArray());

		} catch (S3ServiceException e) {
			log.error("", e);
		} catch (ServiceException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
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
		bucketName = validateBucketName(bucketName);
		key = validateKey(key);

		try {
			s3Service.deleteObject(bucketName, key);
		} catch (ServiceException e) {
			log.error("", e);
			return false;
		}

		return true;
	}

	@Override
	public boolean isObjectInBucket(String bucketName, String key) {
		bucketName = validateBucketName(bucketName);
		key = validateKey(key);

		try {
			return s3Service.isObjectInBucket(bucketName, key);
		} catch (Exception e) {
			return false;
		}
	}

	private String validateBucketName(String bucketName) {
		if (bucketName.startsWith("/")) {
			bucketName = bucketName.substring(1);
		}
		if (bucketName.endsWith("/")) {
			bucketName = bucketName.substring(0, bucketName.length() - 1);
		}
		return bucketName;
	}

	private String validateKey(String key) {
		if (key.startsWith("/")) {
			key = key.substring(1);
		}
		return key;
	}

    @Override
    public boolean createImg(String bucketName, String key) {
        // return false directly, because VSTO team won't operate S3 directly
        return false;
    }

}
