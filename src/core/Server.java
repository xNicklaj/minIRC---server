package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import management.Client;

import org.ini4j.Wini;

import management.PathFinder;

public class Server {
	private List<Client> clientList;
	private ServerSocket server;
	private CLI cli;
	private boolean run = true;
	private Wini ini;

	public Server()
	{
		this.clientList = new ArrayList<Client>();
		cli = new CLI(this);
	}

	public List<Client> getClientList() {
		return clientList;
	}

	public void createIni() throws IOException
	{
		try {
			if(Files.notExists(Paths.get(PathFinder.getProjectPath() + "/settings.ini")))
			{
				Files.createFile(Paths.get(PathFinder.getProjectPath() + "/settings.ini"));
				ini = new Wini(new File("settings.ini"));
				ini.put("general", "name", "A minIRC server");
				ini.put("network", "port", "2332");
				ini.store();
			}
		}catch(InvalidPathException e)
		{
			if(Files.notExists(Paths.get(PathFinder.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6) + "/settings.ini")))
			{	
				Files.createFile(Paths.get(PathFinder.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6) + "/settings.ini"));
				ini = new Wini(new File("settings.ini"));
				ini.put("general", "name", "A minIRC server");
				ini.put("network", "port", "2332");
				ini.store();
			}
		}

	}
	
	public CLI getCLI()
	{
		return this.cli;
	}

	public void iniLoad() throws IOException
	{
		try {
			createIni();
			ini = new Wini(new File(PathFinder.getProjectPath().toString() + "/settings.ini"));
			ini.load();
			this.server = new ServerSocket(Integer.parseInt(ini.get("network", "port")));
		}
		catch(NullPointerException e)
		{

		}
	}

	public synchronized void removeFromList(String username)
	{
		for(int i = 0; i < this.clientList.size(); i++)
			if(this.clientList.get(i).getUsername().equals(username))
				this.clientList.remove(i);

	}

	public void toggleRun()
	{
		this.run = !this.run;
	}

	public static void main(String args[])
	{
		Server server = new Server();
		server.start();
	}

	public void start()
	{
		try {
			this.iniLoad();
			cli.start();
			System.out.println(
					"|---------------------------------------------------|\n"
					+ "|                                                   |\n"
					+ "|  minIRC, is the server component for the minIRC   |\n"
					+ "|  project that you can find on the link:           |\n"
					+ "|  https://www.github.com/xNicklaj/minIRC           |\n"
					+ "|                                                   |\n"
					+ "|  You can type in 'help' for a command list        |\n"
					+ "|                                                   |\n"
					+ "|---------------------------------------------------|\n");
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + "Server listening for new connections");
			while(run)
			{
				this.clientList.add(new Client(server.accept(), this));
				this.clientList.get(this.clientList.size() - 1).setName(this.clientList.get(this.clientList.size() - 1).getIP());
				this.streamServerAttributes(this.clientList.get(this.clientList.size() - 1));
				this.clientList.get(this.clientList.size() - 1).start();
			}
		} catch (IOException e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}

	}

	public void streamServerAttributes(Client client) throws IOException
	{
		PrintWriter writer = new PrintWriter(client.getSocket().getOutputStream(), true);
		writer.println(this.ini.get("general", "name"));
	}

	public synchronized void broadcastMessage(String username, String message)
	{
		for(int i = 0; i < this.clientList.size(); i++)
		{
			try {
				this.clientList.get(i).sendToThis(username, message);
			} catch (IOException | IndexOutOfBoundsException e) {
				return;
			}
		}
	}
}
