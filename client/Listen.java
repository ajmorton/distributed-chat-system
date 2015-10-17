package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import commands.Command;
import commands.NewIdentity;
import commands.Ping;
import commands.RoomChange;
import commands.RoomContents;
import commands.RoomList;
import commands.ServerMessage;

/**
 * Listen is a thread in ChatClient that listens for messages from the server and
 * prints to stdout along with any required operations required by the message
 */
public class Listen extends Thread
{
	
	private static final boolean DEBUG = false;
	
	BufferedReader 	in;			// reads in messages from the server
	ChatClient 		c;			// the client that the thread is in
	Boolean 		quitFlag;	// flag to terminate the thread
	
	/**
	 * The Listen Constructor
	 * @param c
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public Listen(ChatClient c) throws UnsupportedEncodingException, IOException{
		Socket s = c.getSocket();
		
		this.in       = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF8"));
		this.c        = c;
		this.quitFlag = false;
		this.start();
	}
	
	// SETTERS
	/** sets the quitFlag to true*/
	public void quit()	{quitFlag = true;}
		
	
	
	/**
	 * The operation of the Listen Thread
	 */
	public void run(){

		try {
			while(!(quitFlag = c.getQuitFlag())){
				// if a non-blocking read is possible
				
				if (DEBUG) {System.out.println("***READY TO RECIEVE***");}
				// convert the JSON to its corresponding message object
				if (DEBUG) {System.out.println("***RECEIVING***");}
				String  json    = in.readLine();
				if (DEBUG) {
					System.out.println("***RECEIVED***");
					System.out.println(json);
				}

				Command command = getCommand(sanitize(json));

				// if JSON cannot be matched to a command object
				if(command == null){
					System.out.println("JSON Message Error");
					System.out.println("JSON is " + json);
					continue;
				}

				// performs the operations required for the message
				command.execute(c);
					
				// if no non-blokcing read available wait for .1 seconds
				sleep(100);
			}
			
		} catch (IOException e) {
			System.out.println("Listen: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Listen: " + e.getMessage());
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * Sanitizes the input string to ensure correct operation when 
	 * converting to a Command object via gson.
	 * Specifically deals with JSON string with a list in them.
	 * @param input the input string to be sanitized
	 * @return a sanitized input string
	 */
	private String sanitize(String input){
		
		int openBracket,
			closeBracket;
		
		openBracket  = input.indexOf('[');
		closeBracket = input.indexOf(']') + 1;
		
		// no brackets, no need to sanitize
		if(openBracket == -1 || closeBracket == -1){
			return input;
		}
		
		// split into sections, toFix is the only section that requires fixing
		String preOpen   = input.substring(0,openBracket);
		String toFix     = input.substring(openBracket,closeBracket);
		String postClose = input.substring(closeBracket);
		
		// if there is a " after the [ then it can be assumed that input is sanitary
		if(input.substring(openBracket+1, openBracket+2).equals("\"")){
			// input will work as is
			return input;
		}
		
		// else need to fix:
		// replace all " with \" 
		// replace { with "{
		// replace } with }"
		toFix = toFix.replace("\"", "\\\"");
		toFix = toFix.replace("{", "\"{");
		toFix = toFix.replace("}", "}\"");
		
		String sanitized = preOpen + toFix + postClose;
		return sanitized;
	}
	
	
	/**
	 * Takes a JSON string and converts to its correspoding command object.
	 * The type of object is determined by the "type" key
	 * @param json the string to convert
	 * @return a command object
	 */
	private Command getCommand(String json){
		
		Gson gson       = new Gson();
		JsonObject jObj = gson.fromJson(json, JsonObject.class);
		String type     = jObj.get("type").getAsString(); 
		
		switch(type){
		case "message":				
			return gson.fromJson(json, ServerMessage.class);
		case "newidentity": 		
			return gson.fromJson(json, NewIdentity.class);			
		case "roomchange":			
			return gson.fromJson(json, RoomChange.class);
		case "roomcontents":		
			return gson.fromJson(json, RoomContents.class);
		case "roomlist":			
			return gson.fromJson(json, RoomList.class);
		case "ping":				
			return gson.fromJson(json, Ping.class);
		}
		
		//invalid JSON received
		return null;
	}
	
}