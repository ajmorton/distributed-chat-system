package commands;


/**
 * The Password Object
 * Used to send a password to the server
 */
public class Password extends Command{
	
	private 		String 	hash;			// the password hash to send
	private 		String 	user;			// the username for authentication
	final static 	int 	argCount = 1;	// the number of arguments to supply to the command
	
	public Password(String user, String hash){
		this.hash = hash;
		this.type = "message";		
		this.user = user;
	}
	
	// GETTERS 
	public String getHash() {return hash;}
	public String getUser() {return user;}
}