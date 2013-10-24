package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class Application {
	private Socket socket;
	private InputStream streamIn;
	private OutputStream streamOut;
	
	private boolean connected;
	private static Logger log= Logger.getLogger(Application.class);
	
	public Application()
	{
		connected = false;
		log.setLevel(Level.ALL);
		SimpleLayout sL = new SimpleLayout();
		ConsoleAppender cA = new ConsoleAppender(sL);
		log.addAppender(cA);
		FileAppender fA;
		try {
			fA = new FileAppender(sL,"log/client.log", true);
			log.addAppender(fA);
		} catch (IOException e) {
			System.out.println("Couldn't open log file");
			log.error(e.toString());
		}

	}
	
	public static void main(String[] args)
	{
		Application app = new Application();
		BufferedReader cons = new BufferedReader(new InputStreamReader(System.in));
		
		boolean quit = false;
		
		while(!quit)
		{
			System.out.print("Prompt> ");
			String input;
			try {
				input = cons.readLine();
				quit = app.executeCommand(input);
			} catch (IOException e) {
				log.error(e.toString());
			}
			
		}
	}
	
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
			log.error("Error! Unknown command.");
		}
		
		return quit;
	}
	
	private String read()
	{
		StringBuilder buf = new StringBuilder();
		boolean reading = true;
		while(reading)
		{
			char c;
			try {
				c = (char)(streamIn.read());
				if(c != '\r')
					buf.append(c);
				else
					reading = false;
			} catch (IOException e) {
				
				log.error(e.toString());
				reading = false;
			}
		}
		log.trace("Read from socket: "+buf.toString());
		return buf.toString();
	}
	
	private void write(String s)
	{
		byte[] data = s.getBytes();
		
		for(int i = 0; i < data.length; ++i)
		{
			try {
				streamOut.write(data[i]);
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
		try {
			streamOut.write('\r');
			streamOut.flush();
		} catch (IOException e) {
			
			log.error(e.toString());
		}
		log.trace("Wrote to socket: "+s);
	}
	
	private void connect(String args)
	{
		String[] argv = args.split("\\s+");
		if(argv.length != 2)
		{
			log.error("Bad connect: wrong number of arguments");
			return;
		}
		
		if(connected)
			disconnect("");
		
		String address = argv[0];
		int port = -1;
		try{
			port = Integer.parseInt(argv[1]);
		}
		catch(NumberFormatException e)
		{
			log.error("Bad port number");
			return;
		}
		
		try {
			socket = new Socket(address, port);
			streamIn = socket.getInputStream();
			streamOut = socket.getOutputStream();
			
			String confirm =read();
			log.info(confirm);
			System.out.print(confirm);
			connected = true;
			
		} catch (UnknownHostException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}
	}
	
	private void send(String args)
	{
		if(!connected)
		{
			log.error("Error: Not connected!");
		}
		else
		{
			write(args);
			String echo = read();
			System.out.print(echo);
		}
	}
	
	private void disconnect(String args)
	{
		if(!connected)
		{
			log.warn("Not connected");
			return;
		}
		
		if(!args.trim().equals(""))
		{
			log.error("Error: disconnect doesn't take arguments");
		}
		
		try {
			streamIn.close();
			streamOut.close();
			String host = socket.getInetAddress().toString();
			int port = socket.getPort();
			socket.close();
			
			log.info("Connection terminated: "+host+" / "+port);
			System.out.println("Connection terminated: "+host+" / "+port);
			
		} catch (IOException e) {
			log.error(e.toString());
		}

		connected = false;
	}
	private void help(String args)
	{
		System.out.println("Commands:");
		System.out.println("connect <server> <port>");
		System.out.println("send <message>");
		System.out.println("disconnect");
		System.out.println("help");
		System.out.println("logLevel <logLevel: ALL|DEBUG|INFO|WARN|ERROR|FATAL|OFF>");
		System.out.println("quit");
	}
	private void logLevel(String args)
	{
		if(args.equals("ALL"))
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
	private void quit(String args)
	{
		if(! args.equals(""))
			log.warn("Error: quit doesn't take arguments");
		disconnect("");
		System.out.println("Application exit!");
	}
}
