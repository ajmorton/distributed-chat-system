package client;

import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.beust.jcommander.JCommander;

import java.io.*;


/**
 * The main class for ChatClient
 * Contains some details about the client such as name and current room and
 * the threads send and listen that perform most of the operation
 */
public class ChatClient {

  private static final String DEFAULT_HOST = "localhost";
  private static final boolean USE_CONTEXT = true;

  private String    clientName,      // name of the client on the server
                    roomName,        // name of the clients current room on the server
                    makeRoomRequest; // name of the room the clients is trying the create
  private SSLSocket socket;          // socket the client uses to connect to the server
  private Send      send;            // thread to send from stdin to the server
  private Listen    listen;          // thread that listen for server messages
  private boolean   quitFlag;        // flag that terminates the client

  // CONSTRUCTOR
  /**
   * The ChatClient constructor.
   * sets the initial values for the client and establishes the connection to the server
   * @param hostName   the name of the server host
   * @param serverPort the port to connect to the server on
   * @throws UnknownHostException
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws KeyStoreException
   * @throws CertificateException
   * @throws KeyManagementException
   */
  public ChatClient(String hostName, int serverPort) throws UnknownHostException, IOException, NoSuchAlgorithmException, KeyManagementException, CertificateException, KeyStoreException{
    this.clientName      = "";
    this.roomName        = "";
    this.makeRoomRequest = "";
    this.quitFlag        = false;

    SocketFactory factory = null;

    if (USE_CONTEXT) {
      // Create a trust manager factory to process the keystore later
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      
      // Create a default keystore instance to take the certificate
      KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
      
      // Create a stream from the certificate using the class loader
      InputStream keystoreStream = ClassLoader.getSystemResourceAsStream("resc/client.jks");
      
      // Load the keystore with the password
      keystore.load(keystoreStream, "bbbbbb".toCharArray());
      
      // Set the trust manager to use the keystore
      trustManagerFactory.init(keystore);
      
      // Get the new trust manager
      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      
      // Create a corresponding SSL context
      SSLContext ctx = SSLContext.getInstance("TLS");
      
      // Set the context to use the trust manager
      ctx.init(null, trustManagers, null);
      
      // Set the new context as the default
      SSLContext.setDefault(ctx);
      
      // Create a SocketFactory using our certificate
      factory = ctx.getSocketFactory();
    }
    else {
      System.setProperty("javax.net.ssl.trustStore", "/Users/rob/Desktop/distribCert.jks");
      System.setProperty("javax.net.ssl.trustStorePassword", "aaaaaa");
      factory = SSLSocketFactory.getDefault();
    }

    this.socket = (SSLSocket) factory.createSocket(hostName, serverPort);
    this.socket.startHandshake();
    this.send   = new Send(this);
    this.listen = new Listen(this);

  }

  // GETTERS
  public SSLSocket getSocket()          {return socket;}
  public String    getClientName()      {return clientName;}
  public Send      getSend()            {return send;}
  public Listen    getListen()          {return listen;}
  public Boolean   getQuitFlag()        {return quitFlag;}
  public String    getMakeRoomRequest() {return makeRoomRequest;}
  public String    getCurrentRoom()     {return roomName;}

  // SETTERS
  public void setRoom(String newRoom)         {roomName = newRoom;}
  public void setClientName(String newName)   {clientName = newName;}
  public void setRoomRequest(String roomName) {makeRoomRequest = roomName;}

  // METHODS



  /**
   * prints a prompt with the format [roomName] clientName>
   */
  public void printPrompt(){
    System.out.print("  [" + roomName + "] " + clientName + "> ");
  }

  /**
   * When called quits the client
   */
  public void quitClient(){
    quitFlag = true;
    try {
      socket.close();
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    }
  }


  /**
   * The main operation of the client
   * @param args
   * @throws InterruptedException
   * @throws KeyStoreException
   * @throws CertificateException
   * @throws NoSuchAlgorithmException
   * @throws KeyManagementException
   */
  public static void main (String args[]) throws InterruptedException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {

    Socket s = null;

    try{

      CmdLineArgs settings = new CmdLineArgs();
      new JCommander(settings, args);

      String hostName = settings.host.isEmpty() ? DEFAULT_HOST : settings.host.get(0);

      ChatClient c = new ChatClient(hostName, settings.port);

      // add shutdown hook
      Runtime.getRuntime().addShutdownHook(new ShutdownHookClient(c.getSend(), c.getListen()));

      while(!c.quitFlag){
        // main won't end until connection is severed
        Thread.sleep(1000);
      }

    }catch (UnknownHostException e) {
      System.out.println("Socket:"+e.getMessage());
    }catch (EOFException e){
      System.out.println("EOF:"+e.getMessage());
    }catch (IOException e){
      System.out.println("readline:"+e.getMessage());

    }finally {
      if(s!=null) try {
        s.close();
      }catch (IOException e){
        System.out.println("close:"+e.getMessage());
      }
    }
  }
}
