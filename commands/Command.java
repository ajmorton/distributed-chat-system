package commands;
import java.io.IOException;

import client.ChatClient;

import com.google.gson.Gson;

import server.Connection;

// the parent class(es) that all command are extended from

/**
 * The parent class of all commands
 */
public class Command{  
  protected        String type;     // the type of command
  protected static int    argCount; // how many argument should be supplied to the argument
                                    // used when creating objects from strings via gson

  // CONSTRUCTOR
  public Command() {}

  // GETTERS
  public        String getType()     {return type;}
  public static int    getArgCount() {return argCount;}

  // METHODS
  
  /**
   * execute the command on the server side
   * execute is overridden in each child of Command
   * @param c the server connection
   * @throws IOException
   */
  public void execute(Connection c) throws IOException{
    System.out.println("execute(Client c) not overridden");
  }
  
  /**
   * execute the command on the client side
   * execute is overridden in each child of Command
   * @param c the chatClient
   * @throws IOException
   */
  public void execute(ChatClient c) {
    System.out.println("execute(ChatClient c) not overridden");
  }
  
  /**
   * converts the object to a JSON String and sends to the client
   * Only used server side
   * @param c
   * @throws IOException
   */
  public void sendJSON(Connection c) throws IOException{  
    Gson   gson = new Gson();
    String json = gson.toJson(this);
    c.send(json);
  }


  
  
}



