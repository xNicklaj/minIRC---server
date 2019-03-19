package core;

import java.io.File;
import java.io.IOException;
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
	private Wini ini;
	
	public Server()
	{
		this.clientList = new ArrayList<Client>();
	}
	
	public void createIni() throws IOException
	{
		try {
			if(Files.notExists(Paths.get(PathFinder.getProjectPath() + "settings.ini")))
				Files.createFile(Paths.get(PathFinder.getProjectPath() + "settings.ini"));
		}catch(InvalidPathException e)
		{
			if(Files.notExists(Paths.get(PathFinder.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6) + "local.ini")))
				Files.createFile(Paths.get(PathFinder.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6) + "local.ini"));
		}
		ini.put("network", "port", "2332");
		ini.store();
	}
	
	public void iniLoad() throws IOException
	{
		try {
			createIni();
			ini = new Wini(new File(PathFinder.getProjectPath().toString() + "settings.ini"));
			ini.load();
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
			this.clientList.add(new Client(server.accept(), this.ini));
			this.clientList.get(this.clientList.size() - 1).setName(this.clientList.get(this.clientList.size() - 1).getIP());
			this.clientList.get(this.clientList.size() - 1).start();
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + this.clientList.get(this.clientList.size() - 1).getUsernameAtAddress() + ": Client connected" );
		} catch (IOException e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
		
	}
}
