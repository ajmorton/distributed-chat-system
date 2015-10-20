package commands;

import client.ChatClient;

public class AuthResponse extends Command
{
	private String message;
	private boolean authSuccess;
	
	public AuthResponse(String message, boolean authSuccess)
	{
		this.type = "authresponse";
		this.message = message;
		this.authSuccess = authSuccess;
	}
	
	public void execute(ChatClient c)
	{
		if (!authSuccess) {
			System.out.println("\nAuthentication Failure:");
			System.out.println(message);
		}
		else if(message.isEmpty()) {
			System.out.println("\nLogin Success");
		} 
		else {
			System.out.println("\nAuthenticated Account Created");
		}
		
		c.printPrompt();
	}
}
