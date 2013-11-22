package common.messages;

/**
 * Represents a simple text message for communication between client and server
 */
public class TextMessage implements Message {

	private String msg;
	private byte[] msgBytes;
	private static final char LINE_FEED = 0x0A;
	private static final char RETURN = 0x0D;
	
	/**
     * Constructs an empty TextMessage object
     */
	public TextMessage() {
		msgBytes = addCtrChars(new byte[0]);
		msg = new String(msgBytes).trim();
	}
	
    /**
     * Constructs a TextMessage object with a given array of bytes that 
     * forms the message.
     * 
     * @param bytes the bytes that form the message in ASCII coding.
     */
	public TextMessage(byte[] bytes) {
		msgBytes = addCtrChars(bytes);
		msg = new String(msgBytes).trim();
	}
	
	/**
     * Constructs a TextMessage object with a given String that
     * forms the message. 
     * 
     * @param msg the String that forms the message.
     */
	public TextMessage(String msg) {
		this.msg = msg;
		this.msgBytes = toByteArray(msg);
	}


	/**
	 * Returns the content of this TextMessage as a String.
	 * 
	 * @return the content of this message in String format.
	 */
	public String getMsg() {
		return msg.trim();
	}

	/**
	 * Set to the message encoded in an array of bytes
	 */
	public Message set(byte[] bytes) {
		msgBytes = addCtrChars(bytes);
		msg = new String(msgBytes).trim();
		return this;
	}
	
	/**
	 * Returns an array of bytes that represent the ASCII coded message content.
	 * 
	 * @return the content of this message as an array of bytes 
	 * 		in ASCII coding.
	 */
	public byte[] getBytes() {
		return msgBytes;
	}
	
	private byte[] addCtrChars(byte[] bytes) {
		byte[] ctrBytes = new byte[]{LINE_FEED, RETURN};
		byte[] tmp = new byte[bytes.length + ctrBytes.length];
		
		System.arraycopy(bytes, 0, tmp, 0, bytes.length);
		System.arraycopy(ctrBytes, 0, tmp, bytes.length, ctrBytes.length);
		
		return tmp;		
	}
	
	private byte[] toByteArray(String s){
		byte[] bytes = s.getBytes();
		byte[] ctrBytes = new byte[]{LINE_FEED, RETURN};
		byte[] tmp = new byte[bytes.length + ctrBytes.length];
		
		System.arraycopy(bytes, 0, tmp, 0, bytes.length);
		System.arraycopy(ctrBytes, 0, tmp, bytes.length, ctrBytes.length);
		
		return tmp;		
	}
	
}
