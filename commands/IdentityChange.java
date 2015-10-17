package commands;

import java.io.IOException;
import java.util.Vector;

import com.google.gson.Gson;

import server.Connection;
import server.ServerInfo;

/**
 * The IdentityChange class
 * Changes a users name if the new name is valid
 */
public class IdentityChange extends Command
{
	final static int 	argCount = 1; 	// the number of arguments to supply to the command
	protected String 	identity;		// the new identity to change to
	
	
	// CONSTRUCTOR
	public IdentityChange(String identity){
		this.identity = identity;
		this.type = "identitychange";
	}
	
	// GETTER
	public String getIdentity() {return identity;}
	
	/**
	 * Changes a clients name on the server if the new name is valid
	 */
	public void execute(Connection c) throws IOException{
		
		String oldName   = c.getName();
		String newName   = identity;
		
		ServerInfo sInfo = c.getServerInfo();
		
		NewIdentity newID; 			//the newIdentity message
		Gson gson = new Gson();
		String json;

		if(validName(newName, c.getServerInfo())){
			
			newID = new NewIdentity(newName, oldName);
			json  = gson.toJson(newID);
			c.setName(newName);
			
			// update owner of rooms owned by client
			c.getClientInfo().updateOwnedRoom(newName);
			
			// free up previous guestName if it was in use
			sInfo.freeGuest(oldName);
			
			// broadcast the name change to all clients
			c.getServerInfo().broadcast(json);
						
		}else{
			// name not updated
			newID = new NewIdentity(oldName, oldName);
			newID.sendJSON(c);
		}
	}
	
	
	/**
	 * Checks if the new name is valid.
	 * To be valid it must not be in use, alphanumeric, begin with a letter, 
	 * and between 6 and 16 characters long
	 * @param newName the potential new new
	 * @param sInfo   server information
	 * @return
	 */
	private Boolean validName(String newName, ServerInfo sInfo){
		
		// check that name is not already in use
		Vector<Connection> allClients = sInfo.getAllClients();
		for(int i = 0; i < allClients.size(); i++){
			if(allClients.get(i).getName().equals(newName)){
				return false;
			}
		}
		
		// is the name the right length
		Boolean rightLength = (newName.length() >= 3) && (newName.length() <= 16);
		if(rightLength){
			
			// is the name alphanumeric
			Boolean alphanumeric = newName.matches("[A-Za-z0-9]+");
			if(alphanumeric){
				
				// is the first character a letter
				Boolean firstIsAlpha = newName.substring(0,1).matches("[A-Za-z]");
				if(firstIsAlpha){
					return true;
				}
			}
		}
	
		return false;
	}
	
	
}