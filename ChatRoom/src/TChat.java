import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet.CharacterAttribute;
import javax.swing.text.DefaultCaret;

/**
 * TChat is the client side of the program used by those wishing to connect to
 * the chat server
 * 
 * @author Terrance Rose Jr.
 *
 */
public class TChat extends JPanel {

	private JFrame mainFrame_ = new JFrame();// window that holds the panel displaying the chat
	private JPanel mainMenu;
	private static ChatClient client_;// the client's connection to the chat server
	private JTextField textBox_;// holds text entered by client
	private static JTextArea chatBox_;// area where chat messages are displayed
	private final static String newline = "\n";// for making sure chat messages appear under each other
	private static JScrollPane pane_;// panel that holds the chatBox so chat auto scrolls when messages reach bottom
										// of chat window

	/**
	 * Constructor that generates the window for client to see and write in chat.
	 * 
	 * @param hostID
	 *            ip address of the chat server
	 * @param port
	 *            port number of the chat server
	 */
	public TChat(String hostID, int port) {
		super();
		mainFrame_.setLayout(new BorderLayout());
		mainFrame_.setSize(500, 600);
		mainFrame_.setLocationRelativeTo(null);
		mainFrame_.setTitle("TChat");
		mainFrame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame_.addWindowListener(new WindowClosed());
		mainMenu = new JPanel();
		mainMenu.setLayout(new BorderLayout());
		mainMenu.setBackground(new Color(0x77dd77));

		JButton button = new JButton("send");
		button.addMouseListener(new SendClickMouseListener());
		textBox_ = new JTextField("Enter your text.");
		textBox_.addKeyListener(new KeyPressedActionListener());
		chatBox_ = new JTextArea("Messages appear here.\n");
		chatBox_.setLineWrap(true);
		chatBox_.setWrapStyleWord(true);
		chatBox_.setEditable(false);
		pane_ = new JScrollPane(chatBox_);
		mainMenu.add(pane_, BorderLayout.CENTER);
		mainMenu.add(button, BorderLayout.SOUTH);
		mainMenu.add(textBox_, BorderLayout.NORTH);
		mainMenu.setVisible(true);
		mainFrame_.setContentPane(mainMenu);
		mainFrame_.setVisible(true);
		client_ = new ChatClient(hostID, port);
		IncomingMessages incoming = new IncomingMessages(client_);
		incoming.start();

	}

	/**
	 * Sends message to server when mouse click is detected
	 * 
	 * @author Terrance Rose Jr.
	 *
	 */
	private class SendClickMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			client_.sendMessage(textBox_.getText());
			textBox_.setText("");

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Sends message to server when "enter" key has been pressed
	 * 
	 * @author Terrance Rose Jr.
	 *
	 */
	private class KeyPressedActionListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if (e.getKeyCode() == e.VK_ENTER) {
				TChat.client_.sendMessage(textBox_.getText());
				textBox_.setText("");

			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Listener for sending message to server when client closes window
	 * 
	 * @author Terrance Rose Jr.
	 *
	 */
	public static class WindowClosed implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Window closed");
			/*
			 * will send message to server with name of client letting it know client closed
			 * the chat window.
			 */
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Thread class that receives messages from server and displays it in client's
	 * chat window
	 * 
	 * @author Terrance Rose Jr.
	 *
	 */
	public static class IncomingMessages extends Thread implements Runnable {

		public IncomingMessages(ChatClient client) {
			try {
				ChatClient.in_ = new BufferedReader(new InputStreamReader(client.connection_.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {

				while (true) {
					String message;
					message = ChatClient.in_.readLine();
					if (message == null) {
						System.out.println("Something went wrong with the server. You should try reconnecting.");
						break;
					}

					java.util.Date date = new java.util.Date();
					SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");

					chatBox_.append("[" + sdf.format(date) + "] " + message + newline);
					chatBox_.setCaretPosition(chatBox_.getDocument().getLength());

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static boolean hostAvailabilityCheck(String hostID, int port) {
		try (Socket s = new Socket(hostID, port)) {
			return true;
		} catch (IOException ex) {
			/* ignore */
		}
		String[] options = { "yes", "no" };
		int answer = JOptionPane.showOptionDialog(null,
				"Invalid server info entered or host is not available. Do you want to try again?", "Error",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		if (answer == 0) {
			return false;
		}
		System.exit(0);
		return false;
	}

	public static void main(String[] args) {
		boolean valid = false;
		String hostID = "";
		int port = 0;
		while (valid == false) {
			hostID = JOptionPane.showInputDialog("Enter host address.");
			port = Integer.parseInt(JOptionPane.showInputDialog("Enter port number."));
			valid = hostAvailabilityCheck(hostID, port);
		}

		TChat chatWindow = new TChat(hostID, port);
	}
}