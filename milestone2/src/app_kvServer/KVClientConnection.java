package app_kvServer;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import common.messages.KVMessage.StatusType;
import common.messages.KVNetworkMessage;
import common.messages.Message;
import server.ClientConnection;

public class KVClientConnection extends ClientConnection {
	
	private static Logger logger = Logger.getRootLogger();
	
	private KVDatabase db;
	public KVClientConnection(Socket clientSocket, KVDatabase db) {
		super(clientSocket);
		this.db = db;
	}
	

	@Override
	protected void onOpen() throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onMessage(Message msg) throws IOException {
		if (msg instanceof KVNetworkMessage) {
			KVNetworkMessage request = (KVNetworkMessage)msg;
			if(request.getStatus() == null)
				throw new IOException("Received bad message");
			logger.info("Received message: "+request.getStatus().name()+", key: "+request.getKey()+" value: "+request.getValue());
			KVNetworkMessage response = null;
			switch(request.getStatus()){
			case GET:
				if(db.containsKey(request.getKey()))
					response = new KVNetworkMessage(StatusType.GET_SUCCESS, request.getKey(), db.get(request.getKey()));
				else
					response = new KVNetworkMessage(StatusType.GET_ERROR, request.getKey(), "");
				break;
			case PUT:
				if(request.getValue().equals("null")) {
					if (db.delete(request.getKey()))
						response = new KVNetworkMessage(StatusType.DELETE_SUCCESS, request.getKey(), request.getValue());
					else
						response = new KVNetworkMessage(StatusType.DELETE_ERROR, request.getKey(), request.getValue());
				}
				else { 
					if(db.containsKey(request.getKey())){
						if (db.put(request.getKey(), request.getValue()))
							response = new KVNetworkMessage(StatusType.PUT_UPDATE, request.getKey(), request.getValue());
						else
							response = new KVNetworkMessage(StatusType.PUT_ERROR, request.getKey(), request.getValue());
					}
					else {
						if (db.put(request.getKey(), request.getValue()))
							response = new KVNetworkMessage(StatusType.PUT_SUCCESS, request.getKey(), request.getValue());
						else
							response = new KVNetworkMessage(StatusType.PUT_ERROR, request.getKey(), request.getValue());
					}	
				}
				break;
			default:
				response = new KVNetworkMessage();
				break;
			}
			sendMessage(response);
			logger.info("Sent response: "+response.getStatus().name()+" key: "+response.getKey()+" value: "+response.getValue());
		}
		else {
		   throw new IOException("Received bad message");
		}
		
	}

	@Override
	protected void onClose() throws IOException {
		
	}

	@Override
	protected Message createMessage() {
		return new KVNetworkMessage();
	}



}
