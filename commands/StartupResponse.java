package commands;

import java.io.IOException;

import server.Connection;
import server.ServerInfo;

public class StartupResponse extends Command
{
	private String username;
	private String hash;
	
	public StartupResponse(String username, String hash)
	{
		this.type = "startupresponse";
		this.username = username;
		this.hash = hash;
	}

	public void execute(Connection c) throws IOException
	{
		ServerInfo sInfo = c.getServerInfo();
		NewIdentity newID;
		String newClientName;
		
		System.out.println("StartupResponse begins");
		
		if (sInfo.tryExistingAuth(username, hash)) {
			newClientName = username;
		}
		else {
			newClientName = sInfo.getNewName();
		}

		// set the new clients id and inform client
		c.setName(newClientName);
		newID = new NewIdentity(newClientName, c.getName());
		newID.sendJSON(c);
		System.out.println("NewID sent.");

		// add client to clientList and move to MainHall
		(new Join("MainHall")).execute(c);
		System.out.println("StartupResponse ends");
		
		return;	
	}
}
