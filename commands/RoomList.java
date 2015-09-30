package commands;

import java.util.Vector;

import client.ChatClient;

import com.google.gson.Gson;

import server.Room;

/**
 * The RoomList command
 * informs clients of what rooms exist on the server 
 * and how many clients are in each
 */
public class RoomList extends Command {
	
	private 	 String[] 	rooms;			// the list of rooms
	final static int 		argCount = 1;	// the number of arguments to supply to the command
	
	// CONSTRUCTOR
	public RoomList(Vector<Room> roomVec){
		this.type = "roomlist";
		this.rooms = new String[roomVec.size()];
		
		Gson   		gson     = new Gson();
		String 		roomid,
			   		json;
		int    		count;
		RoomDetails rDetails = new RoomDetails();
		
		int i = 0;
		for(Room room : roomVec){
			roomid   = room.getName();
			count    = room.getSize();
			
			rDetails.set(roomid, count);
			
			json       = gson.toJson(rDetails);
			rooms[i++] = json;
		}
	}
	
	// GETTER
	public String[] getRooms(){return rooms;}
	
	
	/** 
	 * Prints out a list of rooms on the server
	 */
	public void execute(ChatClient c){

		Boolean checkForNewRoom = false,
				roomMadeFlag    = false;
		String  roomCheckName   = c.getMakeRoomRequest();
		
		// if makeRoomRequest is null client is not checking that they 
		// have successfully created a room
		if(!c.getMakeRoomRequest().equals("")){
			checkForNewRoom = true;
			c.setRoomRequest("");
		}
			
		Gson        gson     = new Gson();
		String      json;
		RoomDetails rDetail;
		
		// print out the details of each room in roomList
		for(String room : rooms){
			json    = room;
			rDetail = gson.fromJson(json, RoomDetails.class);
			rDetail.execute(c);
			
			// check if the room is the one the client created
			if(rDetail.roomid.equals(roomCheckName)){
				roomMadeFlag = true;
			}
		}
		
		// if client is looking to see if a room was created
		if(checkForNewRoom){
			
			if(roomMadeFlag){
				System.out.println("Room " + roomCheckName + " created");
			} else{
				System.out.println("Room " + roomCheckName + " is invalid");
			}
			
		}
		
		c.printPrompt();
	}	
}


/**
 * Details of each room in RoomList
 */
class RoomDetails extends Command{
	
	String roomid;
	int    count;

	// COSNTRUCTOR
	public void set(String roomid, int count){
		this.roomid = roomid;
		this.count  = count;
	}	
	
	/**
	 * Print room details to stdout
	 */
	public void execute(ChatClient c){
		System.out.println(roomid + ": " + count);
	}
	
}












