import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;
/**
 * 
 * @author Terrance Rose Jr.
 *
 *This is the ChatServer class which is used to host the chat server.
 */
public class ChatServer {

	public static void main(String args[]) throws InterruptedException {

		try {
			socket_ = new ServerSocket(0, 1, InetAddress.getLocalHost());
			System.out.println(
					"\r\nRunning Server: " + "Host=" + getSocketAddress().getHostAddress() + " Port=" + getPort());
			System.out.println("ChatServer Running...");
			
			DisplayServerInfo info = new DisplayServerInfo();
			info.start();

			while (true) {

				Socket connection_ = socket_.accept();
				sendToAll("New person joined.");
				Thread.sleep(2000);
				IncomingMessages messages = new IncomingMessages(connection_);
				connections_.add(connection_);
				try {
					messages.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				messages.start();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// end of main

	public static ServerSocket socket_;//for hosting. allows other sockets to connect to this one.
	public static Socket connection_;//holds reader/writer for receiving/sending messages
	public static ArrayList<Socket> connections_ = new ArrayList<Socket>();//holds list of sockets/clients connected to the server

	/**
	 * Gets the IP Address the server is being hosted on
	 * @return the IP Address of the server
	 */
	public static InetAddress getSocketAddress() {
		return socket_.getInetAddress();
	}

	/**
	 * gets the port number of the server
	 * @return the port number of the server
	 */
	public static int getPort() {
		return socket_.getLocalPort();
	}

	/**
	 * Sends a message to all clients connected to the server
	 * @param message - message to be sent
	 */
	public static void sendToAll(String message) {

		for (Socket curr : connections_) {
			try {
				PrintWriter out = new PrintWriter(curr.getOutputStream());
				out.println(message);
				out.flush();
				System.out.println("sent message");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @author Terrance Rose Jr.
	 *
	 * Thread class which displays the info for connecting to the server
	 */
	public static class DisplayServerInfo extends Thread implements Runnable {

		@Override
		public void run() {
			JOptionPane.showMessageDialog(null,
					"\r\nRunning Server: " + "Host=" + getSocketAddress().getHostAddress() + " Port=" + getPort(),
					"Server Info", JOptionPane.INFORMATION_MESSAGE, null);
		}
	}
	
	/**
	 * 
	 * @author Terrance Rose Jr.
	 * 
	 * Thread class for receiving messages from clients
	 *
	 */
	public static class IncomingMessages extends Thread implements Runnable {
		BufferedReader in_;

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
						System.out.println("someone disconnected.");
						sendToAll("Someone disconnected.");
						break;
					}
					System.out.println(message);
					sendToAll(message);

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
