/*
 * @author Tosin Afolabi
 * 
 * This class defines the communication protocal 
 * that will be used between the Server & Client
 * 
 * The use of an object eliminates the need to count bytes or parse strings.
 */

import java.io.*;

public class Message implements Serializable {
	
	public enum MessageProtocol {
		
		// Login - Login a user
		// List - List of users connected
		// Message - Text Message sent by user to server
		// Logout - To disconnect from the server
		LOGIN, MESSAGE, LIST, LOGOUT, AUTHSTATUS;
	}

	// Lol, thats 42 (Binary), as that's the answer to life, the universe and everything ;)
	private static final long serialVersionUID = 101010L;
	private MessageProtocol type;
	private String text;

	public Message(MessageProtocol type, String text) {
		this.type = type;
		this.text = text;
	}
	
	public MessageProtocol getType() {
		return type;
	}
	
	public String getText() {
		return text;
	}
}
