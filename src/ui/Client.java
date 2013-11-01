package ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class Client {
	
	protected String host;
	protected int port;
	protected boolean connected;
	
	private Socket socket;
	private InputStream streamIn;
	private OutputStream streamOut;

	public static Logger log= Logger.getLogger(Client.class);
	
	public Client(){
		connected = false;
	}
	
	/**
	 * Opens a connection to the server
	 * @param host: Address of the server
	 * @param port: Port to connect to
	 */
	public void connect(String host, int port){
		if(port <0 || port >  65535)
		{
			log.error("Invalid port "+port);
			return;
		}

		try {
			socket = new Socket(host, port);
			streamIn = socket.getInputStream();
			streamOut = socket.getOutputStream();
			
			this.host = socket.getInetAddress().toString();
			this.port = socket.getPort();
			connected = true;
			
		} catch (UnknownHostException e) {
			log.error(e.toString());
		} catch (IOException e) {
			log.error(e.toString());
		}
		
	}
	
	/**
	 * Closes the current connection
	 */
	public void disconnect(){
		//Nothing to do
		if(!connected){
			log.warn("Tried to close a closed connection.");
			return;
		}
		try {
			streamIn.close();
			streamOut.close();
			socket.close();
			
			log.info("Connection terminated: "+host+" / "+port);
			
		} catch (IOException e) {
			log.error(e.toString());
		}
		connected = false;
	}
	
	/**
	 * Receives a sequence of bytes from the server	
	 * @return the data received
	 */
	public byte[] read()
	{
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		boolean reading = true;
		int read = 0;
		while(reading)
		{
			byte b;
			try {
				b = (byte) streamIn.read();
				if((char)b != '\r'){
					buffer[read] = b;
					read++;
				}
				else
					reading = false;
				if(read == bufferSize){
					byte[] tmpBuffer = new byte[bufferSize*2];
					System.arraycopy(buffer, 0, tmpBuffer, 0, bufferSize);
					bufferSize *= 2;
					buffer = tmpBuffer;
				}
			} catch (IOException e) {
				log.error(e.toString());
				reading = false;
			}
		}
		log.trace("Read from socket (length): "+read);
		byte[] result = new byte[read];
		System.arraycopy(buffer, 0, result, 0, read);
		return result;
	}
	
	/**
	 * Sends a sequence of bytes to the server
	 * @param data: the data to be sent
	 */
	public void write(byte[] data)
	{		
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
	}
}
