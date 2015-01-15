import static org.junit.Assert.*;
import org.junit.Test;

public class MessageTest {
	
	Message message;

	@Test
	public void testLOGINProtocol() {
		message = new Message(Message.MessageProtocol.LOGIN, "username");
		assertEquals(Message.MessageProtocol.LOGIN, message.getType());
		assertEquals("username", message.getText());	
	}
	
	@Test
	public void testLISTProtocol() {
		message = new Message(Message.MessageProtocol.LIST, "");
		assertEquals(Message.MessageProtocol.LIST, message.getType());
		assertEquals("", message.getText());	
	}
	
	@Test
	public void testLOGOUTProtocol() {
		message = new Message(Message.MessageProtocol.LOGOUT, "");
		assertEquals(Message.MessageProtocol.LOGOUT, message.getType());
		assertEquals("", message.getText());	
	}
	
	@Test
	public void testAUTHSTATUSProtocol() {
		message = new Message(Message.MessageProtocol.AUTHSTATUS, "Authenticated");
		assertEquals(Message.MessageProtocol.AUTHSTATUS, message.getType());
		assertEquals("Authenticated", message.getText());	
	}

}
