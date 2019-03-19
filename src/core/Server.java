package core;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
	private Wini ini;
	
	public Server()
	{
		this.clientList = new ArrayList<Client>();
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
	
	public static void main(String args[])
	{
		Server server = new Server();
		server.start();
	}
	
	public void start()
	{
		try {
			this.iniLoad();
			this.clientList.add(new Client(server.accept()));
			this.clientList.get(this.clientList.size() - 1).setName(this.clientList.get(this.clientList.size() - 1).getIP());
			this.streamServerAttributes(this.clientList.get(this.clientList.size() - 1));
			this.clientList.get(this.clientList.size() - 1).start();
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + this.clientList.get(this.clientList.size() - 1).getUsernameAtAddress() + ": Client connected" );
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
}
