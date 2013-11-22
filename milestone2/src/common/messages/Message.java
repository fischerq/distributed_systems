package common.messages;

public interface Message {

	/**
	 * Set to the message encoded in an array of bytes
	 * @return TODO
	 */
	public Message set(byte[] bytes);
	
	/**
	 * Returns an array of bytes that represent the ASCII coded message content.
	 * 
	 * @return the content of this message as an array of bytes 
	 * 		in ASCII coding.
	 */
	public byte[] getBytes();
	
}
