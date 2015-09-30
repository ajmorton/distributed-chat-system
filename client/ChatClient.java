package client;

import java.net.*;
import java.io.*;


/**
 * The main class for ChatClient
 * Contains some details about the client such as name and current room and
 * the threads send and listen that perform most of the operation
 */
public class ChatClient {
	
	private String 	clientName,			// name of the client on the server
					roomName,			// name of the clients current room on the server
					makeRoomRequest;	// name of the room the clients is trying the create
	private Socket 	socket;				// socket the client uses to connect to the server
	private Send 	send;				// thread to send from stdin to the server
	private Listen 	listen;				// thread that listen for server messages
	private Boolean quitFlag;			// flag that terminates the client
	
	
	// CONSTRUCTOR
	/**
	 * The ChatClient constructor.
	 * sets the initial values for the client and establishes the connection to the server
	 * @param hostName   the name of the server host
	 * @param serverPort the port to connect to the server on
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public ChatClient(String hostName, int serverPort) throws UnknownHostException, IOException{
		this.clientName      = "";
		this.roomName        = "";
		this.makeRoomRequest = "";
		
		this.quitFlag 		 = false;
		
		this.socket   		 = new Socket(hostName, serverPort);
		this.send  			 = new Send(this);
		this.listen			 = new Listen(this);
		
	}
	
	// GETTERS
	public Socket  getSocket()			{return socket;}
	public String  getClientName()		{return clientName;}
	public Send    getSend()			{return send;}
	public Listen  getListen()			{return listen;}
	public Boolean getQuitFlag()		{return quitFlag;}
	public String  getMakeRoomRequest() {return makeRoomRequest;}
	
	// SETTERS
	public void setRoom(String newRoom)			{roomName = newRoom;}
	public void setClientName(String newName)	{clientName = newName;}
	public void setRoomRequest(String roomName) {makeRoomRequest = roomName;}


	// METHODS
	
	/**
	 * prints a prompt with the format [roomName] clientName>
	 */
	public void printPrompt(){
		System.out.print("[" + roomName + "] " + clientName + "> ");
	}
	
	/**
	 * When called quits the client
	 */
	public void quitClient(){
		quitFlag = true;
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	
	
	/** 
	 * The main operation of the client
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main (String args[]) throws InterruptedException {
		
		Socket s = null;

		try{

			String hostName   = args[0];
			int    serverPort = 4444;		// the default port number

			// optional port change
			if(args.length > 1){
				serverPort = Integer.parseInt(args[1]);
			}

			ChatClient c = new ChatClient(hostName, serverPort); 
			
			// add shutdown hook
			Runtime.getRuntime().addShutdownHook(new ShutdownHookClient(c.getSend(), c.getListen()));

			while(!c.quitFlag){
				// main won't end until connection is severed
				Thread.sleep(1000);
			}

		}catch (UnknownHostException e) {
			System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){
			System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){
			System.out.println("readline:"+e.getMessage());
		
		}finally {
			if(s!=null) try {
				s.close();
			}catch (IOException e){
				System.out.println("close:"+e.getMessage());
			}
		}
	}
}
