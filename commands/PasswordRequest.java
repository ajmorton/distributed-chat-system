package commands;

import com.google.gson.Gson;

import client.ChatClient;

public class PasswordRequest extends Command {

	static int argCount = 0;
	
	// CONSTRUCTOR
		public PasswordRequest(){
			this.type = "passwordrequest";
		}
	
	public void execute(ChatClient c){
		
		// prompt client to enter password
		String hash = c.getSend().enterPassword();
		
		// get clients name
		String user = c.getClientName();
		
		
		Gson gson = new Gson();
		String json = gson.toJson(new Password(user, hash));
		
		//send the password json to the server
		c.getSend().getOut().println(json);
	}
	
}
