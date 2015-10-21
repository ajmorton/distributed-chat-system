package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import commands.Quit;

/**
 * A shutdown hook for the client class.
 * If the client is terminated via ctrl+c then shutdown hook will 
 * ensure that a threads in client are terminated
 * 
 */
public class ShutdownHookClient extends Thread{
	Send   s;	// the send   thread of ChatClient
	Listen l;	// the listen thread of ChatClient
	
	/**
	 * Constructor for ShutdownHookClient
	 * @param s the clients send thread
	 * @param l the clients listen thread
	 */
	public ShutdownHookClient(Send s, Listen l){
		this.s = s;
		this.l = l;
	}
	
	public void run(){
		// terminate all ChatClient Threads
		Quit quit = new Quit();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String json = gson.toJson(quit);
		s.getOut().println(json);
		
		l.quit();
		s.quit();
	}
	
	
}