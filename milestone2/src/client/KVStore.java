package client;

import java.io.IOException;
import java.net.UnknownHostException;


import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.messages.KVNetworkMessage; 


public class KVStore implements KVCommInterface{
	
	private KVConnection connection = null;
	private String address;
	private int port;
	private boolean connected = false;
	
	public KVStore(String address, int port) {

		this.address= address;
		this.port = port;
	}
	public void connect()throws UnknownHostException,
			IOException {
		connection = new KVConnection(address, port);
		connection.connect();
		connected = true;
	}
	
	@Override
	public void disconnect() {
		if(connected)
			connection.disconnect();
		connected = false;
	}
	
	@Override
	public KVMessage put(String key, String value) throws Exception {
		if(connected)
			connection.sendMessage(new KVNetworkMessage(StatusType.PUT, key, value));
		else throw new Exception("Not connected");
		return (KVNetworkMessage)connection.receiveMessage();
	}

	@Override
	public KVMessage get(String key) throws Exception {
		if(connected)
			connection.sendMessage(new KVNetworkMessage(StatusType.GET, key, ""));
		else throw new Exception("Not connected");
		return (KVNetworkMessage)connection.receiveMessage();
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	

}
