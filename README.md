# DistSyst
Assignment 2 for COMP90015 Distributed Systems at the
University of Melbourne.

A distributed, room-based Chat Server and Client, with a JSON protocol, written in
Java.

**Main classes:**

    Server: server.ChatServer.java
    Client: client.ChatClient.java

To run the server, call:
    `java ChatServer [-p <port-number>]`

To run the client, call:
    `java ChatClient <hostname> [-p <port-number>]`

Where port numbers defaults to 4444.

**Features:**

    SSH communication
    Multithreading
    Client Crash detection and resolution
    Room dependent user permissions
    Password hashing and salting
    Dynamic guest user name generation
    Persistent User Accounts (for the lifetime of the Server)


**Client Commands:**  
On connection a client can either log in to an existing account, or join as a guest.
At this point the following commands are available to the user:

  `#authenticate`  
  changes a guest user to an authenticated user, prompting for a password and
  creating a new account on the server

  `#createroom <newRoom>`  
  creates a new chat room on the server, with the user given admin permissions

  `#kick <kickRoom> <banTime> <kickedUser>`  
  removes and bans a user kickedUser from the chat room kickRoom for banTime seconds
  This action is only completed if the calling user is the owner of the room

  `#delete <room>`  
  deletes to provided chat room only if the calling user is the owner of the room

  `#login <userName>`
  attempts to log into an existing account. Prompts for the password and rejects
  if case of a wrong password, non-existant userName or if the account is already
  logged in

  `#join <room>`  
  attempts to join a chat room. Fails if the room does not exist or if the user is banned

  `#who <room>`  
  provides information on a room; the owner and all users currently in the room are listed

  `#list`  
  returns a list of all rooms that exist on the server

  `#identitychange <newIdentity>`  
  changes the users name to newIdentity, fails if the identity is already in use

  `#logout`  
  logs an authenticated user out of the server

  `#quit`  
  allows any user to leave the server

  `<message>`  
  any command that does not match the above patterns is treated as a message and
  broadcast to all users in the same chat room


*Additional information is provided in Assignment-1.pdf*
