package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

import commands.RoomChange;

/** 
 * Shutdown hook for the server
 * if the server receives a ctrl+c input then close all 
 * threads and connections before terminating
 */
public class ShutdownHook extends Thread{
	ServerInfo    sInfo;		// information about the server
	ServerCleanUp sCleanup;		// the server cleaning thread
	ServerSocket  sSocket;		// the servers socket to detect new connections
		
	// CONSTRUCTOR
	public ShutdownHook(ServerInfo sInfo, ServerCleanUp sCleanup, ServerSocket listenSocket){
		this.sInfo    = sInfo;
		this.sCleanup = sCleanup;
		this.sSocket  = listenSocket;
	}
	
	/**
	 * called before server termination
	 * closes all connections and terminates all threads
	 */
	public void run(){
		
		Vector<Connection> allConns = sInfo.getAllClients();
		
		try {
			// close the server socket
			sSocket.close();
			
			// send a quit message to all clients and close connection
			for(Connection c: allConns){
				RoomChange rChange = new RoomChange(c.getName(), c.getClientInfo().getCurrRoomName(), "");
				rChange.sendJSON(c);
				c.terminate();
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// terminate cleaning thread		
		sCleanup.interrupt();
		
	}
	
	
}