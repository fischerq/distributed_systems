package client;

import java.io.IOException;
import java.net.UnknownHostException;

import common.messages.KVNetworkMessage;
import common.messages.Message;

public class KVConnection extends Connection {

	public KVConnection(String address, int port) throws UnknownHostException,
			IOException {
		super(address, port);
	}

	@Override
	protected Message createMessage() {
		return new KVNetworkMessage();
	}

}
