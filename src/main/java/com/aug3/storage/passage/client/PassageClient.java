package com.aug3.storage.passage.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

public class PassageClient {

	private static String server = ConfigManager.getProperty("passage.server");
	private static int port = ConfigManager.getIntProperty("passage.port", 8888);

	public void passageHandler(Action action) {

		TTransport transport = new TSocket(server, port);

		TProtocol protocol = new TBinaryProtocol(transport);

		PassageService.Client client = new PassageService.Client(protocol);

		try {
			transport.open();
			action.perform(client);
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}

		transport.close();

	}

	public void passageInvoker(Action action) {
		TTransport transport;
		try {

			TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
			params.setTrustStore(this.getClass().getResource("/key/truststore.jks").getPath(), "chin@sc0pe");

			transport = TSSLTransportFactory.getClientSocket(server, port, 30000, params);
			TProtocol protocol = new TBinaryProtocol(transport);

			PassageService.Client client = new PassageService.Client(protocol);

			action.perform(client);

			transport.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
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

			new PassageClient().passageInvoker(a);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);

	}

}
