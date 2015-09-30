package server;
import java.net.*;
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
			
			// the default server port
			int serverPort = 4444; 
			
			// if there is an additional argument in from the terminal
			if(args.length > 0){
				serverPort = Integer.parseInt(args[0]);
			}
			
			// create the listenSocket
			ServerSocket listenSocket = new ServerSocket(serverPort);
			
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
		
		catch(IOException e){
		}
		
		
	} 
}