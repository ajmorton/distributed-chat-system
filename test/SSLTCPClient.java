package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * See the comments for the server!
 * 
 * @author Nikolay
 * @author Adel
 *
 */
public class SSLTCPClient {
	
	private static final boolean USE_CONTEXT = true;

	public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException {
		// You can hardcode the values of the JVM variables as follows:
		//System.setProperty("javax.net.ssl.trustStore", "<path-to-certificate>");
		//System.setProperty("javax.net.ssl.trustStorePassword","<password>");
		
		SocketFactory factory = null;
		
		if (USE_CONTEXT) {
			// Create a trust manager factory to process the keystore later
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			// Create a default keystore instance to take the certificate
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			// Create a stream from the certificate using the class loader
			InputStream keystoreStream = ClassLoader.getSystemResourceAsStream("resc/distribCert.jks");
			// Load the keystore with the password
			keystore.load(keystoreStream, "aaaaaa".toCharArray());
			// Set the trust manager to use the keystore
			trustManagerFactory.init(keystore);
			// Get the new trust manager
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			// Create a corresponding SSL context
			SSLContext ctx = SSLContext.getInstance("TLS");
			// Set the context to use the trust manager
			ctx.init(null, trustManagers, null);
			// Set the new context as the default
			SSLContext.setDefault(ctx);
			// Create a SocketFactory using our certificate
			factory = ctx.getSocketFactory();
		}
		else {
			System.setProperty("javax.net.ssl.trustStore", "/Users/rob/Desktop/distribCert.jks");
			System.setProperty("javax.net.ssl.trustStorePassword", "aaaaaa");
			factory = SSLSocketFactory.getDefault();
		}
		
		// Use the factory to instantiate SSLSocket
		try(Socket socket = factory.createSocket("localhost", 8081)) {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			out.writeUTF("Hello Server");
			out.flush();
			String msg = in.readUTF();
			System.out.println(msg);
		}
	}
}
