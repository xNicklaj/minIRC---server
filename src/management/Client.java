package management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import org.ini4j.Wini;

import core.Server;

public class Client extends Thread{
	private static Object mutex;
	private boolean isActive = true;
	
	private Socket socket;
	private String username;
	private String IP;
	private Group group;
	
	private BufferedReader reader;
	private Server server;
	
	public Client(String username, String IP, Group group)
	{
		this.username = username;
		this.IP = IP;
		this.group = group;
	}
	
	public Client(Socket socket, Server server)
	{
		this.socket = socket;
		this.server = server;
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.IP = socket.getInetAddress().toString().substring(1);
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
	
	public InputStream getInputStream() throws IOException
	{
		return this.socket.getInputStream();
	}
	
	public OutputStream getOutputStream() throws IOException
	{
		return this.socket.getOutputStream();
	}
	
	public synchronized void sendToThis(String username, String message) throws IOException
	{
		new PrintWriter(socket.getOutputStream(), true).println(username);
		new PrintWriter(socket.getOutputStream(), true).println(message);
	}
	
	public void register()
	{
		try {
			this.username = reader.readLine();			
			reader.readLine();
			new PrintWriter(socket.getOutputStream(), true).println();
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
		String message;
		try {
			switch(reader.readLine())
			{
			case "register":
				this.register();
				break;
			case "login":
				this.login();
				break;
			}
			
			while(isActive)
			{
				message = reader.readLine();
				if(message == null)
					return;
				System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + this.username + "@" + this.IP + ": " + message);
				server.broadcastMessage(this.username, message);
			}
		} catch (IOException e) {
			this.disconnect();
			this.server.removeFromList(username);
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + this.username + "@" + this.IP + ": User disconnected");
		}	
	}
	
	public synchronized Object getMutex()
	{
		return Client.mutex;
	}
	
}
