package commands;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
			System.out.println("Authentication Failure: " + message);
			return;
		}
		
		if(message.isEmpty()) {
			System.out.println("Authentication Success");
			return;
		}
		
		System.out.println("New Authentication Success");
		try {
			PrintWriter pw = new PrintWriter("./chat.config", "UTF-8");
			pw.println(message);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
