package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientData 
{
	private Socket socket;
	
	private BufferedReader input;
	
	private PrintWriter output;
	
	private String username;
	
	public ClientData(Socket socket) throws IOException
	{
		this.socket = socket;
		
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		output = new PrintWriter(socket.getOutputStream());
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public BufferedReader getInput() {
		return input;
	}
	
	public PrintWriter getOutput() {
		return output;
	}
	
	
}
