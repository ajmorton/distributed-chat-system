package commands;

import java.io.IOException;
import java.util.Vector;

import server.Connection;
import server.Room;
import server.ServerInfo;

/**
 * The List Command
 * Used to send a list of all rooms on the server
 */
public class List extends Command{
  
  final static int argCount = 0; // the number of arguments to supply to the command
  
  // CONSTRUCTOR
  public List(){
    this.type = "list";
  }
  
  /**
   * fetches a list of all rooms on the server and sends to the client
   */
  public void execute(Connection c) throws IOException{
    ServerInfo   sInfo = c.getServerInfo();
    Vector<Room> rList = sInfo.getRoomList(); 
    
    RoomList roomList = new RoomList(rList);
    roomList.sendJSON(c);    
  }
  
}