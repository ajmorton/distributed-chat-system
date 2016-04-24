package commands;

import java.io.IOException;
import java.util.Vector;

import server.Connection;
import server.Room;

/** 
 * The Who Command
 * Used to request a list of clients in a room
 */
public class Who extends Command{
  
  final static int    argCount = 1;  // the number of arguments to supply to the command
  private      String roomid;        // the name of the room the client is querying 
  
  // CONSTRUCTOR
  public Who(String roomid){
    this.roomid = roomid;
    this.type   = "who";
  }
  
  /**
   * fetches a list of clients in the requested room and sends 
   * the result back to the requesting client
   */
  public void execute(Connection c) throws IOException{
    
    Room queryRoom = c.getServerInfo().getRoom(roomid);

    Vector<Connection> inRoom;
    String          owner;
    
    if(queryRoom == null){
      // no clients in nonexistent room
      inRoom = new Vector<Connection>();
      owner = "";
    } else {
      // get client in room
      inRoom = queryRoom.getRoomClients();
      owner  = queryRoom.getRoomOwner();
    }
    
    
    
    // send roomContents message to requesting client
    RoomContents roomContents = new RoomContents(roomid, inRoom, owner);
    roomContents.sendJSON(c);    
  }
}