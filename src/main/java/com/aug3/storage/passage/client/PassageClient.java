package com.aug3.storage.passage.client;

import java.io.*;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.aug3.storage.passage.client.action.Action;
import com.aug3.storage.passage.client.action.PutObjectAction;
import com.aug3.storage.passage.thrift.PassageService;
import com.aug3.storage.passage.thrift.SObject;
import com.aug3.storage.passage.thrift.Storage;
import com.aug3.storage.passage.thrift.Strategy;
import com.aug3.storage.passage.util.ConfigManager;

public class PassageClient implements Serializable {

    private static final long serialVersionUID = -2805284943658356093L;

    private static String server = ConfigManager.getProperty("passage.server");
    private static int port = ConfigManager.getIntProperty("passage.port", 8888);

    public Object perform(Action action) {

        TTransport transport = new TSocket(server, port);

        TProtocol protocol = new TBinaryProtocol(transport);

        PassageService.Client client = new PassageService.Client(protocol);

        try {
            transport.open();
            return action.perform(client);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            transport.close();
        }
        return null;
    }

    public Object securePerform(Action action) {
        TTransport transport = null;
        try {

            TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
            params.setTrustStore(this.getClass().getResource("/key/truststore.jks").getPath(), "chin@sc0pe");

            transport = TSSLTransportFactory.getClientSocket(server, port, 30000, params);
            TProtocol protocol = new TBinaryProtocol(transport);

            PassageService.Client client = new PassageService.Client(protocol);

            return action.perform(client);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                transport.close();
            }
        }
        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Strategy strategy = new Strategy();
        strategy.setBucketName("D://received//gen-java//");
        strategy.setSType(Storage.HAFS);

        SObject sObj = new SObject();
        sObj.setKey("abc.pdf");

        long t1 = System.currentTimeMillis();

        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream("D://work//diveintopythonzh-cn.pdf"));
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            in.close();

            byte[] content = out.toByteArray();

            sObj.setData(content);

            PutObjectAction a = new PutObjectAction();
            a.setStrategy(strategy);
            a.setsObj(sObj);

            new PassageClient().securePerform(a);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);

    }

}
