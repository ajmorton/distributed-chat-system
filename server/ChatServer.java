package server;
import java.net.*;
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

import com.beust.jcommander.JCommander;

import client.CmdLineArgs;

import java.io.*;

import commands.Join;
import commands.NewIdentity;

/** The main class for ChatServer
 * contains server information and allows clients to join 
 * then creates a thread for each connection
 */
public class ChatServer {
	
	private static final boolean USE_CONTEXT = false;
	
	public static void main (String args[]) throws UnrecoverableKeyException {
				
		try{
			
			CmdLineArgs settings = new CmdLineArgs();
			new JCommander(settings, args);
			
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
			
			// create the listenSocket
			ServerSocket listenSocket = factory.createServerSocket(settings.port);
			
			// create server info 
			ServerInfo sInfo = new ServerInfo();
			
			// create server cleaning thread
			ServerCleanUp sCleanUp = new ServerCleanUp(sInfo);
			
			// attach shutdown hook
			Runtime.getRuntime().addShutdownHook(new ShutdownHook(sInfo, sCleanUp, listenSocket));			
			
			
			
			// listen for new connections
			while(true) {
				Socket clientSocket = listenSocket.accept();
				
				String     newClientName = sInfo.getNewName();
				ClientInfo newClientInfo = new ClientInfo(sInfo);
				
				// create new client connection
				Connection c = new Connection(clientSocket, sInfo, newClientInfo);
				
				// set the new clients id and inform client
				NewIdentity newID = new NewIdentity(newClientName, c.getName());
				c.setName(newClientName);
				newID.sendJSON(c);

				// add client to clientList and move to MainHall
				sInfo.addClientList(c);
				(new Join("MainHall")).execute(c);
			} 
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} 
}