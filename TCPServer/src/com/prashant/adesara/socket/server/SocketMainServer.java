package com.prashant.adesara.socket.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;


/**
 * @author Prashant Adesara
 * The class extends the Thread class so we can receive and send messages at the
 * same time
 */
public class SocketMainServer extends Thread
{
	private int SERVERPORT = 5657;
	private ServerSocket serverSocket;
	private Socket client = null;
	private boolean running = false;
	private PrintWriter mOut;
	private OnMessageReceived messageListener;
	
	public static void main(String[] args)
	{
		// opens the window where the messages will be received and sent
		ServerBoard frame = new ServerBoard();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null); // *** center the app *** 
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Constructor of the class
	 * @author Prashant Adesara
	 * @param messageListener listens for the messages
	 */
	public SocketMainServer(OnMessageReceived messageListener)
	{
		this.messageListener = messageListener;
	}

	/**
	 * Method to send the messages from server to client
	 * @author Prashant Adesara
	 * @param message the message sent by the server
	 */
	public void sendMessage(String message)
	{
		try
		{
			if (mOut != null && !mOut.checkError())
			{
				System.out.println(message);
				// Here you can connect with database or else you can do what you want with static message
				mOut.println(message);
				mOut.flush();
			}
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * @author Prashant Adesara
	 * */
	@Override
	public void run()
	{
		super.run();
		running = true;
		try
		{
			System.out.println("PA: Connecting...");

			// create a server socket. A server socket waits for requests to
			// come in over the network.
			serverSocket = new ServerSocket(SERVERPORT);

			// create client socket... the method accept() listens for a
			// connection to be made to this socket and accepts it.
			try
			{
				client = serverSocket.accept();
				System.out.println("S: Receiving...");
				// sends the message to the client
				mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
				System.out.println("PA: Sent");
				System.out.println("PA: Connecting Done.");
				// read the message received from client
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				sendMessage("Server connected with Android Client now you can chat with socket server.");
				
				// in this while we wait to receive messages from client (it's an infinite loop)
				// this while it's like a listener for messages
				while (running)
				{
					String message = in.readLine();
					if (message != null && messageListener != null)
					{
						// call the method messageReceived from ServerBoard class
						messageListener.messageReceived(message);
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("PA: Error: "+e.getMessage());
				e.printStackTrace();
			}
			finally
			{
				client.close();
				System.out.println("PA: Done.");
			}
		}
		catch (Exception e)
		{
			System.out.println("PA: Error");
			e.printStackTrace();
		}

	}

	/**
	 * Declare the interface. The method messageReceived(String message) will
	 * @author Prashant Adesara 
	 * must be implemented in the ServerBoard
	 * class at on startServer button click
	*/
	public interface OnMessageReceived
	{
		public void messageReceived(String message);
	}

}
