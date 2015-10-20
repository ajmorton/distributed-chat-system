package commands;

import java.io.IOException;

import server.ClientInfo;
import server.Connection;
import server.ServerInfo;

/**
 * Authenticate changes a user to an authenticated user, optionally
 * changing their username.
 * 
 * It takes a password to be (hashed and) kept for reference by the server.
 * It also stores a local copy of the username and password so the client
 * can automatically reconnect as an authenticated user.
 * 
 * @author rob
 *
 */
public class Authenticate extends IdentityChange
{
	final static int NUM_ARGS = 2;
	private String hash;
	
	/**
	 * One argument constructor method for when user keeps current identity
	 */
	/*
	public Authenticate(String hash)
	{
		super("");
		this.type = "authenticate";
		this.hash = hash;
		this.identity = "";
	}
	*/
	
	/**
	 * Two argument constructor method for when new name is specified
	 */
	public Authenticate(String hash, String identity)
	{
		super(identity);
		this.type = "authenticate";
		this.hash = hash;
	}

	/**
	 * Change the client to an authenticated client.
	 * Change their name if they entered one.
	 */
	@Override
	public void execute(Connection c) throws IOException
	{
		ClientInfo cInfo = c.getClientInfo();
		ServerInfo sInfo = c.getServerInfo();
		String currName = c.getName();
			
		// If the user is already authenticated,
		// then they are dumb
		if (cInfo.isAuthenticated()) {
			// TODO send the client a message that they are dumb
			(new AuthResponse("Already authenticated.\n"
					+"Use '#identitychange' to change username.", false)).sendJSON(c);
			return;
		}
		
		// If name fails regex
		boolean isInvalidNewID = !validRegexName(identity);
		// Or it's already in use (not by current user)
		isInvalidNewID |= !currName.equals(identity) && nameAlreadyExists(identity, sInfo);
		
		// Then reject it
		if (isInvalidNewID) {
			(new AuthResponse("Invalid username. You may want #login.", false)).sendJSON(c);;
			return;
		}
		
		// Also disallow authenticating guest names
		if (isGuestName(identity)) {
			(new AuthResponse("You may not authenticate as a guest.\nPick another name.", false)).sendJSON(c);
			return;
		}
		
		// Otherwise the name doesn't exist,
				
		// Add the user to the authentication index
		sInfo.addAuthUser(identity, hash);
		// Mark the user's auth flag as true
		cInfo.makeAuth(hash);
		
		// Tell the client the new identity is authenticated
		(new AuthResponse(identity, true)).sendJSON(c);
		
		// Finally, change the user's name if it's different
		if (!currName.equals(identity)) {
			changeID(c, sInfo, currName, identity);
		}

		return;
	}
}
