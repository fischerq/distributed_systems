package ui;


import org.apache.log4j.Logger;

public class EchoClient extends Client {
	
	public static Logger log= Logger.getLogger(EchoClient.class);

	/**
	 * Connects to a echo server, prints confirmation message 
	 */
	public String connectToServer(String host, int port){
		super.connect(host, port);
		String confirm = "";
		if(connected){
			confirm = readMessage();
			log.info(confirm);
		}
		else
		{
			log.error("Connecting failed");
		}
		return confirm;
	}
	
	/**
	 * Closes the connection
	 */
	public void disconnect()
	{
		super.disconnect();
		log.trace("Disconnected");
	}
	
	/**
	 * Check the connection status
	 * @return true if connected, else false
	 */
	public boolean connected(){
		return connected;
	}
	
	/**
	 * Sends a message to the server
	 * @param s: message
	 */
	public void writeMessage(String s){
		write(s.getBytes());
	}
	
	/**
	 * Receives a Message
	 * @return Received message as string
	 */
	public String readMessage(){
		StringBuilder stringBuilder = new StringBuilder();
		byte[] read = read();
		for(int i = 0; i < read.length; ++i)
		{
			stringBuilder.append((char)read[i]);
		}
		log.trace("Read: "+stringBuilder.toString());
		return stringBuilder.toString();
	}
	
}
