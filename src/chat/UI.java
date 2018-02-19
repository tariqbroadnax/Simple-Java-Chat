package chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UI implements KeyListener, WindowListener
{
	private Client client;
	
	private JFrame frame;
	
	private JTextArea messageFld;
	
	private JTextField inputFld;
	
	public UI(Client client)
	{
		this.client = client;
		
		frame = new JFrame();
		
		messageFld = new JTextArea();
		
		inputFld = new JTextField();
		
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		messageFld.setEditable(false);
		
		Container content = frame.getContentPane();
		
		content.setLayout(new BorderLayout());
		content.add(new JScrollPane(messageFld), BorderLayout.CENTER);
		content.add(inputFld, BorderLayout.SOUTH);
		
		inputFld.addKeyListener(this);
		frame.addWindowListener(this);
	}
	
	public void start() 
	{
		frame.setVisible(true);
	}
	
	public void stop()
	{
		frame.setVisible(false);
		frame.dispose();
	}
	
	public void update()
	{
		String txt = "";
		
		for(Message message : client.getMessages())
			txt += message + "\n";

		messageFld.setText(txt);
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			String txt = inputFld.getText();
			
			if(txt.length() > 0)
			{
				client.sendMessage(txt);
				
				inputFld.setText("");
			}
		}
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("here");
		client.stop();
	}

	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
