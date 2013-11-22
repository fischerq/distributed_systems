package common.messages;

import org.apache.log4j.Logger;

public class KVNetworkMessage extends TextMessage implements KVMessage {
	
	private StatusType status;
	private String key;
	private String value;
	protected static Logger logger = Logger.getRootLogger();
	
	public KVNetworkMessage() {
		super();
		status = null;
		key = null;
		value = null;
	}
	
	public KVNetworkMessage(StatusType status, String key, String value) {
		super(status.name()+" "+key+" "+value);
		this.status = status;
		this.key = key;
		this.value = value;
	}
	
	public Message set(byte[] bytes) {
		TextMessage msg = (TextMessage) super.set(bytes);
		String[] parts = msg.getMsg().split(" ",3);

		if(parts.length >= 2){
			status = StatusType.valueOf(parts[0]);
			key = parts[1];
		}
		else {
			logger.error("Tried to parse bad Message");
			status = null;
			key = null;
		}

		if(parts.length >= 3)
			value = parts[2].trim();
		else 
			value = "";
		if(parts.length >3)
		{
			logger.error("Something wemt wrong parsing message");
		}
		return this;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public StatusType getStatus() {
		return status;
	}

}
