package commands;

import client.ChatClient;

/**
 * The ServerMessage Command
 * Used to send messages to clients from the server
 *
 */
public class ServerMessage extends Command{
  
  private String content,  // the content of the message
                 identity; // the client the message is from
  
  final static int argCount = 2;  // the number of arguments to supply to the command
    
  // CONSTRUCTOR
  public ServerMessage (String content, String identity){
    this.type     = "message";
    this.content  = content;
    this.identity = identity;
  }
  
  // GETTER
  public String getIdentity(){return identity;}
  
  /**
   * prints the message content to stdout
   */
  public void execute(ChatClient c){
    
    if(!identity.equals(c.getClientName())){
      // if message from other client overwrite current prompt line
      // couldn't be bothered to import a formatter
      
      // TODO check that this will work on assessment computer --> will have to do friday
      System.out.print("\r" + "  [" + c.getCurrentRoom() + "] " + identity + "> " + content + "                 \n");        
    }

    c.printPrompt();

    
              
  }
}