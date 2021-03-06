package commands;

import java.io.IOException;

import com.google.gson.Gson;

import server.ClientInfo;
import server.Connection;
import server.ServerInfo;

/**
 * The IdentityChange class
 * Changes a users name if the new name is valid
 */
public class IdentityChange extends Command
{
  final static int argCount = 1; // the number of arguments to supply to the command
  protected String identity;     // the new identity to change to
  
  
  // CONSTRUCTOR
  public IdentityChange(String identity){
    this.identity = identity;
    this.type     = "identitychange";
  }
  
  // GETTER
  public String getIdentity() {return identity;}
  
  /**
   * Changes a clients name on the server if the new name is valid
   */
  public void execute(Connection c) throws IOException{
    
    String oldName   = c.getName();
    String newName   = identity;
    
    ServerInfo sInfo = c.getServerInfo();

    if (!validName(newName, sInfo))
    {
      newName = oldName;
    }
    changeID(c, sInfo, oldName, newName);
    return;
  }
  
  protected void changeID(Connection c, ServerInfo sInfo, String oldName, String newName) throws IOException
  {    
    NewIdentity newID = new NewIdentity(newName, oldName);
    
    if (newName.equals(oldName)) {
      newID.sendJSON(c);
      return;
    }
    
    ClientInfo cInfo = c.getClientInfo();
    
    if (cInfo.isAuthenticated()) {
      sInfo.changeAuthenticatedName(oldName, newName);
    }
    
    c.setName(newName);
    
    cInfo.updateOwnedRoom(newName);
    
    sInfo.freeGuest(oldName);
    
    Gson   gson = new Gson();
    String json = gson.toJson(newID);
    cInfo.getCurrRoom().broadcast(json);
  }
  
  /**
   * Checks if the new name is valid.
   * To be valid it must not be in use, alphanumeric, begin with a letter, 
   * and between 6 and 16 characters long
   * @param newName the potential new new
   * @param sInfo   server information
   * @return
   */
  private Boolean validName(String newName, ServerInfo sInfo){
    
    return validRegexName(newName) && !nameAlreadyExists(newName, sInfo);
  }

  /**
   * 
   * @param name
   * @param sInfo
   * @return
   */
  protected boolean nameAlreadyExists(String name, ServerInfo sInfo)
  {
    return isAuthName(name, sInfo) || isConnectedName(name, sInfo);
  }
  
  protected boolean isConnectedName(String name, ServerInfo sInfo)
  {
    for (Connection conn: sInfo.getAllClients()) {
      if (conn.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean isAuthName(String name, ServerInfo sInfo)
  {
    return sInfo.inAuthIndex(name);
  }
  
  protected boolean validRegexName(String name)
  {
    return name.matches("[A-Za-z][A-Za-z0-9]{5,15}");
  }
  
  
  protected boolean isGuestName(String name)
  {
    return name.matches("guest\\d+");
  }
}