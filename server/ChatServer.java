package server;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
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
		
	
	public static void main (String args[]) {
				
		try{
			
			CmdLineArgs settings = new CmdLineArgs();
			new JCommander(settings, args);
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream keystoreStream = ClassLoader.getSystemResourceAsStream("/resc/distribCert.jks");
			keystore.load(keystoreStream, "COMP90015@unimelb".toCharArray());
			trustManagerFactory.init(keystore);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			SSLContext ctx = SSLContext.getInstance("SSL");
			ctx.init(null, trustManagers, null);
			SSLContext.setDefault(ctx); 
			
			//SSLContext ctx = SSLContext.getInstance("TLS");
			
			//ctx.init(null, new ChatTrustManager[]{new ChatTrustManager()}, null);
			
			ServerSocketFactory factory = ctx.getServerSocketFactory();
			
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