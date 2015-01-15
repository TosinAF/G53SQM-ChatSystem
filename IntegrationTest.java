import static org.junit.Assert.*;

import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class IntegrationTest {
	
	Server server;

	@Before
	public void setUp() {
		server = new Server();
		server.start();
	}
	
	@Test
	public void testInabilityForMulitpleUsersWithSameUsername() {
		
		ClientGUI clientGUI = new ClientGUI();
		ClientGUI clientGUI2 = new ClientGUI();
		
		clientGUI.usernameTextField.setText("Tosin");
		clientGUI.loginToggleButton.doClick();
		
		delay(1);
		
		clientGUI2.usernameTextField.setText("Tosin");
		clientGUI2.loginToggleButton.doClick();
		
		String chatAreaContent = "Welcome to the Chat room"
				+ "\nConnection accepted on port 1025\n"
				+ "A User with that username already exists. Please pick another username.\n";
		
		delay(1);
		
		assertTrue(clientGUI2.chatTextArea.getText().equals(chatAreaContent));
		
		clientGUI.connectionFailed();
		clientGUI2.connectionFailed();
		
		clientGUI.dispatchEvent(new WindowEvent(clientGUI, WindowEvent.WINDOW_CLOSING));
		clientGUI2.dispatchEvent(new WindowEvent(clientGUI2, WindowEvent.WINDOW_CLOSING));
	}
	
	@Test
	public void testMulitpleConnectionsAndLogins() {
		
		ClientGUI clientGUI = new ClientGUI();
		ClientGUI clientGUI2 = new ClientGUI();
		
		String chatAreaContent = "Welcome to the Chat room"
				+ "\nConnection accepted on port 1025\n";
		
		clientGUI.usernameTextField.setText("Tosin");
		clientGUI.loginToggleButton.doClick();
		
		assertTrue(clientGUI.chatTextArea.getText().equals(chatAreaContent));
		
		clientGUI2.usernameTextField.setText("Ayo");
		clientGUI2.loginToggleButton.doClick();
		
		assertTrue(clientGUI2.chatTextArea.getText().equals(chatAreaContent));
		
		delay(1);
		
		chatAreaContent += "User has been authenticated!\n";
		assertTrue(clientGUI.chatTextArea.getText().equals(chatAreaContent));
		assertTrue(clientGUI2.chatTextArea.getText().equals(chatAreaContent));
		
		clientGUI.connectionFailed();
		clientGUI2.connectionFailed();
		
		clientGUI.dispatchEvent(new WindowEvent(clientGUI, WindowEvent.WINDOW_CLOSING));
		clientGUI2.dispatchEvent(new WindowEvent(clientGUI2, WindowEvent.WINDOW_CLOSING));
	}
	
	@Test
	public void testSingleUserConnectionToServerAndLogin() {
		
		ClientGUI clientGUI = new ClientGUI();
		
		String chatAreaContent = "Welcome to the Chat room"
				+ "\nConnection accepted on port 1025\n";
		
		clientGUI.usernameTextField.setText("Tosin");
		clientGUI.loginToggleButton.doClick();
		
		assertTrue(clientGUI.chatTextArea.getText().equals(chatAreaContent));
		
		delay(1);
		
		chatAreaContent += "User has been authenticated!\n";
		assertTrue(clientGUI.chatTextArea.getText().equals(chatAreaContent));
		
		clientGUI.connectionFailed();
		clientGUI.dispatchEvent(new WindowEvent(clientGUI, WindowEvent.WINDOW_CLOSING));
	}
	
	@Test
	public void testSendMessageToChatRoom() {
		
		ClientGUI clientGUI = new ClientGUI();
		
		String username = "Tosin";
		String msg = "Hello People";
		String chatAreaContent = "Welcome to the Chat room"
				+ "\nConnection accepted on port 1025\n" 
				+ "User has been authenticated!\n";
		
		clientGUI.usernameTextField.setText("Tosin");
		clientGUI.loginToggleButton.doClick();
		
		delay(1);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		
		clientGUI.inputTextField.setText(msg);
		clientGUI.sendButton.doClick();
		
		delay(2);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.SECOND, -2);
		Date twoSecondsBack = cal.getTime();
		
		String time = simpleDateFormat.format(twoSecondsBack);
		chatAreaContent += "[" + time + "] " + username + ": " + msg + "\n";
		
		assertTrue(clientGUI.chatTextArea.getText().equals(chatAreaContent));
		
		clientGUI.connectionFailed();
		clientGUI.dispatchEvent(new WindowEvent(clientGUI, WindowEvent.WINDOW_CLOSING));
	}
	
	@Test
	public void testConversationInChatRoom() {
		
		ClientGUI clientGUI = new ClientGUI();
		ClientGUI clientGUI2 = new ClientGUI();
		
		String username1 = "Tosin";
		String username2 = "Ayo";
		
		clientGUI.usernameTextField.setText(username1);
		clientGUI.loginToggleButton.doClick();
		
		clientGUI2.usernameTextField.setText(username2);
		clientGUI2.loginToggleButton.doClick();
		
		delay(1);
		
		String msg = "Hello People";
		String msg2 = "Hi";
		String msg3 = "How are you?";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		
		String chatAreaContent = "Welcome to the Chat room"
				+ "\nConnection accepted on port 1025\n"
				+ "User has been authenticated!\n";
		
		clientGUI.inputTextField.setText(msg);
		clientGUI.sendButton.doClick();
		
		String time = simpleDateFormat.format(new Date());
		chatAreaContent += "[" + time + "] " + username1 + ": " + msg + "\n";
		
		clientGUI2.inputTextField.setText(msg2);
		clientGUI2.sendButton.doClick();
		
		chatAreaContent += "[" + time + "] " + username2 + ": " + msg2 + "\n";
		
		clientGUI.inputTextField.setText(msg3);
		clientGUI.sendButton.doClick();
		
		chatAreaContent += "[" + time + "] " + username1 + ": " + msg3 + "\n";
		
		delay(2);
		
		System.out.println("chat is" + chatAreaContent);
		System.out.println("other c is " + clientGUI.chatTextArea.getText());
		
		assertTrue(clientGUI.chatTextArea.getText().equals(chatAreaContent));
		assertTrue(clientGUI2.chatTextArea.getText().equals(chatAreaContent));
		
		clientGUI.connectionFailed();
		clientGUI2.connectionFailed();
		
		clientGUI.dispatchEvent(new WindowEvent(clientGUI, WindowEvent.WINDOW_CLOSING));
		clientGUI2.dispatchEvent(new WindowEvent(clientGUI2, WindowEvent.WINDOW_CLOSING));
	}
	
	@Test
	public void testUserLogout() {
		
		ClientGUI clientGUI = new ClientGUI();
		ClientGUI clientGUI2 = new ClientGUI();
		
		String username1 = "Tosin";
		String username2 = "Ayo";
		
		clientGUI.usernameTextField.setText(username1);
		clientGUI.loginToggleButton.doClick();
		
		clientGUI2.usernameTextField.setText(username2);
		clientGUI2.loginToggleButton.doClick();
		
		delay(1);
		
		String chatAreaContent = "Welcome to the Chat room"
				+ "\nConnection accepted on port 1025\n"
				+ "User has been authenticated!\n"
				+ "User has been successfully logged out.\n"
				+ "The server has closed the connection. Please login or restart the client.\n";
		
		clientGUI.usernameTextField.setText("Tosin");
		clientGUI.loginToggleButton.doClick();
		clientGUI.loginToggleButton.doClick();
		
		delay(1);
		
		assertTrue(clientGUI.chatTextArea.getText().equals(chatAreaContent));
		
		clientGUI.connectionFailed();
		clientGUI.dispatchEvent(new WindowEvent(clientGUI, WindowEvent.WINDOW_CLOSING));
	}
	
	@Test
	public void testListingUsers() {
		
		ClientGUI clientGUI = new ClientGUI();
		ClientGUI clientGUI2 = new ClientGUI();
		
		String username1 = "Tosin";
		String username2 = "Ayo";
		
		clientGUI.usernameTextField.setText(username1);
		clientGUI.loginToggleButton.doClick();
		
		clientGUI2.usernameTextField.setText(username2);
		clientGUI2.loginToggleButton.doClick();
		
		clientGUI.listUsersButton.doClick();
		
		String chatAreaContent = "Welcome to the Chat room"
				+ "\nConnection accepted on port 1025\n"
				+ "User has been authenticated!\n"
				+ "List of the users connected:\n"
				+ "1) " + username1 + "\n"
				+ "2) " + username2 + "\n";
		
		delay(1);
		
		assertTrue(clientGUI.chatTextArea.getText().equals(chatAreaContent));
		
		clientGUI.connectionFailed();
		clientGUI.dispatchEvent(new WindowEvent(clientGUI, WindowEvent.WINDOW_CLOSING));
	}
	
	@After
	public void tearDown() {
		server.stop();
	}
	
	public void delay(int s) {
		try {
		    Thread.sleep(s * 1000L);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}	
	}

}
