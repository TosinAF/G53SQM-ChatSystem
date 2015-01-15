/*
 * @author Tosin Afolabi
 * 
 * This class is
 * 
 */

import java.net.*;
import java.io.*;

public class Client implements IClient {
	
	private static final int port = 1025;
	private static final String server = "localhost";

	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;		
	private Socket socket;

	private ClientGUI gui;
	
	public Client(ClientGUI gui) {
		this.gui = gui;
	}
	
	public boolean start() {
		
		try {
			
			socket = new Socket(server, port);
			
		} catch(Exception e) {
			
			display("There was an error connecting to the server :(");
			
			System.err.println("Error connecting to server.\nStack Trace Shown Below.");
			e.printStackTrace();
			
			return false;
		}
		
		String msg = "Connection accepted on port " + socket.getPort();
		display(msg);
	
		try {
			
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			
			display("A system error occurred, please restert the client");
			
			System.err.println("Error creating input/output streams.\nStack Trace Shown Below.");
			e.printStackTrace();
			
			return false;
		}

		new ServerListener().start();
		
		return true;
	}

	public void display(String msg) {
		gui.append(msg + "\n");
	}
	
	public void sendMessage(Message msg) {
		
		try {
			
			sOutput.writeObject(msg);
			
		} catch(IOException e) {
			
			display("The server has closed the connection. Please login or restart the client.");
			
			System.err.println("Error sending a message to the server.\nStack Trace Shown Below.");
			e.printStackTrace();
			
			disconnect();
		}
	}

	public void disconnect() {
		
		try {
			
			if(sInput != null) sInput.close();
			if(sOutput != null) sOutput.close();
			if(socket != null) socket.close();
			
		} catch(Exception e) {}
		
		gui.connectionFailed();		
	}

	class ServerListener extends Thread {

		public void run() {
			
			while(true) {
				
				try {
					
					Message message = (Message) sInput.readObject();
					
					if ( message.getType() == Message.MessageProtocol.AUTHSTATUS ) {
						if ( message.getText().equals("Authenticated") ) {
							gui.connectionSuccessful();
							gui.append("User has been authenticated!\n");
						} else {
							gui.append("A User with that username already exists. Please pick another username.\n");
							break;
						}
					} 
					
					if ( message.getType() == Message.MessageProtocol.MESSAGE ) {
						gui.append(message.getText());
					}
					
					
				} catch(IOException e) {
					
					display("The server has closed the connection. Please login or restart the client.");
			
					System.err.println("Error the connection has been closed by the server.\nStack Trace Shown Below.");
					e.printStackTrace();
					
					gui.connectionFailed();
					break;
					
				} catch(ClassNotFoundException e2) {}
			}
		}
	}
}

