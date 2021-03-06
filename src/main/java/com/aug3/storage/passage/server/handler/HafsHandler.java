package com.aug3.storage.passage.server.handler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aug3.storage.passage.thrift.SObject;

public class HafsHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HafsHandler.class);

    @Override
    public boolean putObject(String bucketName, String key, byte[] data) {

        File f = new File(validateObjKey(bucketName, key));

        File folder = new File(f.getParent());
        boolean exists = true;
        if (!folder.exists()) {
            exists = folder.mkdirs();
            log.info("create folder[" + folder.getPath() + "]");
        }

        if (exists) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                fos.write(data);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                log.info(e.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return true;
        } else {
            return false;
        }
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

    private ConvertCmd cmd;
    private IMOperation op;

    public HafsHandler() {
        cmd = new ConvertCmd(true);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            cmd.setGlobalSearchPath("D:\\GraphicsMagick-1.3.17-Q16");
        }

        op = new IMOperation();
        op.addImage("-[0]");
        op.colors(256);
        op.addImage("png:-");
    }

    @Override
    public boolean createImg(String bucketName, String key) {
        InputStream is = null;
        ByteArrayOutputStream baos = null;

        try {
            String fullPath = bucketName + key;

            is = new BufferedInputStream(new FileInputStream(new File(fullPath)));
            if (is != null) {
                baos = new ByteArrayOutputStream();
                Pipe pipe = new Pipe(is, baos);
                cmd.setInputProvider(pipe);
                cmd.setOutputConsumer(pipe);
                cmd.run(op);
                
                putObject(bucketName, key.replace("/pdf/", "/img/").replace(".pdf", ".png"), baos.toByteArray());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("Failed create img, key=[" + key + "]", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                }
            }
        }

        return false;
    }
}
