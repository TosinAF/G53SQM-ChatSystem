/**
 * The Interface IServer.
 *
 * @author Yves Findlay
 * 
 * Interface for the Server class.
 */

public interface IServer {
	
	public void start();
	public void stop();
	public void broadcast(String message);
	public void remove(int id);
	public void display(String msg);
	
}