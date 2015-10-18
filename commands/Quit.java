package commands;

import java.io.IOException;
import java.util.Vector;

import com.google.gson.Gson;

import server.Connection;
import server.Room;
import server.ServerInfo;

/**
 * The Quit Command
 * Used to signal a client is quitting
 */
public class Quit extends Command{
	
	static int argCount = 0;	// the number of arguments to supply to the command

	// CONSTRUCTOR
	public Quit(){
		this.type = "quit";
	}
	
	/**
	 * removes the client from the server and terminates the 
	 * connection on the server side
	 */
	public void execute(Connection c) throws IOException{
		ServerInfo sInfo = c.getServerInfo();
		
		// remove client from room and allClients
		sInfo.getAllClients().remove(c);
		c.getClientInfo().getCurrRoom().remove(c);
				
		
		// inform all in room of departure
		Room   currRoom = c.getClientInfo().getCurrRoom();
		String oldRoom  = currRoom.getName();
		
		Gson       gson       = new Gson();
		RoomChange roomChange = new RoomChange(c.getName(), oldRoom, "");
		String     json       = gson.toJson(roomChange);
		
		currRoom.broadcast(json);
		
		
		// if leaving the room allows for its deletion
		Room         room  = c.getClientInfo().getCurrRoom();
		Vector<Room> rList = c.getServerInfo().getRoomList();
		
		boolean noOwner 		= room.getRoomOwner().isEmpty();
		boolean AndNotMain 		= noOwner && !room.getName().equals(rList.get(0).getName());
		boolean canBeDeleted 	= AndNotMain && room.getSize() == 0;

		if(canBeDeleted){
			rList.remove(room);
		}

		// set owned rooms to owner null and if possible delete them
		if (!c.getClientInfo().isAuthenticated()) {
			Vector<Room> ownedRooms = c.getClientInfo().getOwnedRooms();
			for(Room owned: ownedRooms){
				owned.setRoomOwner("");

				noOwner 		= owned.getRoomOwner().equals("");
				AndNotMain 		= noOwner && !owned.getName().equals("MainHall");
				canBeDeleted   	= canBeDeleted && owned.getSize() == 0;

				if(canBeDeleted){
					rList.remove(owned);
				}
			}
		}
		
		
		// free up guest number		
		sInfo.freeGuest(c.getName());
		
		// terminate connection
		c.terminate();
		
	}
	
}