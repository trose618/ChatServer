import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

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
				// System.out.println("about to start reading thread.");
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

	public static ServerSocket socket_;
	public static Socket connection_;
	public static ArrayList<Socket> connections_ = new ArrayList<Socket>();

	public static InetAddress getSocketAddress() {
		return socket_.getInetAddress();
	}

	public static int getPort() {
		return socket_.getLocalPort();
	}

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

	public static class DisplayServerInfo extends Thread implements Runnable {

		@Override
		public void run() {
			JOptionPane.showMessageDialog(null,
					"\r\nRunning Server: " + "Host=" + getSocketAddress().getHostAddress() + " Port=" + getPort(),
					"Server Info", JOptionPane.INFORMATION_MESSAGE, null);
		}
	}

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
					// Thread.sleep(1000);
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
