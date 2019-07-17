package core;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import management.Group;

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
			this.evaluateCommand(lastInput);
		}while(!lastInput.equals("exit"));
		System.exit(0);
	}
	
	public synchronized void evaluateCommand(String input)
	{
		this.lastInput = input;
		try
		{	
			if(lastInput.trim().substring(0, 4).contains("exit"))
				return;
			else if(lastInput.trim().substring(0, 4).contains("help"))
			{
				System.out.println(
							"Commands: \n"
							+ "  kick [host] \n"
							+ "  send [host] [message] \n"
							+ "  op [host] \n"
							+ "  exit"
						);
			}
			else if(lastInput.trim().substring(0, 4).contains("kick"))
				this.kick();
			else if(lastInput.trim().substring(0, 4).contains("send"))
			{
				System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + lastInput.substring(lastInput.indexOf(" ") + 1));
				this.server.broadcastMessage("admin", lastInput.substring(lastInput.indexOf(" ") + 1));
			}
			else if(lastInput.trim().substring(0, 2).contains("op"))
			{
				this.op();
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
	}
	
	public void op()
	{
		String host;
		try {			
			host = lastInput.split(" ")[1];
		}catch(ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e)
		{
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "No host selected.");
			return;
		}
		
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
		server.getClientList().get(i).setGroup(Group.ADMIN);
		System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost: " + "Opped " + host);
		try {
			server.getClientList().get(i).sendToThis("admin", "Ti sono stati concessi i privilegi da amministratore");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		server.getClientList().get(i).kick();
		server.getClientList().remove(i);
		server.broadcastMessage("admin", "L'utente " + temp + " è stato kickato dall'amministratore.");
	}
}
