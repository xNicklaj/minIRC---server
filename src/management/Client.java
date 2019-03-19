package management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import org.ini4j.Wini;

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
			this.setName(this.IP);
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
	
	public void register()
	{
		try {
			this.username = reader.readLine();
			reader.readLine();
			new PrintWriter(socket.getOutputStream(), true).println("1");
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + this.username + "@" + this.IP + ": User registered");
		} catch (IOException e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
	}
	
	public void login()
	{
		try {
			this.username = reader.readLine();
			reader.readLine();
			new PrintWriter(socket.getOutputStream(), true).println("1");
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + this.username + "@" + this.IP + ": User logged in");
		} catch (IOException e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
		
	}
	
	public void run()
	{
		System.out.println("Thread started");
		try {
			switch(reader.readLine())
			{
			case "register":
				System.out.println("register received");
				this.register();
				break;
			case "login":
				this.login();
				break;
			}
			
			while(isActive)
			{
				reader.readLine(); 
				
			}
		} catch (IOException e) {
			this.disconnect();
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + this.username + "@" + this.IP + ": User disconnected");
		}	
	}
	
	public synchronized Object getMutex()
	{
		return Client.mutex;
	}
	
}
