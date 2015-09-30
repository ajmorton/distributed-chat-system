package commands;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.Gson;

import server.Connection;
import client.ChatClient;

/**
 * The Ping class
 * No operation, used by the server to catch dropped connections
 */
public class Ping extends Command{
	
	
	// CONSTRUCTOR
	public Ping(){
		this.type = "ping";
	}
	
	/**
	 * No operation
	 */
	public void execute(ChatClient c){
		// do nothing. only used to check connection on server end
	}

	/**
	 * Sends a ping to the client from the server
	 * @param c the connection to the client
	 * @throws IOException throws if the socket is broken, indicating a dropped connection
	 */
	public void sendPing(Connection c) throws IOException{
		DataOutputStream out = new DataOutputStream(c.getSocket().getOutputStream());
		
		Gson gson = new Gson();
		String json = gson.toJson(this);
		
		out.write((json + "\n").getBytes("UTF-8"));
	}
}

