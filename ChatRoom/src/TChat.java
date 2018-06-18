import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

public class TChat extends JPanel {

	private JFrame mainFrame_ = new JFrame();
	private JPanel mainMenu;
	private static ChatClient client_;
	JTextField textBox_;
	static JTextArea chatBox_;
	private final static String newline = "\n";
	private static JScrollPane pane_;

	public TChat(String hostID, int port) {
		super();
		mainFrame_.setLayout(new BorderLayout());
		mainFrame_.setSize(500, 600);
		mainFrame_.setLocationRelativeTo(null);
		mainFrame_.setTitle("TChat");
		mainFrame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
					
					chatBox_.append("["+ sdf.format(date)+ "] "+ message + newline);
					chatBox_.setCaretPosition(chatBox_.getDocument().getLength());

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {

		String hostID = JOptionPane.showInputDialog("Enter host address.");

		int port = Integer.parseInt(JOptionPane.showInputDialog("Enter port number."));

		TChat chatWindow = new TChat(hostID, port);
	}
}