package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import common.messages.Message;

public abstract class Connection {
	
	private Logger logger = Logger.getRootLogger();
	private Socket clientSocket;
	private OutputStream output;
 	private InputStream input;
	private boolean connected = false; 
	
	private static final int BUFFER_SIZE = 1024;
	private static final int DROP_SIZE = 1024 * BUFFER_SIZE;
	
	public Connection(String address, int port) 
			throws UnknownHostException, IOException {
		
		clientSocket = new Socket(address, port);
		logger.info("Connection established");
	}
	public void connect() {
		try {
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
			connected = true;
		} catch (IOException ioe) {
			logger.error("Connection could not be established!");
			
		}
	}
	
	public void disconnect(){
		try {
			logger.info("tearing down the connection ...");
			if (clientSocket != null) {
				input.close();
				output.close();
				clientSocket.close();
				clientSocket = null;
				connected = false;
				logger.info("connection closed!");
			}
		} catch (IOException ioe) {
			logger.error("Unable to close connection!");
		}
	}
	
	public boolean isConnected(){
		return connected;
	}
	
	/**
	 * Creates a new message
	 * @return an empty message
	 */
	protected abstract Message createMessage();
	
	/**
	 * Method sends a TextMessage using this socket.
	 * @param msg the message that is to be sent.
	 * @throws IOException some I/O error regarding the output stream 
	 */
	public void sendMessage(Message msg) throws IOException {
		byte[] msgBytes = msg.getBytes();
		output.write(msgBytes, 0, msgBytes.length);
		output.flush();
		logger.info("Send message:\t '" + new String(msg.getBytes()).trim() + "'");
    }
	
	
	protected Message receiveMessage() throws IOException {
		
		int index = 0;
		byte[] msgBytes = null, tmp = null;
		byte[] bufferBytes = new byte[BUFFER_SIZE];
		
		/* read first char from stream */
		byte read = (byte) input.read();	
		boolean reading = true;
		
		while(read != 13 && reading) {/* carriage return */
			/* if buffer filled, copy to msg array */
			if(index == BUFFER_SIZE) {
				if(msgBytes == null){
					tmp = new byte[BUFFER_SIZE];
					System.arraycopy(bufferBytes, 0, tmp, 0, BUFFER_SIZE);
				} else {
					tmp = new byte[msgBytes.length + BUFFER_SIZE];
					System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
					System.arraycopy(bufferBytes, 0, tmp, msgBytes.length,
							BUFFER_SIZE);
				}

				msgBytes = tmp;
				bufferBytes = new byte[BUFFER_SIZE];
				index = 0;
			} 
			
			/* only read valid characters, i.e. letters and numbers */
			if((read > 31 && read < 127)) {
				bufferBytes[index] = read;
				index++;
			}
			
			/* stop reading is DROP_SIZE is reached */
			if(msgBytes != null && msgBytes.length + index >= DROP_SIZE) {
				reading = false;
			}
			
			/* read next char from stream */
			read = (byte) input.read();
		}
		
		if(msgBytes == null){
			tmp = new byte[index];
			System.arraycopy(bufferBytes, 0, tmp, 0, index);
		} else {
			tmp = new byte[msgBytes.length + index];
			System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
			System.arraycopy(bufferBytes, 0, tmp, msgBytes.length, index);
		}
		
		msgBytes = tmp;
		
		/* build final String */
		Message msg = createMessage();
		msg.set(msgBytes);
		logger.info("Receive message:\t '" + new String(msg.getBytes()) + "'");
		return msg;
    }
}
