package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
	private boolean running = false;
	
	private Thread connectThread, commandThread;
	
	private ExecutorService sessionService;
	
	private ServerSocket socket;

	private List<ClientData> clientData;
	
	public Server()
	{
		clientData = new ArrayList<ClientData>();
		
		connectThread = new Thread(() -> recieveClients());
		commandThread = new Thread(() -> communicateClients());
		
		sessionService = Executors.newCachedThreadPool();
	}
	
	public void start()
	{
		if(!running)
		{
			running = true;
			
			try 
			{
				socket = new ServerSocket(222);
				
				connectThread.start();
				commandThread.start();
				
				System.out.println("server started");
			}
			catch (IOException e) 
			{
				System.out.println("could not start server: " + e.getMessage());
			}		
		}
	}
	
	public void stop() 
	{
		if(running)
		{
			try {
				socket.close();
			} catch (IOException e) {}
			
			running = false;
			
			System.out.println("server stopped");
		}
	}

	private void recieveClients()
	{
		while(running)
		{
			try 
			{
				Socket clientSocket = socket.accept();
				
				sessionService.execute(() -> initSession(clientSocket));
			} 
			catch (IOException e) {
				stop();
			}
		}
	}
	
	private void initSession(Socket clientSocket) 
	{
		try 
		{
			ClientData data = new ClientData(clientSocket);
		
			while(data.getUsername() == null)
			{
				String command = data.getInput().readLine();
								
				String[] parts = command.split(" ");				

				if(parts.length > 1 && parts[0].equals("username"))
				{
					String username = parts[1];
					
					boolean contains = false;
					
					for(ClientData cd : clientData)
					{
						if(cd.getUsername().equals(username))
						{
							contains = true;
							
							break;
						}
					}
					
					if(contains)
						data.getOutput().println("failure");
					else
					{
						data.getOutput().println("success");
						
						data.setUsername(username);
						
						clientData.add(data);
					}
									
					data.getOutput().flush();
				}
			}
		} 
		catch (IOException e) {}				
	}

	private void communicateClients()
	{
		while(running)
		{
			for(ClientData cd : new ArrayList<ClientData>(clientData))
			{
				try 
				{
					cd.getSocket().getOutputStream().write(-1);
					cd.getOutput().println("poll");
					
					if(cd.getInput().ready())
					{
						String txt = cd.getInput().readLine();
										
						recieveCommand(cd, txt);
					}
				} 
				catch (IOException e) 
				{
					clientData.remove(cd);
					System.out.println("removing client");
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
	
	private void recieveCommand(ClientData data, String txt) throws IOException
	{
		String[] parts = txt.split(" ");
		
		if(parts.length == 0)
			return;
		
		String command = parts[0];
		
		switch(command)
		{
		case "message":
			if(parts.length > 1)
			{
				String newCommand = "message " + data.getUsername() + " " + txt.substring(7); 
								
				for(ClientData cd : new ArrayList<ClientData>(clientData))
				{
					if(cd != data)
					{
						cd.getOutput().println(newCommand);
						cd.getOutput().flush();
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		Server server = new Server();
		
		server.start();
		
		Scanner input = new Scanner(System.in);
		
		input.nextLine();
		
		server.stop();
		
		input.close();
	}
}
