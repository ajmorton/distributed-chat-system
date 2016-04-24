package commands;

import java.io.IOException;
import java.util.Vector;

import server.Connection;
import server.Room;
import server.ServerInfo;

/**
 * The Delete object
 * Used to remove rooms from the server
 */
public class Delete extends Command{
  
  final static int   argCount = 1; // the number of arguments to supply to the command
  private      String roomid;      // the name of the room to delete
  
         
  // CONSTRUCTOR
  public Delete(String roomid){
    this.roomid = roomid;
    this.type   = "delete";
  }
  
  /**
   * deletes a room from the server if the command is sent by the owner of the room
   */
  public void execute(Connection c) throws IOException{
    ServerInfo sInfo  = c.getServerInfo();
    String     userid = c.getName();
    
    
    String kickersName = c.getName();
    if(!sInfo.inAuthIndex(kickersName)){
      // user not authenticated, cannot delete rooms
      // send them a list to show rooms unchanged
      (new ServerMessage("", c.getName())).sendJSON(c);
      return;
    }
    
    Room delRoom = sInfo.getRoom(roomid);
    if(delRoom == null){
      //room not found
      (new ServerMessage("", c.getName())).sendJSON(c);
      return;
    }
    
    // else room exists
    Boolean isRoomOwner = delRoom.getRoomOwner().equals(userid);
    if(isRoomOwner){
      // can delete room:
      
      // move all users to mainHall
      Vector<Connection> inRoom = delRoom.getRoomClients();
      Connection user;
      while(inRoom.size() > 0){
        user            = inRoom.get(0);
        String mainHall = "MainHall";
        
        Join join = new Join(mainHall);
        join.execute(user);
        
      }
      
      sInfo.removeRoom(delRoom);
      
      // remove room from roomList
      Vector<Room> roomList = sInfo.getRoomList();
      RoomList     rList    = new RoomList(roomList);
      rList.sendJSON(c);
    } else {
      (new ServerMessage("", c.getName())).sendJSON(c);
    }
    
    
  }
  
}