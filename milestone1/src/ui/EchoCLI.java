package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class EchoCLI {
	
	private EchoClient client;
	private static Logger log= Logger.getLogger(EchoCLI.class);
	
	
	public EchoCLI(){
		client = new EchoClient();
		
		log.setLevel(Level.ALL);
		SimpleLayout sL = new SimpleLayout();
		//ConsoleAppender cA = new ConsoleAppender(sL);
		//log.addAppender(cA);
		FileAppender fA;
		try {
			fA = new FileAppender(sL,"log/client.log", true);
			log.addAppender(fA);
		} catch (IOException e) {
			println("Couldn't open log file");
			log.error(e.toString());
		}

		//Gather all messages in one logger
		EchoClient.log = log;
		Client.log = log;
		
	}
	
	/**
	 * Enters the main loop of the command line interface
	 */
	public void mainLoop(){
		BufferedReader cons = new BufferedReader(new InputStreamReader(System.in));
		
		boolean quit = false;
		
		while(!quit)
		{
			print("");
			String input;
			try {
				input = cons.readLine();
				quit = executeCommand(input);
			} catch (IOException e) {
				log.error(e.toString());
			}
			
		}
	}
	
	/**
	 * Print some output to the console (without newline)
	 * @param s: string to print
	 */
	public void print(String s){
		System.out.print("Prompt> "+s);
	}
	
	/**
	 * Print some output to the console
	 * @param s: string to print
	 */
	public void println(String s){
		log.info("Printed: "+s);
		if(s.endsWith("\n"))
			System.out.print("Prompt> "+s);
		else
			System.out.println("Prompt> "+s);
		
	}
	
	/**
	 * Parses an input line and executes the command
	 * @param input: the command line
	 * @return true to stop the CLI, false to continue
	 */
	private boolean executeCommand(String input)
	{
		int cmdDelim = input.indexOf(' ');
		String cmd = "";
		String args = "";
		if(cmdDelim >= 0)
		{
			cmd = input.substring(0, cmdDelim);
			if(cmdDelim+1 < input.length())
				args = input.substring(cmdDelim+1);
		}
		else
		{
			cmd = input;
		}
		
		boolean quit = false;
		
		int cmdId = 0;
		
		if(cmd.equals("connect"))
			cmdId=1;
		else if(cmd.equals("send"))
			cmdId=2;
		else if(cmd.equals("disconnect"))
			cmdId=3;
		else if(cmd.equals("help"))
			cmdId=4;
		else if(cmd.equals("logLevel"))
			cmdId=5;
		else if(cmd.equals("quit"))
			cmdId=6;
		log.trace("Issued command "+cmd+"(ID:"+cmdId+") args: "+args);
		
		switch(cmdId)
		{
		case 1:
			connect(args);
			break;
		case 2:
			send(args);
			break;
		case 3:
			disconnect(args);
			break;
		case 4:
			help(args);
			break;
		case 5:
			logLevel(args);
			break;
		case 6:
			quit(args);
			quit = true;
			break;
		case 0:
			println("Unknown command.");
		}
		
		return quit;
	}
	
	/**
	 * Connects to an echo server
	 * @param args: host, port
	 */
	private void connect(String args){
		String[] argv = args.split("\\s+");
		if(argv.length != 2)
		{
			log.error("Bad connect: wrong number of arguments");
			return;
		}
		
		if(client.connected()){
			disconnect("");
			log.warn("Was already connected, disconnected before connecting");
		}
		
		
		String address = argv[0];
		int port = -1;
		try{
			port = Integer.parseInt(argv[1]);
		}
		catch(NumberFormatException e)
		{
			log.error("Malformed port number");
			return;
		}
		
		String confirm = client.connectToServer(address, port);
		if(client.connected())
			println(confirm);
		else
			println("Connecting failed");
	}
	
	/**
	 * Disconnects from the server
	 * @param args: should be empty
	 */
	private void disconnect(String args){
		if(!args.trim().equals(""))
		{
			log.error("Disconnect doesn't take arguments");
		}
		if(client.connected()){
			client.disconnect();
			println("Connection terminated: "+client.host+" / "+client.port);
		}
		else
			println("No connection to terminate");
	}
	
	/**
	 * Sends a message and receives the echo
	 * @param args: The message to be sent
	 */
	private void send(String args){
		if(!client.connected())
		{
			println("Error: Not connected!");
		}
		else
		{
			client.writeMessage(args);
			String echo = client.readMessage();
			println(echo);
		}
	}
	
	/**
	 * Print help information
	 * @param args: should be empty, is ignored
	 */
	private void help(String args)
	{
		if(!args.trim().equals(""))
			log.warn("Help doesn't take arguments");
		
		println("Commands:");
		println("    connect <server> <port>");
		println("    send <message>");
		println("    disconnect");
		println("    help");
		println("    logLevel <logLevel: ALL|DEBUG|INFO|WARN|ERROR|FATAL|OFF>");
		println("    quit");
	}
	
	/**
	 * Sets the log level
	 * @param args: the new log level
	 */
	private void logLevel(String args)
	{
		if(args.trim().equals("ALL"))
			log.setLevel(Level.ALL);
		else if(args.equals("DEBUG"))
			log.setLevel(Level.DEBUG);
		else if(args.equals("INFO"))
			log.setLevel(Level.INFO);
		else if(args.equals("WARN"))
			log.setLevel(Level.WARN);
		else if(args.equals("ERROR"))
			log.setLevel(Level.ERROR);
		else if(args.equals("FATAL"))
			log.setLevel(Level.FATAL);
		else if(args.equals("OFF"))
			log.setLevel(Level.OFF);
		else
			log.warn("unknown logLevel");
	}
	
	/**
	 * Quits the CLI
	 * @param args: should be empty
	 */
	private void quit(String args)
	{
		if(! args.trim().equals(""))
			log.warn("Quit doesn't take arguments");
		if(client.connected)
			disconnect("");
		println("Application exit!");
	}
	
}
