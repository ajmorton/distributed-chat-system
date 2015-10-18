package commands;

import java.io.IOException;
import java.util.Vector;

import server.Connection;
import server.ServerInfo;
import server.Room;

/**
 * The CreateRoom command object
 * Used to create rooms on the server
 */
public class CreateRoom extends Command{
	final static int 	argCount = 1;	// the number of arguments to supply to the command
	private 	 String roomid;			// the name of the room to create
		
	// CONSTRUCTOR
	public CreateRoom(String roomid){
		this.roomid = roomid;
		this.type = "createroom";
	}
	
	/**
	 * Creates a room on the server provided that it is valid
	 */
	public void execute(Connection c) throws IOException{
		
		ServerInfo sInfo = c.getServerInfo();
		Vector<Room> rList = sInfo.getRoomList(); 
		
		String ownerName = c.getName();
		
		if(validName(roomid, sInfo)){
			sInfo.addRoom(roomid, ownerName);
		}
		
		Vector<Room> ownedRooms = c.getClientInfo().getOwnedRooms();
		Room newRoom = sInfo.getRoom(roomid);
		ownedRooms.add(newRoom);
		
		RoomList roomList = new RoomList(rList);
		roomList.sendJSON(c);
		
	}
	
	/**
	 * Determines if a roomName is valid
	 * a valid name must not be present already, alphanumeric, 
	 * start with a letter, and be between 3 and 32 characters long 
	 * @param newName the potential new room name
	 * @param sInfo	  information about the server
	 * @return Boolean, true if the name is valid false if not
	 */
	private Boolean validName(String newName, ServerInfo sInfo){
		// name is valid if it is alphanumeric
		// starting with a character
		// 3 >= length >= 16
		
		// if room already exists
		Vector<Room> allRooms = sInfo.getRoomList();
		for(Room room : allRooms){
			if(room.getName().equals(newName)){
				return false;
			}
		}
		return newName.matches("[A-Za-z][A-Za-z0-9]{2,32}");
	}
}

