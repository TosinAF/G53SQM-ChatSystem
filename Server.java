/**
 * The Class Server.
 *
 * @author Yves Findlay
 * 
 * Implementation for the Server class.
 */

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server implements IServer {

	private static int uniqueId;

	private int port = 1025;
	private SimpleDateFormat simpleDateFormat;
	private boolean acceptConnections;
	private ArrayList<ClientThread> clientThreads;

	public Server() {
		simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		clientThreads = new ArrayList<ClientThread>();
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

	public void start() {
		acceptConnections = true;

		try {

			ServerSocket serverSocket = new ServerSocket(port);

			while (acceptConnections) {

				display("Server waiting for Clients on port " + port + ".");

				Socket socket = serverSocket.accept();

				if (!acceptConnections)
					break;

				ClientThread clientThread = new ClientThread(socket);
				clientThread.start();
			}

			try {
				serverSocket.close();
				
				for (ClientThread clientThread : clientThreads) {
					
					try {
						clientThread.sInput.close();
						clientThread.sOutput.close();
						clientThread.socket.close();
					} catch (IOException e) {
					}
					
				}

			} catch (Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}

		catch (IOException e) {
			String msg = simpleDateFormat.format(new Date())
					+ " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}

	public void stop() {
		acceptConnections = false;

		try {

			new Socket("localhost", port);

		} catch (Exception e) {
		}
	}

	public synchronized void broadcast(String message) {

		String time = simpleDateFormat.format(new Date());
		String messageLf = "[" + time + "] " + message + "\n";

		System.out.print(messageLf);

		for (int i = clientThreads.size(); --i >= 0;) {

			ClientThread clientThread = clientThreads.get(i);

			if (!clientThread.sendMessage(new Message(Message.MessageProtocol.MESSAGE, messageLf))) {
				clientThreads.remove(i);
				display("Disconnected Client " + clientThread.username
						+ " removed from list.");
			}
		}
	}

	public synchronized void remove(int id) {

		for (int i = 0; i < clientThreads.size(); ++i) {

			ClientThread clientThread = clientThreads.get(i);

			if (clientThread.id == id) {
				clientThreads.remove(i);
				return;
			}
		}
	}

	public void display(String msg) {
		String time = simpleDateFormat.format(new Date()) + " " + msg;
		System.out.println(time);
	}

	class ClientThread extends Thread {

		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;

		int id;
		Message message;
		String username, date;
		
		ClientThread(Socket socket) {

			id = ++uniqueId;
			this.socket = socket;

			System.out
					.println("Thread trying to create Object Input/Output Streams");

			try {

				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());

				display("New connection was made!");

			} catch (IOException e) {

				display("Exception creating new Input/output Streams: " + e);
				return;
			}

			date = new Date().toString() + "\n";
		}

		public void run() {

			boolean isAlive = true;

			while (isAlive) {

				try {

					message = (Message) sInput.readObject();

				} catch (IOException e) {

					display(username + " Exception reading Streams: " + e);
					break;

				} catch (ClassNotFoundException e2) {

					break;
				}

				String text = message.getText();

				switch (message.getType()) {
				
				case LOGIN:
					
					String givenUsername = text;
					
					Boolean foundUsername = false;
					for (ClientThread clientThread : clientThreads) {
						if ( clientThread.username.equals(givenUsername) ) {
							sendMessage(new Message(Message.MessageProtocol.AUTHSTATUS, "notAuthenticated"));
							foundUsername = true;
							break;
						}
					}
					
					if (!foundUsername) {
						username = text;
						display(username + " logged in!");
						sendMessage(new Message(Message.MessageProtocol.AUTHSTATUS, "Authenticated"));
						clientThreads.add(this);
					}
					
					break;
					
				case AUTHSTATUS:
					display("[ERROR]: Client should not be sending an auth message!");
					break;
					
				case MESSAGE:
					
					broadcast(username + ": " + text);
					break;

				case LOGOUT:
					
					display(username + " logged out");
					isAlive = false;
					break;

				case LIST:
					
					String outputTitle = "List of the users connected:\n";
					sendMessage(new Message(Message.MessageProtocol.MESSAGE, outputTitle));
					
					for (int i = 0; i < clientThreads.size(); ++i) {
						
						ClientThread clientThread = clientThreads.get(i);
						String outputLine = (i + 1) + ") " + clientThread.username + "\n";
						sendMessage(new Message(Message.MessageProtocol.MESSAGE, outputLine));	
					}
					break;
				}
			}

			remove(id);
			close();
		}


		private void close() {

			try {

				if (sOutput != null)
					sOutput.close();
				if (sInput != null)
					sInput.close();
				if (socket != null)
					socket.close();

			} catch (Exception e) {
			}
		}
		
		private boolean sendMessage(Message msg) {

			if (!socket.isConnected()) {
				close();
				return false;
			}

			try {
				sOutput.writeObject(msg);
			} catch (IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}

			return true;
		}
	}
}
