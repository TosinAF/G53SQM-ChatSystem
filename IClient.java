/**
 * The Interface IClient.
 *
 * @author Tosin Afolabi
 * 
 * Interface for the Client class.
 */

public interface IClient {
	
	public boolean start();
	public void display(String msg);
	public void sendMessage(Message msg);
	public void disconnect();
}