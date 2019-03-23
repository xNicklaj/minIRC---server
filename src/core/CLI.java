package core;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class CLI extends Thread{
	private Scanner input;
	private String lastInput;
	private Server server;
	
	public CLI(Server server)
	{
		this.setName("CLI");
		this.server = server;
		input = new Scanner(System.in);
	}
	
	public void run()
	{
		do
		{
			lastInput = input.nextLine();
			if(lastInput.trim().substring(0, 4).contains("exit"))
				continue;
			else if(lastInput.trim().substring(0, 4).contains("kick"))
				this.kick();
		}while(!lastInput.equals("exit"));
		System.exit(0);
	}
	
	public void kick()
	{
		String host = lastInput.split(" ")[1];
		//TODO: kick all users via IP
		//TODO-CLIENT: add error messages and kicks
		int i = 0;
		try {
			while(i < server.getClientList().size() && (!server.getClientList().get(i).getUsername().equals(host) && !server.getClientList().get(i).getIP().equals(host)))
				i++;			
		}catch(IndexOutOfBoundsException e)
		{
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "host " + host + " not found.");
		}
		
		System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "kicked " + host);
		
		try {
			server.getClientList().get(i).sendToThis("admin", "0xc0001");
		} catch (IOException e) {
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "Tried to kick " + host + " but failed.");
		}
		server.getClientList().get(i).disconnect();
		server.getClientList().remove(i);
		server.broadcastMessage("admin", "L'utente " + server.getClientList().get(i).getUsername() + " è stato kickato dall'amministratore.");
	}
}
