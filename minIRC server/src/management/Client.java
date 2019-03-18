package management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread{
	private static Object mutex;
	private Socket socket;
	private String username;
	private String IP;
	private BufferedReader reader;
	private Group group;
	private boolean isActive;
	
	public Client(String username, String IP, Group group)
	{
		this.username = username;
		this.IP = IP;
		this.group = group;
	}
	
	public Client(Socket socket)
	{
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.IP = socket.getInetAddress().toString();
			this.username = reader.readLine();
		} catch (IOException e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
	}

	public void toggleActivity()
	{
		this.isActive = !this.isActive;
	}
	
	public String getUsernameAtAddress()
	{
		return this.username + "@" + this.IP;
	}
	
	public void disconnect()
	{
		this.isActive = false;
		this.socket = null;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getUsername() {
		return username;
	}

	public String getIP() {
		return IP;
	}
	
	public void run()
	{
		try {
			while(isActive)
			{
				reader.readLine(); 
				
			}
		} catch (IOException e) {
			this.disconnect();
		}	
	}
	
	public synchronized Object getMutex()
	{
		return Client.mutex;
	}
	
}
