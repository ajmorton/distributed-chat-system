package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.google.gson.Gson;
import commands.*;

/**
 * The Send thread deals with reading in commands from stdin 
 * and converting them to their respective JSON command then 
 * sending them to the server
 */
public class Send extends Thread{

	// TODO max buffer size 1000
	
	BufferedReader in;    		// used to read in from stdin
	PrintWriter    out;			// used to send messages to the server
	ChatClient 	   c;			// the client that send is a thread of
	Boolean 	   quitFlag;	// the flag to quit the client
	
	
	// CONSTRUCTOR
	
	public Send(ChatClient c) throws UnsupportedEncodingException, IOException{
		Socket s 	  = c.getSocket();
		this.in       = new BufferedReader(new InputStreamReader(System.in));
		this.out 	  = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF8"), true);
		this.c   	  = c;
		this.quitFlag = false;
		
		this.start();
		
	}

	
	// SETTERS
	
	public void quit()			{quitFlag = true;}
	
	
	
	/** Operation of the Send thread
	 * reads in from stdin and then based on the input converts the input to
	 * its corresponding message object then sends to the server as a JSON
	 */
	public void run(){

		String input;
						
		while(!(quitFlag = c.getQuitFlag())){
			
			try {
				if(in.ready()){
					// can read from stdin without blocking
					
					input = in.readLine();
					
					// finds the index of the first space in the input string
					// -1 means that no space exists in the string
					int firstSpaceIndex = input.indexOf(' ');

					// if no space in word
					if(firstSpaceIndex == -1){
						firstSpaceIndex = input.length();
					}

					// split the input string into the first argument (the type of command)
					// and the rest of the the string (the arguments supplied to the command)
					String firstWord   = input.substring(0,firstSpaceIndex);
					String restOfInput = input.substring(firstSpaceIndex);

					// strip leading space if they exists
					if(restOfInput.indexOf(" ") == 0){
						restOfInput = restOfInput.substring(1);
					}

					// convert input string to a Command object
					Command commandObj = getCommandObj(c, firstWord, restOfInput);

					// convert the command object to a JSON string using gson
					Gson gson   = new Gson();
					String json = gson.toJson(commandObj);
					out.println(json);
	
				} else {
					// if a non-blocking read is not ready wait .1 seconds
					try {
						sleep(100);
					} catch (InterruptedException e) {
						System.out.println("Send: " + e.getMessage());
						e.printStackTrace();
					} 
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Converts the input string into a Command Object from the messages package
	 * the Object type is determined by the first argument in the input string 
	 * @param c           a reference to the ChatClient
	 * @param firstWord   the first word of the input string, dictates the type of command
	 * @param restOfInput the arguments supplied to the command
	 * @return an object of the Command type
	 */
	private static Command getCommandObj(ChatClient c, String firstWord, String restOfInput){

		String[] argArray = restOfInput.split(" ");

		switch(firstWord){
		case "#identitychange":
			return new IdentityChange(restOfInput);	
		case "#join":
			return new Join(restOfInput);				
		case "#who":
			return new Who(restOfInput);				
		case "#list":
			return new List();				
		case "#createroom":
			c.setRoomRequest(restOfInput);
			return new CreateRoom(restOfInput);		
		case "#kick":
			// simple error checking
			if((argArray.length < 3) || (argArray[1].matches("[0-9]+"))){return null;}
			return new Kick(argArray);				
		case "#delete":
			return new Delete(restOfInput);			
		case "#quit":
			return new Quit();			
		default:
			// if the first word doesn't match any of the above switch cases it is a message
			return new Message(firstWord + " " + restOfInput);		
		}		
	}
}		


