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
			try
			{
				if(lastInput.trim().substring(0, 4).contains("exit"))
					continue;
				else if(lastInput.trim().substring(0, 4).contains("kick"))
					this.kick();
				else if(lastInput.trim().substring(0, 4).contains("send"))
				{
					System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + lastInput.substring(lastInput.indexOf(" ") + 1));
					this.server.broadcastMessage("admin", lastInput.substring(lastInput.indexOf(" ") + 1));
				}
				else
				{
					try {
						System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "Command " + lastInput.split(" ")[0] + " not recognized.");					
					}catch(ArrayIndexOutOfBoundsException e)
					{
						System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "Command " + lastInput + " not recognized.");
						return;
					}
				}
			}catch(StringIndexOutOfBoundsException e) {}
		}while(!lastInput.equals("exit"));
		System.exit(0);
	}

	public void kick()
	{
		String host;
		try {			
			host = lastInput.split(" ")[1];
		}catch(ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e)
		{
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "No host selected.");
			return;
		}
		//TODO: kick all users via IP
		//TODO-CLIENT: add error messages and kicks
		int i = 0;
		try {
			if(server.getClientList().size() == 0)
			{
				System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "Host " + host + " not found");
				return;				
			}

			while(i < server.getClientList().size() && (!server.getClientList().get(i).getUsername().equals(host) && !server.getClientList().get(i).getIP().equals(host)))
				i++;	
		}catch(IndexOutOfBoundsException e)
		{
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "Host " + host + " not found.");
			return;
		}
		System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "kicked " + host);


		try {
			server.getClientList().get(i).sendToThis("admin", "0xc0001");
		} catch (IOException e) {
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "Tried to kick " + host + " but failed.");
		}
		server.getClientList().get(i).disconnect();
		String temp = server.getClientList().get(i).getUsername();
		server.getClientList().remove(i);
		server.broadcastMessage("admin", "L'utente " + temp + " è stato kickato dall'amministratore.");
	}
}
