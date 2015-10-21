package commands;

import java.io.IOException;

import server.Connection;
import server.Room;
import server.ServerInfo;

/**
 * The Kick command
 * Used to kick a client from a room owned by the caller
 */
public class Kick extends Command{
	
	private 	 String roomid;			// the room banned from
	private 	 int 	time;			// the duration of the ban
	private 	 String identity;		// the name of the client banned
	final static int 	argCount = 3; 	// the number of arguments to supply to the command
	
	// CONSTRUCTOR
	public Kick(String[] values){
		// values[0] == roomid, values[1] == time, values[2] == identity
		this.type 	  = "kick";
		this.roomid   = values[1];				
		this.time	  = Integer.parseInt(values[2]);	
		this.identity = (values[0]);		
	}
	
	// GETTERS
	public int 	  getTime()	 	{return time;}
	public String getRoomID()	{return roomid;}
	
	
	/**
	 * Kicks a user from a room and doesn't allow rejoining for a set amount of time
	 * Only works if caller is the owner of the room
	 */
	public void execute(Connection c) throws IOException{

		
		
		ServerInfo sInfo   = c.getServerInfo();
		Room       banRoom = sInfo.getRoom(roomid);
		Connection banUser = sInfo.getClient(identity);

		String kickersName = c.getName();
		if(!sInfo.inAuthIndex(kickersName)){
			// user not authenticated, cannot kick other users
			(new ServerMessage("", c.getName())).sendJSON(c);
			return;
		}

		
		// if room exists
		Boolean roomExists = (banRoom != null); 
		
		// if user exists
		Boolean userExists = (banUser != null);
		
		if(!roomExists || !userExists){
			// either the room or the user to kick do not exist
			(new ServerMessage("", c.getName())).sendJSON(c);
			return;
		}
		
		// if caller is owner of room
		Boolean isOwner = banRoom.getRoomOwner().equals(c.getName());
		
		// if banned user is currently in room
		Boolean inRoom = banRoom.inRoom(identity);
		
		// if time is a valid integer
		Boolean timeValid = time >= 0;
		
		if(isOwner && inRoom && timeValid){
			// can ban user from room
			banUser.getClientInfo().addBan(roomid, time);

			// move kicked user to MainHall
			new Join("MainHall").execute(banUser);
						
		}
	}
}