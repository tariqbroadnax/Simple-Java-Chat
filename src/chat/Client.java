package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Client 
{	
	private UI ui;
	
	private String username;
	
	private List<Message> messages;
	
	private Socket socket;
	
	private PrintWriter output;
	
	private BufferedReader input;
	
	private Thread thread;
	
	private boolean running;
	
	public Client()
	{
		ui = new UI(this);
		
		messages = new ArrayList<Message>();
		
		thread = new Thread(() -> listenToServer());
	}
	
	public void start()
	{		
		if(!running)
		{
			try 
			{
				connectToServer();
				
				setUsername();
				
				running = true;
				
				thread.start();
				
				ui.start();
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
				
				stop();
			}
		}
	}
	
	public void stop()
	{
		if(running)
		{
			running = false;
	
			if(socket != null)
			{
				try {
					socket.close();
				} catch (IOException e) {}
				
				socket = null;
				input = null;
				output = null;
			}
			
			username = null;
					
			messages.clear();
			
			ui.stop();
		}
	}
	
	private void listenToServer()
	{
		while(running)
		{
			try 
			{
				String txt = input.readLine();
			
				recieveCommand(txt);
			} 
			catch (IOException e)
			{
				if(running)
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
	
					stop();
				}
			}
		}
	}
	
	private void recieveCommand(String txt)
	{
		String[] args = txt.split(" ");
		
		if(args.length == 0)
			return;
			
		String command = args[0];
		
		switch(command)
		{
		case "message":
			if(args.length > 2)
			{
				String username = args[1];
				txt = txt.substring(7 + username.length() + 1);
				
				Message message = new Message(username, txt);
				
				messages.add(message);
				
				ui.update();
			}
			break;
		}
	}
	
	private void connectToServer() throws IOException
	{
		String hostname = InetAddress.getLocalHost().getHostName();
		
		int port = 222;
		
		socket = new Socket(hostname, port);

		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		output = new PrintWriter(socket.getOutputStream());
	}
	
	private void setUsername() throws IOException
	{
		while(username == null)
		{
			String username = JOptionPane.showInputDialog("Enter username: ");
		
			if(username == null || username.equals(""))
			{
				stop();
				return;
			}
						
			output.println("username " + username);
			output.flush();
			
			String reply = input.readLine();
									
			if(reply.equals("success"))
				this.username = username;
			else
				JOptionPane.showMessageDialog(null, "username already taken");
		}
	}
	
	public void sendMessage(String txt) 
	{
		output.println("message " + txt);
		output.flush();

		Message message = new Message(username, txt);
		
		messages.add(message);
		
		ui.update();
	}
	
	public List<Message> getMessages() 
	{
		return messages;
	}
	
	public static void main(String[] args)
	{
		Client c = new Client();
		
		c.start();
	}
} 
