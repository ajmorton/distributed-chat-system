package server;

import java.util.Vector;

/**
 * Contains information about each client such as 
 * current room, room name, rooms owned by client 
 * and which rooms the client is banned from
 */
public class ClientInfo {
	
	String       	currRoomName;	// clients current room name
	Room         	currRoom;		// clients current room object
	Vector<Room> 	ownerOf;		// the list of rooms owned by the client
	BanList      	banList;		// the list of bans the client has
	boolean			isAuth;		// True if the user is authenticated	
	
	public ClientInfo(ServerInfo sInfo){
		currRoomName 	= "";
		currRoom     	= sInfo.getRoom(currRoomName);
		ownerOf      	= new Vector<Room>();
		banList      	= new BanList();
		isAuth			= false;
	}
	
	
	// GETTERS
	public Room 		getCurrRoom()	{return currRoom;}
	public Vector<Room> getOwnedRooms() {return ownerOf;}

	// SETTERS
	public void setCurrRoom(Room newRoom)	{currRoom = newRoom;}
	
	
	/**
	 * gets the current rooms name
	 * @return
	 */
	public String getCurrRoomName(){

		// when client has just joined currRoom is null
		if(currRoom == null){
			return "";
		}
		
		return currRoom.getName();
	}
	
	/**
	 * adds a room to the list of rooms owned by the client
	 * @param r the new room to add
	 */
	public void addOwnedRoom(Room r){
		ownerOf.add(r);
	}

	/**
	 * updates the owner string of the owned rooms
	 * @param newName the new name of the client
	 */
	public void updateOwnedRoom(String newName) {
		for(Room room: ownerOf){
			room.setRoomOwner(newName);
		}
	}

	/**
	 * add a ban to the client
	 * @param roomid   the room banned from
	 * @param duration how long (in seconds) the client is banned
	 */
	public void addBan(String roomid, int duration) {
		banList.addBan(roomid, duration);
	}

	/**
	 * checks if the user is banned from a room
	 * called before a join is executed
	 * @param roomid the name of the room
	 * @return if the user is banned as a bool
	 */
	public Boolean isBanned(String roomid) {
		return banList.isBanned(roomid);
	}
	
	public void makeAuth(String hash)
	{
		this.isAuth = true;
	}
	
	public boolean isAuthenticated()
	{
		return this.isAuth;
	}
}