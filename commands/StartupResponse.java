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
		
		
		if ((!isConnectedName(username, sInfo)) && sInfo.tryExistingAuth(username, hash)) {
			newClientName = username;
		}
		else {
			newClientName = sInfo.getNewName();
			(new AuthResponse("You have failed me for the last time!", false)).sendJSON(c);
		}

		// set the new clients id and inform client
		c.setName(newClientName);
		newID = new NewIdentity(newClientName, "");
		newID.sendJSON(c);

		// add client to clientList and move to MainHall
		(new Join("MainHall")).execute(c);
		
		return;	
	}
	
	protected boolean isConnectedName(String name, ServerInfo sInfo)
	{
		for (Connection conn: sInfo.getAllClients()) {
			if (conn.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	
}
