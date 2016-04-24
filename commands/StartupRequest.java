package commands;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import client.ChatClient;

public class StartupRequest extends Command
{
  private static final Console console = System.console();
  
  public StartupRequest()
  {
    this.type = "startuprequest";
  }
  
  public void execute(ChatClient c)
  {
    
    Gson           gson   = new GsonBuilder().disableHtmlEscaping().create();
    PrintWriter    pw     = c.getSend().getOut();
    BufferedReader reader = c.getSend().getIn();

    StartupResponse resp;
    String          json;
    
    boolean loginRequested = false;
    try {
      loginRequested = askToLogin(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (loginRequested) {
      try {
        System.out.print("Username: ");
        String username = reader.readLine();
        String hash     = takePassword("Password: ");
        
        resp = new StartupResponse(username, hash);
        json = gson.toJson(resp, StartupResponse.class);
        pw.println(json);
        return;
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    resp = new StartupResponse("","");
    json = gson.toJson(resp, StartupResponse.class);
    pw.println(json);
    return;
  }
  
  private static String takePassword(String message)
  {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    
    char[] pass      = console.readPassword(message);
    byte[] passBytes = (new String(pass)).getBytes(StandardCharsets.UTF_8);
    
    return new String(Base64.encode(md.digest(passBytes)));
  }
  
  private static boolean askToLogin(BufferedReader reader) throws IOException
  {
    String response;
    System.out.println("Do you wish to login? (yes/no)");
    response = reader.readLine();
    while (!response.matches("yes|no")) {
      System.out.println("Please type 'yes' or 'no'.");
      System.out.println("Do you wish to login? (yes/no)");
      response = reader.readLine();
    }
    
    if (response.equals("yes")) {
      return true;
    }
    
    return false;
  }
}
