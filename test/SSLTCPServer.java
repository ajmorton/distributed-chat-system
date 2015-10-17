package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * First we need to make a certificate. We use keytool, which is 
 * part of J2SE SDK. If it is not on your PATH, you can find the executable in
 * the bin folder of your java installation - i.e. $JAVA_HOME/bin or %JAVA_HOME%\bin.
 * 
 * The program will ask for certificate owner information and passwords:
 * 
 * 		keytool -genkey -keystore mySrvKeystore -keyalg RSA
 * 
 * After this command you will have certificate file in the working directory.
 * 
 * You can now run the server and the client with these parameters:
 * 
 * for Server: java -jar -Djavax.net.ssl.keyStore=[path to mySrvKeystore] -Djavax.net.ssl.keyStorePassword=[password] server.jar 
 * for client: java -jar -Djavax.net.ssl.trustStore=[path to mySrvKeystore] -Djavax.net.ssl.trustStorePassword=[password] client.jar
 * 
 * Alternatively, you can hardcode the values in you java code, as demonstrated by
 * the comments in the client and server.
 * 
 * @author Nikolay
 * @author Adel
 *
 */
public class SSLTCPServer {
	
	private static final boolean USE_CONTEXT = true;

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException, UnrecoverableKeyException {
		// You can hardcode the values of the JVM variables as follows:  
		//System.setProperty("javax.net.ssl.keyStore","<path-to-certificate>");
		//System.setProperty("javax.net.ssl.keyStorePassword","password");
		
		ServerSocketFactory factory = null;
		
		// Use the class loader to load the certificate as a resource
		if (USE_CONTEXT) {
			// Create a trust manager factory to process the keystore later
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			// Create a key manager factory to do the same.
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			// Create a default keystore instance to take the certificate
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			// Create a stream from the certificate using the class loader
			InputStream keystoreStream = ClassLoader.getSystemResourceAsStream("resc/distribCert.jks");
			// Load the keystore with the password
			keystore.load(keystoreStream, "aaaaaa".toCharArray());
			// Set the trust manager to use the keystore
			trustManagerFactory.init(keystore);
			// Set the key manager to the same
			keyManagerFactory.init(keystore, "aaaaaa".toCharArray());
			// Get the new trust manager
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			// Get the new key manager
			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
			// Create a corresponding SSL context
			SSLContext ctx = SSLContext.getInstance("TLS");
			// Set the context to use the trust manager
			ctx.init(keyManagers, trustManagers, null);
			// Set the new context as the default
			SSLContext.setDefault(ctx); 
			// Make a ServerSocketFactory that uses the internal certificate
			factory = ctx.getServerSocketFactory();
		}
		// Load the certificate from an external file in the current directory
		else {
			System.setProperty("javax.net.ssl.keyStore", "/Users/rob/Desktop/distribCert.jks");
			System.setProperty("javax.net.ssl.keyStorePassword", "aaaaaa");
			factory = SSLServerSocketFactory.getDefault();
		}
		
		
		// Use the factory to instantiate SSLServerSocket
		try(ServerSocket server = factory.createServerSocket(8081)) {
			Socket socket = server.accept();
			
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			String msg = in.readUTF();
			System.out.println("Received: " + msg);
			out.writeUTF("Received: " + msg);
			out.flush();
		}
	}
}
