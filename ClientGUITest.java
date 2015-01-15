import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.awt.Dimension;
import org.junit.Before;
import org.junit.Test;


public class ClientGUITest {
	
	ClientGUI clientGUI;
	
	@Before
	public void setUp() {
		clientGUI = new ClientGUI();
	}

	@Test
	public void testMenuBarTitle() {
		assertEquals("G53SQM Chat Client", clientGUI.getTitle());
	}
	
	@Test
	public void testFrameSize() { 
        Dimension d = clientGUI.getSize();
        assertTrue(d.width == 600);
        assertTrue(d.height == 600);
    }
	
	@Test
	public void testChatAreaIsNotEditableByUser() {
		assertTrue(clientGUI.chatTextArea.isEditable() == false);
	}
	
	@Test
	public void testGUILoggedOutState() {
		
		assertTrue(clientGUI.isConnected() == false);
		
		assertTrue(clientGUI.usernameTextField.isEditable() == true);
		assertTrue(clientGUI.inputTextField.isEditable() == false);
		assertTrue(clientGUI.inputTextField.getText().equals("Login to send a message..."));
		
		assertTrue(clientGUI.loginToggleButton.isEnabled() == true);
		assertTrue(clientGUI.loginToggleButton.getText() == "Login");
		assertTrue(clientGUI.listUsersButton.isEnabled() == false);
		assertTrue(clientGUI.sendButton.isEnabled() == false);
		
		assertTrue(clientGUI.chatTextArea.isEditable() == false);
		
    }
	
	@Test
	public void testGUILoggeInState() {
		
		clientGUI.connectionSuccessful();
		
		assertTrue(clientGUI.isConnected() == true);
		
		assertTrue(clientGUI.usernameTextField.isEditable() == false);
		assertTrue(clientGUI.inputTextField.isEditable() == true);
		assertTrue(clientGUI.inputTextField.getText().equals("Enter your message here..."));
		
		assertTrue(clientGUI.loginToggleButton.isEnabled() == true);
		assertTrue(clientGUI.loginToggleButton.getText() == "Logout");
		assertTrue(clientGUI.listUsersButton.isEnabled() == true);
		assertTrue(clientGUI.sendButton.isEnabled() == true);
		
		assertTrue(clientGUI.chatTextArea.isEditable() == false);
		
    }

}
