package server;

import com.google.gson.*;
import commands.*;

import java.io.*;
import java.net.Socket;

/** 
 * A client-server connection
 * Handles all the I/O of the server individually for each connection
 */
public class Connection extends Thread
{
	
	private static final boolean DEBUG = true;
	
	private BufferedReader 	in;				// used to read messages from client
	private PrintWriter    	out;			// used to send messages to client
	private Socket      	clientSocket;	// the socket used to connect to client
	private ServerInfo     	sInfo;			// information about the server
	private ClientInfo     	cInfo;			// information about the client
	private Boolean        	terminateFlag; 	// flag that terminates the connection

	// CONSTRUCTOR
	public Connection (Socket s, ServerInfo sInfo, ClientInfo cInfo) {
		try {
			this.clientSocket  = s;
			this.in            = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF8"));
			this.out 		   = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF8"), true);
			this.sInfo 		   = sInfo;
			this.cInfo 		   = cInfo;
			this.terminateFlag = false;
			setName("");
			
			this.start();
		
		} catch(IOException e) {
			System.out.println("Client:"+e.getMessage());
		} 		
	}
	
	// GETTERS
	public ClientInfo  getClientInfo()			{return cInfo;}
	public ServerInfo  getServerInfo()			{return sInfo;}
	public Socket 	   getSocket()	    		{return clientSocket;}
	public PrintWriter getPrintWriter() 		{return out;};
	public BufferedReader getBufferedReader() 	{return in;}
	
	/**
	 * calls sendMessage to send a JSON string message to the client
	 * detects and removes dropped connections
	 * @param message the JSON string
	 * @return 
	 */
	public void send(String message) throws IOException{
		
		if(sendMessage(message)){
			new Quit().execute(this);
		}
		
	}
	
	/**
	 * sends a JSON object message to the client, returns true if there is an error in sending
	 * @param message the JSON object
	 * @return droppedConnection if the connection is broken
	 */
	public boolean sendMessage(String message) throws IOException{
		
		boolean connDropped = false;
		
		if (DEBUG) {
			System.out.println(this.getName() + ": " + message);
		}

		out.println(message);
		
		connDropped = out.checkError();
		
		if (DEBUG) {System.out.println("***SENT***");}
		return connDropped;
	}

	/**
	 * terminates the connection on the server end
	 */
	public void terminate(){
		terminateFlag = true;
	}
	
	
	/**
	 * listens for incoming messages from the client and the performs the 
	 * operations as dictated by the message
	 */
	public void run(){
		try {
			
			System.out.println("Execution starts");
			executeMessage(readMessage());
			System.out.println("Enter while loop");
			
			while(!terminateFlag){
				executeMessage(readMessage());
			}

		}catch (EOFException e){
			System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {
			System.out.println("readline:"+e.getMessage());
		} catch (NullPointerException e){
			// TODO only occurs at Connection termination
			// due to readline() blocking, ready() not available for SSL
		}finally {
			try {
				clientSocket.close();
			}catch (IOException e){
				System.out.println("IO:" + e.getMessage());
			}
		}
	} 
	
	
	/**
	 * converts a JSON string to a command object
	 * @param json the JSON string
	 * @return a command object
	 */
	private Command getCommand(String json){
		
		// fetch the type information from the JSON
		Gson       gson = new Gson();
		JsonObject jObj = gson.fromJson(json, JsonObject.class);
		String     type = jObj.get("type").getAsString(); 
		
		// convert JSON string to Command Object dependent 
		// on the type values in the JSON string
		switch(type){
		case "identitychange":	
			return gson.fromJson(json, IdentityChange.class);
		case "join": 			
			return gson.fromJson(json, Join.class);			
		case "who":				
			return gson.fromJson(json, Who.class);
		case "list":			
			return gson.fromJson(json, List.class);
		case "createroom":		
			return gson.fromJson(json, CreateRoom.class);
		case "kick":			
			return gson.fromJson(json, Kick.class);
		case "delete":			
			return gson.fromJson(json, Delete.class);
		case "message":			
			return gson.fromJson(json, Message.class);
		case "quit":			
			return gson.fromJson(json, Quit.class);
		case "authenticate":
			return gson.fromJson(json, Authenticate.class);
		case "login":
			return gson.fromJson(json, Login.class);
		}
		
		//invalid JSON received
		return null;
	}
	
	private String readMessage() throws IOException
	{		
		if (DEBUG) {System.out.println("***RECEIVING***");}
		String json = in.readLine();
		if (DEBUG) {
			System.out.println("***RECEIVED***");
			System.out.println(json);
		}

		return json;
	}
	
	private void executeMessage(String json) throws IOException
	{
		// generate Command object from JSON String
		Command command = getCommand(json);	

		//invalid input, ignore and move on
		if(command == null){
			System.out.println("JSON Message Error");
			return;
		}

		// otherwise execute command
		// operation for the command is found in the respective command classes
		command.execute(this);
	}
}
