package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import commands.Quit;


/**
 * The Room class
 * contains information about independent rooms
 * such as the list of clients in the room and the rooms name
 */
public class Room{
	
	private          String roomName;					// the name  of the room
	private volatile String roomOwner;					// the owner of the room
	private volatile Vector<Connection> roomClients;	// the list of clients in the room
	
	// CONSTRUCTOR
	public Room(String roomName, String roomOwner){
		this.roomName    = roomName;
		this.roomOwner   = roomOwner;
		this.roomClients = new Vector<Connection>();
	}
	
	// GETTERS
	public 				Vector<Connection>    getRoomClients()	{return roomClients;}
	public 				String 			   	  getName() 		{return roomName;}
	public synchronized String 				  getRoomOwner() 	{return roomOwner;}
	public 				int 				  getSize()			{return roomClients.size();}
	
	// SETTERS
	public synchronized void setRoomOwner(String newOwner) {roomOwner = newOwner;}
	
	
	/** 
	 * broadcasts a message to all clients in the room
	 * @param message the JSON String message
	 */
	public void broadcast(String message) throws IOException{

		ArrayList<Connection> toRemove = new ArrayList<Connection>();
		
		Iterator<Connection> i = roomClients.iterator();
		Connection conn;
		
		while(i.hasNext()){
			conn = i.next();
			
			if(conn.sendMessage(message)){ // if send message fails remove the connection
				i.remove();
				toRemove.add(conn);
			}	
		}
		
		// remove connections marked as dropped during broadcast
		
		Quit quit = new Quit();
		
		for(Connection droppedConn: toRemove){
			quit.execute(droppedConn);
		}
	}
	
	/**
	 * add a client to the room
	 * @param c the new client
	 */
	public void addClient(Connection c){
		roomClients.add(c);
	}
	
	/**
	 * remove a client from the room
	 * @param c the client
	 */
	public synchronized void remove(Connection c){
		roomClients.remove(c);
	}
	
	/** checks if a client is in the room
	 * @param identity the name of the client
	 * @return true if the client is in the room
	 */
	public Boolean inRoom(String identity) {

		for(Connection c: roomClients){
			if(c.getName().equals(identity)){
				return true;
			}
		}
		
		return false;
		
	}
}
