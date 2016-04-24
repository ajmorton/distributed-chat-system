package commands;

import client.ChatClient;

public class AuthResponse extends Command
{
  private String message;
  private boolean authSuccess;
  
  public AuthResponse(String message, boolean authSuccess)
  {
    this.type        = "authresponse";
    this.message     = message;
    this.authSuccess = authSuccess;
  }
  
  public void execute(ChatClient c)
  {
    if (!authSuccess) {
      // TODO change these to \r after protocol is finished
      System.out.println("Authentication Failure: " + message);
    }
    else if(message.isEmpty()) {
      System.out.println("Login Success");
    } 
    else {
      System.out.println("Authenticated Account Created");
    }
    
    c.printPrompt();
  }
}
