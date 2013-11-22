package app_kvServer;

import java.io.IOException;
import java.net.Socket;

import logger.LogSetup;

import org.apache.log4j.Level;

import server.ClientConnection;
import server.Server;


public class KVServer extends Server {
	
	private KVDatabase db;
	/**
	 * Start KV Server at given port
	 * @param port given port for storage server to operate
	 */
	public KVServer(int port) {
		super(port);
		db = new KVDatabase();
	}
	
	protected ClientConnection createClientConnection(Socket client) {
    	return new KVClientConnection(client, db);
    }
	
	/**
     * Main entry point for the KV server application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
    	try {
			new LogSetup("logs/server.log", Level.ALL);
			if(args.length != 1) {
				System.out.println("Error! Invalid number of arguments!");
				System.out.println("Usage: Server <port>!");
			} else {
				int port = Integer.parseInt(args[0]);
				new KVServer(port).start();
			}
		} catch (IOException e) {
			System.out.println("Error! Unable to initialize logger!");
			e.printStackTrace();
			System.exit(1);
		} catch (NumberFormatException nfe) {
			System.out.println("Error! Invalid argument <port>! Not a number!");
			System.out.println("Usage: Server <port>!");
			System.exit(1);
		}
    }
}
