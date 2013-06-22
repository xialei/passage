package com.aug3.storage.passage.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.aug3.storage.passage.thrift.PassageService;
import com.aug3.storage.passage.util.ConfigManager;

public class PassageServer {

	private String server = ConfigManager.getProperty("passage.server");
	private int port = ConfigManager.getIntProperty("passage.port", 8888);
	private static boolean security = ConfigManager.getBooleanProperty("passage.security", false);

	public void startServer() {
		try {

			TServerSocket serverTransport = new TServerSocket(port);

			PassageService.Processor processor = new PassageService.Processor(new PassageServiceImpl());

			Factory portFactory = new TBinaryProtocol.Factory(true, true);

			Args args = new Args(serverTransport);
			args.processor(processor);
			args.protocolFactory(portFactory);

			TServer server = new TThreadPoolServer(args);
			server.serve();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}

	public void startServerInSecureMode() {
		try {

			TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
			params.setKeyStore(this.getClass().getResource("/key/keystore.jks").getPath(), "chin@sc0pe");

			TServerSocket serverTransport = TSSLTransportFactory.getServerSocket(port, 30000,
					InetAddress.getByName(server), params);
			PassageService.Processor processor = new PassageService.Processor(new PassageServiceImpl());

			TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
			System.out.println("Starting server on port 8888 ...");
			server.serve();

		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PassageServer server = new PassageServer();
		if(security){
			server.startServerInSecureMode();
		}else{
			server.startServer();
		}

	}

}
