package server;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * BanList
 * Tracks all rooms the client is banned from
 */
public class BanList {
	
	Vector<Ban> banList;	// the list of band the client has
	
	// CONSTRUCTOR
	public BanList(){
		banList = new Vector<Ban>();
	}
	
	/**
	 * adds a ban to the client
	 * @param roomName the name of the room banned from
	 * @param duration the duration in seconds of the ban
	 */
	public void addBan(String roomName, int duration){
		banList.add(new Ban(roomName, duration));
	}
	
	/** 
	 * checks to see if client is banned from the room
	 * called before client joins a room
	 * 
	 * Also cleans BanList of expired bans if found
	 * 
	 * @param roomName the room to check
	 * @return true is banned false if not
	 */
	public Boolean isBanned(String roomName){
		Boolean isBanned = false;
		
		Iterator<Ban> iter = banList.iterator();
		Ban ban;
		
		while(iter.hasNext()){
			ban = iter.next();
			if(ban.getRoomName().equals(roomName)){
				// if there is a ban on the room to join set isBanned to the value of that ban
				isBanned = ban.isBanned();
			}
			
			// if an expired ban is found remove from banList
			if(ban.isBanned() == false){
				iter.remove();
			}
			
		}
		
		return isBanned;
	}

	
}


/**
 * The Ban class, used to store bans 
 */
class Ban{
	private Boolean isBanned;	// is the client banned from roomName
	private String  roomName;	// the name of the room client is banned from
	private Timer   timer;		// the timer that removes the ban after a duration
	
	// CONSTRUCTOR
	public Ban(String roomName, int duration) {
		this.isBanned = true;
		this.roomName = roomName;
		this.timer    = new Timer();
		timer.schedule(new BanTask(this), duration*1000);
	}

	// GETTER
	public String getRoomName()	{return roomName;}
	
	/**
	 * returns a boolean if the ban is still true
	 */
	public Boolean isBanned(){
		return isBanned;
	}
	
	/** 
	 * changes the value of a ban.
	 * used to set ban to false after ban duration expires
	 * @param bool if the ban is true or false
	 */
	public void setIsBanned(Boolean bool){
		this.isBanned = bool;
	}
	
	
	
}

/**
 * BanTask
 * used to set a ban to false after ban duration expires
 */
class BanTask extends TimerTask{
	
	Ban ban;	// the to set to false ban
	
	public BanTask(Ban ban){
		this.ban = ban;
	}
	
	@Override
	public void run() {
		// after ban duration set ban to false
		ban.setIsBanned(false);
	}
	
}




