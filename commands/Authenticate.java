package commands;

import java.io.IOException;

import com.google.gson.Gson;

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
	public void execute(Connection c) throws IOException {
		if (identity != "") {
			super.execute(c);
		}
	}
}
