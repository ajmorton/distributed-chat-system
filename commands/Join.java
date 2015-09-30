package commands;

import java.io.IOException;

import com.google.gson.Gson;

import server.Connection;
import server.Room;
import server.ServerInfo;

/**
 * The Join Object
 * Used to move a client to a new room
 */
public class Join extends Command{
	final static int 	argCount = 1;	// the number of arguments to supply to the command
	private		 String roomid;			// the name of the room to move to
	
	// CONSTRUCTOR 
	public Join(String roomid){
		this.roomid = roomid;
		this.type = "join";
	}
	
	/**
	 * Moves the client to the new room if the new room is valid
	 */
	public void execute(Connection c) throws IOException{
		
		String oldRoomStr = c.getClientInfo().getCurrRoomName();
		String newRoomStr = roomid;
		
		Room newRoom = c.getServerInfo().getRoom(newRoomStr);
		Room oldRoom = c.getServerInfo().getRoom(oldRoomStr);
						
		Boolean noRoom = (newRoom == null),
				banned = c.getClientInfo().isBanned(roomid);
		
		if(noRoom || banned){
			// room can't be moved to
			String 	   user 	  = c.getName();
			RoomChange roomChange = new RoomChange(user, oldRoomStr, oldRoomStr);
			roomChange.sendJSON(c);
			return;
		}
		
		// else can change to newRoom
		
		// change the clients current room
		ServerInfo sInfo = c.getServerInfo();
		sInfo.swapRoom(c, newRoom, oldRoom);
				
		c.getClientInfo().setCurrRoom(newRoom);
		
		// broadcast the room change to all clients in the new and old rooms
		String user = c.getName();
		
		Gson gson = new Gson();
		RoomChange roomChange = new RoomChange(user, oldRoomStr, newRoomStr);
		String json = gson.toJson(roomChange);
		
		if(oldRoom != null){
			oldRoom.broadcast(json);
		}
		
		newRoom.broadcast(json);
		
		// if moving to MainHall also send a roomContents and 
		// roomList message to the client
		if(roomid.equals("MainHall")){
			Room mainHall = sInfo.getRoom("MainHall");
			
			RoomContents roomContents = new RoomContents("MainHall", mainHall.getRoomClients(), "noPrompt");
			roomContents.sendJSON(c);
			
			RoomList rList = new RoomList(c.getServerInfo().getRoomList());
			rList.sendJSON(c);
		}
		
		
		// upon move check if the room that was left can be deleted by the server

		// old room not MainHall
		Boolean notMain = !oldRoomStr.equals("MainHall");
		if(notMain){
			
			// the room exists (not moving from "" at start of connection)
			Boolean	roomExists = (c.getServerInfo().getRoom(oldRoomStr) != null);
			if(roomExists){
				
				// room has no owner
				Boolean	noOwner =  oldRoom.getRoomOwner().equals("");
				if(noOwner){
					// if true the room can be deleted from the server
					c.getServerInfo().getRoomList().remove(oldRoom);	
				}
			}
		}		
	}
}