package server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import common.PasswordHash;

/**
 * Server Information class
 * contains information about all clients, 
 * all rooms and which guestNames are available
 */
public class ServerInfo
{
	private volatile Vector<Room>        		roomList;		// list of rooms on the server
	private volatile Vector<Connection>  		clientList;		// list of clients on the server
	private FreeGuestNumbers			 		freeGuestNums;
	private ConcurrentHashMap<String,String> 	authUsers;		// List of authenticated users
	
	// CONSTRUCTOR
	public ServerInfo()
	{
		this.roomList       = new Vector<Room>();
		this.clientList     = new Vector<Connection>();
		this.freeGuestNums 	= new FreeGuestNumbers(this);
		this.authUsers 		= new ConcurrentHashMap<>();
		roomList.add(new Room("MainHall", ""));
	}
	
	
	// GETTERS
	public synchronized Vector<Room> 		getRoomList()	{return roomList;}
	public synchronized Vector<Connection> 	getAllClients() {return clientList;}
	
	
	
	/**
	 * generates a name for a new connecting client
	 * calls getNewName() from FreeGuestNumbers
	 * @return
	 */
	public synchronized String getNewName(){
		return freeGuestNums.getNewName();
	}
	
	
	/**
	 * removes a client from the server
	 * @param c the client to remove
	 */
	public synchronized void removeClient(Connection c){
		// remove from clientList
		clientList.remove(c);
		
		// remove from room client is in
		String roomName = c.getClientInfo().getCurrRoomName();
		Room currRoom = getRoom(roomName);//
		currRoom.remove(c);
		
		// free up guestName if applicable
		freeGuestNums.freeGuest(c.getName());
	}
	
	/**
	 * returns the room object if it exists
	 * @param searchName the string of the roomName to search for
	 * @return the room object if it exists and null if it doesn't
	 */
	public synchronized Room getRoom(String searchName){

		// search all rooms for the room with the correct name
		for(Room room : roomList){
			if(room.getName().equals(searchName)){
				return room;
			}
		}
	
		// room not found
		return null;
	}
	
	/**
	 * add a room to the list of all rooms if new name is valid
	 * @param roomName the name of the new room
	 * @param ownerName the name of the new room owner
	 */
	public synchronized void addRoom(String roomName, String ownerName){
		roomList.add(new Room(roomName,ownerName));
	}

	/**
	 * moves a client from one room to another
	 * @param c the client
	 * @param newRoom the new room to move to
	 * @param oldRoom the old room to move from
	 */
	public synchronized void swapRoom(Connection c, Room newRoom, Room oldRoom){
		newRoom.addClient(c);
		
		// only remove client from old room if client is not moving 
		// from null (on connection)
		if(oldRoom != null){
			oldRoom.remove(c);
		}
	}
	
	/**
	 * sends a JSON string to all clients on the server
	 * @param message the JSON String
	 */
	public synchronized void broadcast(String message) throws IOException{
		
		for(Connection c : clientList){
			c.send(message);
		}
	}
	
	/**
	 * remove a room from the list of a rooms
	 * @param delRoom the room to remove
	 */
	public synchronized void removeRoom(Room delRoom) {
		roomList.remove(delRoom);
	}

	/**
	 * add a client to the list of all clients
	 * @param c the client to add
	 */
	public void addClientList(Connection c) {
		clientList.add(c);
		
	}

	/**
	 * find a client on the server
	 * @param identity the string name of the client
	 * @return the client object
	 */
	public Connection getClient(String identity) {
		for(Connection c : clientList){
			if(c.getName().equals(identity)){
				return c;
			}
		}
		// else client does not exist
		return null;
	}
	
	/**
	 * calls the freeGuest function in FreeGuestNumbers
	 * @param name the name to potentially free
	 */
	public void freeGuest(String name){
		freeGuestNums.freeGuest(name);
	}

	public void addAuthUser(String name, String hash)
	{
		try {
			authUsers.put(name, PasswordHash.createHash(hash));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
	
	public boolean tryExistingAuth(String name, String hash)
	{
		return inAuthIndex(name) && matchesPassword(name, hash);
	}
	
	public boolean inAuthIndex(String name)
	{
		return authUsers.containsKey(name);
	}
	
	private boolean matchesPassword(String name, String hash)
	{
		try {
			return PasswordHash.validatePassword(hash, authUsers.get(name));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return false;
	}

}

