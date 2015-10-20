package commands;

import java.io.IOException;

import server.Connection;
import server.ServerInfo;

public class Login extends IdentityChange
{
	private String hash;
	
	public Login(String hash, String identity)
	{
		super(identity);
		this.type = "login";
		this.hash = hash;
	}
	
	@Override
	public void execute(Connection c) throws IOException
	{
		ServerInfo sInfo = c.getServerInfo();
		String currName = c.getName();
		
		if (isAuthName(identity, sInfo) && !isConnectedName(identity, sInfo)) {
			if (sInfo.tryExistingAuth(identity, hash)) {
				(new AuthResponse("", true)).sendJSON(c);
				changeID(c, sInfo, currName, identity);
				return;
			}
		}
		(new AuthResponse("Incorrect Username or Password", false)).sendJSON(c);
		return;
	}
}
