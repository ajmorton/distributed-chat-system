package commands;

import client.ChatClient;

/**
 * The New Identity Object
 * used to inform clients of a name change by the server
 */
public class NewIdentity extends Command{
	
	private 	 String former;			// the old client name
				 String identity;		// the new client name
	final static int    argCount = 2;	// the number of arguments to supply to the command
		
	// CONSTRUCTOR
	public NewIdentity(String identity, String former){
		this.identity = identity;
		this.former = former;
		this.type   = "newidentity";
	}
	
	// GETTER
	public String getFormer(){return former;}
	
	
	/**
	 * changes the clients name locally (if server allows it)
	 * and prints the result to stdout
	 */
	public void execute(ChatClient c){
		
		if(former.equals("")){
			// first name assignment by server, say nothing
			c.setClientName(identity);
				
		} else if(former.equals(identity)){
			// no change
			System.out.println("Requested identity invalid or in use");
		
		} else if(former.equals(c.getClientName())){
			// change user id
			c.setClientName(identity);
			System.out.println(former + " is now " + identity);
		
		} else {
			// inform others of userid change
			System.out.println("\n" + former + " is now " + identity);
		}
		
		if(!former.equals("")){
			c.printPrompt();	
		}		
	}
}