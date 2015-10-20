package commands;

import java.util.Vector;

import server.Connection;
import client.ChatClient;

/**
 * The RoomContents Command
 * sent by the server to describe which clients are in a room
 */
public class RoomContents extends Command{
	
	private 	 String[] 	identities; 	// the list of clients in the room
	private 	 String  	owner,			// the owner of the room
							roomid;			// the name of the room
	final static int 		argCount = 3;	// the number of arguments to supply to the command
	
	// CONSTRUCTOR
	public RoomContents(String roomid, Vector<Connection> inRoom, String owner){
		this.roomid     = roomid;
		this.owner      = owner;
		this.type       = "roomcontents";
		
		
		String[] inRoomStr = new String[inRoom.size()];

		// build list of clients in room
		for(int i = 0; i < inRoomStr.length ; i++){
			inRoomStr[i] = inRoom.elementAt(i).getName();
		}
		
		this.identities = inRoomStr;
		
		
	}
	
	// GETTERS
	public String   getOwner()		 {return owner;}
	public String[] getIdentitites() {return identities;}

	
	/**
	 * Prints out all clients in the room
	 */
	public void execute(ChatClient c){
		
		System.out.print("\r" + roomid + " contains");
		
		for(String client: identities){	
			System.out.print(" " + client);

			if(client.equals(owner)){
				System.out.print("*");
			}
		}
		
		System.out.println();
		
		// small workaround to prevent prompt only on initial roomlist
		if(!owner.equals("noPrompt")){
			c.printPrompt();			
		}
	}	
}