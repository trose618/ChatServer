import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * This class represents a client connection to the chat server
 * 
 * @author Terrance Rose Jr.
 *
 */
public class ChatClient {

	private static PrintWriter out_;
	public static BufferedReader in_;
	public static Socket connection_;
	private static String name_ = "";
	private static Scanner input = new Scanner(System.in);

	public ChatClient(String hostID, int port) {
		try {
			InetAddress ip = InetAddress.getByName(hostID);
			connection_ = new Socket(ip, port);
			name_ = JOptionPane.showInputDialog("What is your name?");
			in_ = new BufferedReader(new InputStreamReader(connection_.getInputStream()));
			out_ = new PrintWriter(connection_.getOutputStream());
			System.out.println("Connected. You can now type in the chat.");
			System.out.println("");

			System.out.println("started new chatclient incoming thread.");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ConnectException e) {
			System.out.println("connection refused \n");
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/**
	 * Sends message to connected server
	 * 
	 * @param message
	 *            message to be sent
	 */
	public void sendMessage(String message) {
		if (out_ != null) {
			out_.println(name_ + ": " + message);
			out_.flush();
		}
	}

	/**
	 * Thread that receives messages from the server
	 * 
	 * @author Terrance Rose Jr.
	 *
	 */
	public static class IncomingMessages extends Thread implements Runnable {

		public IncomingMessages(Socket connection) {
			try {
				in_ = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
					message = in_.readLine();
					if (message == null) {
						System.out.println("Something went wrong with the server. You should try reconnecting.");
						break;
					}
					System.out.println(message);

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Sets user's name
	 * 
	 * @param name
	 *            name to set as user's
	 */
	public void setName(String name) {
		name_ = name;
	}

	/**
	 * Returns name of client
	 * 
	 * @return user's name
	 */
	public String getName() {
		return name_;
	}

	/**
	 * Returns Client's BufferedReader
	 * 
	 * @return client's BufferedReader
	 */
	public BufferedReader getReader() {
		return in_;
	}

	public static void main(String[] args) {
		System.out.println("enter host address.");
		String hostID = input.nextLine();
		System.out.println("enter port number.");
		int port = Integer.parseInt(input.nextLine());

		System.out.println("What is your name?");
		name_ = input.nextLine();

		System.out.println();
		ChatClient client = new ChatClient(hostID, port);

	}

}
