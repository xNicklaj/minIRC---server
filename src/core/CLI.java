package core;

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
			System.out.print("> ");
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
		int i = 0;
		while(i < server.getClientList().size() || server.getClientList().get(i).getUsername().equals(host) || server.getClientList().get(i).getIP().equals(host))
			i++;
		
		if(i == server.getClientList().size())
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "admin@localhost" + " tried to kick the remote host " + host + " but failed.");
		else 
		{
			server.getClientList().get(i).disconnect();
			server.getClientList().remove(i);
		}
	}
}
