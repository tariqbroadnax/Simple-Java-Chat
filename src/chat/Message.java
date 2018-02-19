package chat;

public class Message 
{
	private String username;
	
	private String txt;
	
	public Message(String username, String txt)
	{
		this.username = username;
		this.txt = txt;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getText() {
		return txt;
	}
	
	public String toString() {
		return username + ": " + txt;
	}
}
