package commands;

import java.io.IOException;

import com.google.gson.Gson;

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
	public Authenticate(String hash)
	{
		super("");
		this.type = "authenticate";
		this.hash = hash;
		this.identity = "";
	}
	
	/**
	 * Two argument constructor method for when new name is specified
	 */
	public Authenticate(String hash, String identity)
	{
		super(identity);
		this.type = "authenticate";
		this.hash = hash;
		this.identity = identity;
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
		
		// Client has specified a new identity
		//if(!identity.isEmpty()) {
		
		// Fail if new name doesn't match rules or if another user is logged in already
		if (!validRegexName(identity) || isConnectedName(identity, sInfo))
		{
			// TODO fail here
			return;
		}

		// If the name is recorded in the authentication index,
		// test the hash and log the user in if it matches
		if (isAuthName(identity, sInfo)) {
			if (!sInfo.tryExistingAuth(identity, hash)) {
				// TODO fail here
				return;
			}
			else {
				// TODO succeed here
				super.execute(c);
				return;
			}
		}
		// Otherwise the name doesn't exist,
		// so change the client's ID
		super.execute(c);
		
		/*}
		// If the user provides no new name but is already authenticated,
		// then they are dumb
		else if (cInfo.isAuthenticated()) {
			// TODO send the client a message that they are dumb
			return;
		}*/
		
		// Add the user to the authentication index
		sInfo.addAuthUser(identity, hash);
		// Mark the user's auth flag as true
		cInfo.makeAuth(hash);
		return;
	}
}
