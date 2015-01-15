/*
 * @author Tosin Afolabi
 * 
 * This class is
 * 
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Client client;
	private boolean connected;

	public boolean isConnected() {
		return connected;
	}

	public JTextField usernameTextField, inputTextField;
	public JButton loginToggleButton, listUsersButton, sendButton;
	public JTextArea chatTextArea;

	public ClientGUI() {

		super("G53SQM Chat Client");
		
		client = new Client(this);

		setupGUI();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);

		usernameTextField.requestFocus();
	}

	public static void main(String[] args) {
		new ClientGUI();
	}

	private void setupGUI() {

		usernameTextField = new JTextField("Enter Username ");

		loginToggleButton = new JButton("Login");
		loginToggleButton.addActionListener(this);

		listUsersButton = new JButton("List Users");
		listUsersButton.addActionListener(this);
		listUsersButton.setEnabled(false);

		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		sendButton.setEnabled(false);

		chatTextArea = new JTextArea("Welcome to the Chat room\n", 25, 40);
		chatTextArea.setEditable(false);

		inputTextField = new JTextField("Login to send a message...");
		inputTextField.setBackground(Color.WHITE);
		inputTextField.setEditable(false);

		JPanel northPanel = new JPanel();
		northPanel.add(new JLabel("Username: "));
		northPanel.add(usernameTextField);
		northPanel.add(loginToggleButton);
		northPanel.add(listUsersButton);

		JPanel centerPanel = new JPanel(new GridLayout(0, 1));
		centerPanel.add(new JScrollPane(chatTextArea));

		JPanel southPanel = new JPanel(new GridLayout(0, 1));
		southPanel.add(inputTextField);
		southPanel.add(sendButton);

		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	public void append(String str) {
		chatTextArea.append(str);
		chatTextArea.setCaretPosition(chatTextArea.getText().length() - 1);
	}

	public void connectionFailed() {
		connected = false;
		configureGUIForLoggedOutState();
	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == loginToggleButton) {
			
			System.out.println(connected);

			if (connected) {
				
				append("User has been successfully logged out.\n");
				client.sendMessage(new Message(Message.MessageProtocol.LOGOUT,
						""));
				return;

			} else {

				String username = usernameTextField.getText().trim();

				if (username.length() == 0) {
					append("[ERROR]: Please enter a valid username.");
					return;
				}
				
				
				if (!client.start())
					return;
				
				client.sendMessage(new Message(Message.MessageProtocol.LOGIN, username));
			}

			return;
		}

		if (source == listUsersButton) {
			client.sendMessage(new Message(Message.MessageProtocol.LIST, ""));
			return;
		}

		if (source == sendButton || source == inputTextField) {
			client.sendMessage(new Message(Message.MessageProtocol.MESSAGE,
					inputTextField.getText()));
			inputTextField.setText("");
			return;
		}
	}
	
	public void connectionSuccessful() {
		connected = true;
		configureGUIForLoggedInState();
	}

	private void configureGUIForLoggedOutState() {

		loginToggleButton.setText("Login");

		listUsersButton.setEnabled(false);
		sendButton.setEnabled(false);

		inputTextField.setText("Login to send a message...");
		inputTextField.removeActionListener(this);
	}

	private void configureGUIForLoggedInState() {

		usernameTextField.setEditable(false);

		loginToggleButton.setText("Logout");

		inputTextField.setEditable(true);
		inputTextField.setText("Enter your message here...");
		inputTextField.addActionListener(this);
		inputTextField.requestFocus();

		listUsersButton.setEnabled(true);
		sendButton.setEnabled(true);
	}
}
