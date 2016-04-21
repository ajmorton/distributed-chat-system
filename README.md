# DistSyst
Assignment 2 for COMP90015 Distributed Systems at the
University of Melbourne.

A simple, room-based Chat Server and Client, with a JSON protocol, written in
Java.

Main classes are:
    
    * Server: server.ChatServer.java
    * Client: client.ChatClient.java

To run the server, call:
    `java ChatServer [-p <port-number>]`

To run the client, call:
    `java ChatClient <hostname> [-p <port-number>]`

Where port numbers default to 4444.

Read the pdf Assignment-1.pdf for more information.
