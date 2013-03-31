Secure an Apache Thrift Service

You need to have a key store with server private key at server-side and a trust store containing server’s public key at client side. For this example let’s create a key store and trust store using JDK keytool.

===Step 1:
$ keytool -genkeypair -alias certificatekey -keyalg RSA -validity 7 -keystore keystore.jks
输入keystore密码：  mypassword
再次输入新密码: mypassword
您的名字与姓氏是什么？
  [Unknown]：  Roger
您的组织单位名称是什么？
  [Unknown]：  myorg
您的组织名称是什么？
  [Unknown]：  myorg
您所在的城市或区域名称是什么？
  [Unknown]：  SH
您所在的州或省份名称是什么？
  [Unknown]：  SH
该单位的两字母国家代码是什么
  [Unknown]：  China
CN=Roger, OU=myorg, O=myorg, L=SH, ST=SH, C=China 正确吗？
  [否]：  y

输入<certificatekey>的主密码
        （如果和 keystore 密码相同，按回车）：

---------
notes:
Give a suitable password and answers to the prompts. After that it will create the key store keystore.jks containing generated private/ public key pair.

===Step 2:
Export the certificate (cret.cer) containing the public key from the key store using following command.

$ keytool -export -alias certificatekey -keystore keystore.jks -rfc -file cert.cer
输入keystore密码：  mypassword
保存在文件中的认证 <cert.cer>


===Step 3:
Now let’s create the trust store (truststore.jks) and import the certificate to it. This can be done using single command line as given below.

$ keytool -import -alias certificatekey -file cert.cer -keystore truststore.jks
输入keystore密码：  mypassword
再次输入新密码: mypassword
所有者:CN=Roger, OU=myorg, O=myorg, L=SH, ST=SH, C=China
签发人:CN=Roger, OU=myorg, O=myorg, L=SH, ST=SH, C=China
序列号:5156cfcf
有效期: Sat Mar 30 19:43:11 CST 2013 至Sat Apr 06 19:43:11 CST 2013
证书指纹:
         MD5:6B:E8:2B:64:88:B4:CB:66:2A:A0:05:C7:CC:D4:32:66
         SHA1:95:16:9C:F0:54:B6:46:4C:FF:FD:FE:6C:38:6D:22:13:4B:45:E1:02
         签名算法名称:SHA1withRSA
         版本: 3
信任这个认证？ [否]：  y
认证已添加至keystore中

Now the certificate setup is complete. Let’s create the secure Thrift server and client to consume it.

Server:

private void start() {
        try {
            TSSLTransportFactory.TSSLTransportParameters params =
                    new TSSLTransportFactory.TSSLTransportParameters();
            params.setKeyStore("path to keystore.jks", "keystore.jks password");

            TServerSocket serverTransport = TSSLTransportFactory.getServerSocket(
                    7911, 10000, InetAddress.getByName("localhost"), params);
            ArithmeticService.Processor processor = new ArithmeticService.Processor(new ArithmeticServiceImpl());

            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).
                    processor(processor));
            System.out.println("Starting server on port 7911 ...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {

        }
}
    
Client:

private void invoke() {
        TTransport transport;
        try {

            TSSLTransportFactory.TSSLTransportParameters params =
                    new TSSLTransportFactory.TSSLTransportParameters();
            params.setTrustStore("path to truststore.jks", "truststore.jks password");

            transport = TSSLTransportFactory.getClientSocket("localhost", 7911, 10000, params);
            TProtocol protocol = new TBinaryProtocol(transport);

            ArithmeticService.Client client = new ArithmeticService.Client(protocol);

            long addResult = client.add(100, 200);
            System.out.println("Add result: " + addResult);
            long multiplyResult = client.multiply(20, 40);
            System.out.println("Multiply result: " + multiplyResult);

            transport.close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
}

