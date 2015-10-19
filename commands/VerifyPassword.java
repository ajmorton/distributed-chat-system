package commands;

import java.io.IOException;

import server.Connection;

public class VerifyPassword extends Command{
	
	static int argCount = 0;	// the number of arguments to supply to the command
	String name;
	String hash;
	
	// CONSTRUCTOR
	public VerifyPassword(String name, String hash){
		this.type = "verify";
		this.hash = hash;
		this.name = name;
	}
	
	/**
	 * removes the client from the server and terminates the 
	 * connection on the server side
	 */
	public void execute(Connection c) throws IOException{
		
		System.out.println(c.getServerInfo().verifypass(name, hash));
		
	}
	
}