package commands;

import java.io.IOException;

import com.google.gson.Gson;

import server.Connection;
import server.Room;

/**
 * The Message Object
 * Used to send a message to the server
 */
public class Message extends Command{
	
	private 	 String content;		// the message to send
	final static int 	argCount = 1;	// the number of arguments to supply to the command
	
	public Message(){	
	}
	
	public Message(String content){
		this.content = content;
		this.type = "message";		
	}
	
	public void execute(Connection c) throws IOException{
		Room currRoom = c.getClientInfo().getCurrRoom();
		String sender = c.getName();
		
		Gson gson = new Gson();
		ServerMessage sMessage = new ServerMessage(content, sender);
		String message = gson.toJson(sMessage);
		currRoom.broadcast(message);
		
	}
	
}