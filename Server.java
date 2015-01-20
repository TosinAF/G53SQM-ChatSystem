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
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements IServer {

    private static final AtomicInteger uniqueId = new AtomicInteger();

    private int port = 1025;
    private SimpleDateFormat simpleDateFormat;
    private boolean acceptConnections;
    private final ArrayList<ClientThread> clientThreads = new ArrayList<>();

    public Server() {
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    @Override
    public void start() {
        acceptConnections = true;

        try {

            ServerSocket serverSocket = new ServerSocket(port);

            while (acceptConnections) {

                display("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();

                if (!acceptConnections) {
                    break;
                }

                ClientThread clientThread = new ClientThread(socket);
                clientThread.start();
            }

            try {
                serverSocket.close();
                synchronized (clientThreads) {
                    for (ClientThread clientThread : clientThreads) {

                        try {
                            clientThread.sInput.close();
                            clientThread.sOutput.close();
                            clientThread.socket.close();
                        } catch (IOException e) {
                        }

                    }
                }

            } catch (Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        } catch (IOException e) {
            String msg = simpleDateFormat.format(new Date())
                    + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    public void stop() {
        acceptConnections = false;
    }

    public void broadcast(String username, String message) {

        message = username + ": " + message;

        String time = simpleDateFormat.format(new Date());
        String messageLf = "[" + time + "] " + message + "\n";

        System.out.print(messageLf);
        ArrayList<ClientThread> localList;
        synchronized (clientThreads) {
            localList = new ArrayList<>(clientThreads);
        }
        for (int i = localList.size(); --i >= 0;) {

            ClientThread clientThread = localList.get(i);

            if (!clientThread.sendMessage(new Message(Message.MessageProtocol.MESSAGE, messageLf))) {
                remove(clientThread.id);
                display("Disconnected Client " + clientThread.username
                        + " removed from list.");
            }
        }
    }

    public void remove(int id) {
        synchronized (clientThreads) {
            for (int i = 0; i < clientThreads.size(); ++i) {

                ClientThread clientThread = clientThreads.get(i);

                if (clientThread.id == id) {
                    clientThreads.remove(i);
                    return;
                }
            }
        }
    }

    public void display(String msg) {
        String time = simpleDateFormat.format(new Date()) + " " + msg;
        System.out.println(time);
    }

    @Override
    public void broadcast(Message message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class ClientThread extends Thread {

        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;

        int id;
        String username, date;
        volatile boolean authenticated = false;

        ClientThread(Socket socket) {

            id = uniqueId.incrementAndGet();
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
            Message message;

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
                        synchronized (clientThreads) {
                            for (ClientThread clientThread : clientThreads) {
                                if (clientThread.username.equals(givenUsername)) {
                                    foundUsername = true;
                                    break;
                                }
                            }
                        }

                        if (!foundUsername) {
                            authenticated = true;
                            username = text;
                            display(username + " logged in!");
                            sendMessage(new Message(Message.MessageProtocol.AUTHSTATUS, "Authenticated"));
                            synchronized (clientThreads) {
                                clientThreads.add(this);
                            }
                        } else {
                            sendMessage(new Message(Message.MessageProtocol.AUTHSTATUS, "notAuthenticated"));
                        }

                        break;

                    case AUTHSTATUS:
                        display("[ERROR]: Client should not be sending an auth message!");
                        break;

                    case MESSAGE:
                        if (!authenticated) {
                            sendMessage(new Message(Message.MessageProtocol.AUTHSTATUS, "notAuthenticated"));
                            return;
                        }
                        broadcast(username, text);
                        break;

                    case LOGOUT:
                        if (!authenticated) {
                            sendMessage(new Message(Message.MessageProtocol.AUTHSTATUS, "notAuthenticated"));
                            return;
                        }
                        display(username + " logged out");
                        isAlive = false;
                        break;

                    case LIST:
                        if (!authenticated) {
                            sendMessage(new Message(Message.MessageProtocol.AUTHSTATUS, "notAuthenticated"));
                            return;
                        }
                        String outputTitle = "List of the users connected:\n";
                        sendMessage(new Message(Message.MessageProtocol.MESSAGE, outputTitle));
                        ArrayList<ClientThread> localList;
                        synchronized (clientThreads) {
                            localList = new ArrayList<>(clientThreads);
                        }
                        for (int i = 0; i < localList.size(); ++i) {

                            ClientThread clientThread = localList.get(i);
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

                if (sOutput != null) {
                    sOutput.close();
                }
                if (sInput != null) {
                    sInput.close();
                }
                if (socket != null) {
                    socket.close();
                }

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
