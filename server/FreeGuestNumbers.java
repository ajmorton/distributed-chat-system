package server;

import java.util.TreeSet;

/**
 * FreeGuestNumbers keeps track of what guest names are in use
 * also generates new guest names
 *
 */
public class FreeGuestNumbers {
  private volatile TreeSet<Integer> freeGuestNums; // list of free guest names on the server
  private volatile ServerInfo       sInfo;         // information bout the server

  private int numGuests; // the number of guest names assigned
  
  public FreeGuestNumbers(ServerInfo sInfo){
    this.freeGuestNums  = new TreeSet<Integer>();
    this.numGuests      = 0;
    this.sInfo          = sInfo;
  }
  
  
  /**
   * creates a new guestName for a new client
   */
  public synchronized String getNewName(){
    
    if(freeGuestNums.size() == 0){
      // there are no free guest names, create a new one
      int newNum = ++numGuests; 
      
      while(sInfo.getClient("guest" + newNum) != null){
        newNum++;
      }
      
      return "guest" + newNum;
    } else{
      // return the smallest free guest name
      int freeNum = freeGuestNums.first();
      freeGuestNums.remove(freeNum);
      return "guest" + freeNum;
    }  
  }  
  
  
  /**
   * take a client name and if it is a guest name ("guest[Integer]") then 
   * add the number to the list of free guest numbers
   * @param name the name string
   */
  public void freeGuest(String name){
    
    if(name.length() > 5){

      Boolean startsWithGuest = name.substring(0,5).equals("guest");
      Boolean endsWithNumber  = name.substring(5).matches("[0-9]+");

      if(!startsWithGuest || !endsWithNumber){
        return;
      }
      
      int gNum = Integer.parseInt(name.substring(5));
      
      // don't free numbers that are negative or zero 
      if(gNum > 0){
        freeGuestNums.add(gNum);
      }
    }
  }  
  
}
