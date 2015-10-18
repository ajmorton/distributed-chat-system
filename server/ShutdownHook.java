package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

import com.google.gson.Gson;

import commands.RoomChange;

/** 
 * Shutdown hook for the server
 * if the server receives a ctrl+c input then close all 
 * threads and connections before terminating
 */
public class ShutdownHook extends Thread{
	ServerInfo    sInfo;		// information about the server
	ServerSocket  sSocket;		// the servers socket to detect new connections
		
	// CONSTRUCTOR
	public ShutdownHook(ServerInfo sInfo, /*ServerCleanUp sCleanup,*/ ServerSocket listenSocket){
		this.sInfo    = sInfo;
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
			
			Gson gson = new Gson();
			
			// sends a message to each client to quit them		
			for(Connection c: allConns){
				
				String     oldRoom    = c.getClientInfo().getCurrRoom().getName();
				RoomChange roomChange = new RoomChange(c.getName(), oldRoom, "");
				String     json       = gson.toJson(roomChange);

				c.sendMessage(json);
				c.terminate();
			}
			
			
		
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	
}