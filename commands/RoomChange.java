package commands;

import client.ChatClient;

/**
 * The RoomChange Command
 * informs clients when a client moves to a different room
 */
public class RoomChange extends Command {
	
	private String roomid,		// the name of the room moved to
				   identity,	// the name of the client moving
				   former;		// the name of the clients old room
	static int argCount = 3;	// the number of arguments to supply to the command
	
	// CONSTRUCTOR
	public RoomChange(String identity, String former, String roomid){
		this.identity = identity;
		this.former = former;
		this.roomid = roomid;
		this.type = "roomchange";
	}
	
	// GETTER
	public String getRoomID(){return roomid;}
	
	/**
	 * responds to a servers changeRoom command
	 * either terminating the client
	 * informing it of a failed room change or
	 * successful room changes by them or another client
	 */
	

	public void execute(ChatClient c){	
		
		// if this client has moved to "" then they can terminate the connection
		if ( identity.equals(c.getClientName()) && roomid.equals("")){
			c.quitClient();
			return;
		} 
		
		
		
		if(identity.equals(c.getClientName())){
			// if this client has moved update current room
			c.setRoom(roomid);
		} else {
			// if another client has moved print \n for formatting
			System.out.println();
		}
		
		
		
		// print out the result of the roomChange
		if(former.equals(roomid)){
			// room is unchanged
			System.out.println("The requested room is invalid or non existent");
		} else {
			// a client has moved rooms
			System.out.println(identity + " moved from " + former + " to " + roomid);
		}
		
		
		
		if(!identity.equals(c.getClientName())){
			// if the moving client is not this client then don't update 
			// this clients current room
			c.printPrompt();
		} else if(former.equals("") || roomid.equals("MainHall")){
			// suppress prompt if this client just joined server or is moving to mainHall
		} else {
			c.printPrompt();
		}
	}
}