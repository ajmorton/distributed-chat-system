package server;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.google.gson.Gson;

import commands.Ping;
import commands.RoomChange;

/**
 * Cleans up dropped connections with the server
 * catches abrupt disconnections
 */
public class ServerCleanUp extends Thread{
	
	private volatile ServerInfo sInfo;		// information about the server
	
	// CONSTRUCTOR
	public ServerCleanUp(ServerInfo sInfo){
		this.sInfo = sInfo;
		this.start();
	}

	/**
	 * for all connected clients check if the socket is broken
	 * if it is remove disconnect from server
	 */
	public void run(){
		
		while(true){
			
			// drop disconnected clients
			Vector<Connection> allConnections = sInfo.getAllClients();
			
			try {
				removeDroppedConnections(allConnections);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			// sleep half a second
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		
	}
	

	/**
	 * checks all connection for a broken socket
	 * if socket is broken remove client
	 * @param allConnections all client-server connections
	 */
	private void removeDroppedConnections(Vector<Connection> allConnections) throws IOException {
		
		Iterator<Connection> iter = allConnections.iterator();
		
		while(iter.hasNext()){
			Connection c = iter.next();
			Ping ping = new Ping();
			
			try{
				// send a ping message
				ping.sendPing(c);
				
			} catch (IOException e){
				// if socket is broken catch IOException and remove connection
				sInfo.freeGuest(c.getName());
				c.getClientInfo().getCurrRoom().remove(c);
				c.getClientInfo().updateOwnedRoom("");
	
				// check if dropped connections room can be removed
				removeEmptyRooms(c);				
				
				// inform others in room of removal
				Gson gson = new Gson();
				String clientName     = c.getName(),
					   currRoomName   = c.getClientInfo().getCurrRoomName();
				
				RoomChange rChange = new RoomChange(clientName, currRoomName, "");
				String 	   message = gson.toJson(rChange);
				
				Room currRoom = c.getClientInfo().getCurrRoom();
				currRoom.broadcast(message);
				
				// remove client from allClients
				iter.remove();
			}
			
		}
		
	}
	
	/**
	 * when a client is removed check if the removal allows 
	 * deletion of any rooms on server
	 * @param c the client to be removed
	 */
	private synchronized void removeEmptyRooms(Connection c){
		
		Room         room  = c.getClientInfo().getCurrRoom();
		Vector<Room> rList = c.getServerInfo().getRoomList();
		
		Boolean noOwner =  room.getRoomOwner().equals(""),
				notMain = !room.getName().equals(rList.get(0).getName()),
				empty   =  room.getSize() == 0;

		if(noOwner && notMain && empty){
			// if the vacated room can be deleted
			rList.remove(room);
		}
		
		// check if any of the dropped clients owned rooms can be removed
		for(Room ownedRoom : c.getClientInfo().getOwnedRooms()){	
			
			noOwner =  ownedRoom.getRoomOwner().equals("");
			notMain = !ownedRoom.getName().equals("MainHall");
			empty   =  ownedRoom.getSize() == 0;

			if(noOwner && notMain && empty){
				rList.remove(ownedRoom);
			}
		}
	}
	
	
	
}

