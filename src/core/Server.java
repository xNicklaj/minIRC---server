package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import management.Client;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
	private List<Client> clientList;
	private ServerSocket server;
	private JSONObject obj;
	
	@SuppressWarnings("unchecked")
	public Server()
	{
		try
		{
			JSONParser parser = new JSONParser();
			Object temp = parser.parse(new FileReader("settings.json"));
			this.obj = (JSONObject) temp;
		}catch(FileNotFoundException e)
		{	
			try {
				File file = new File("settings.json");
				file.createNewFile();
				file = null;
			} catch (IOException e1) {
				System.err.println("Could not create the file settings.json");
			}
			
			JSONParser parser = new JSONParser();
			Object temp = null;
			try {
				temp = parser.parse(new FileReader("settings.json"));
			} catch (FileNotFoundException e2) {
				System.err.println("An error occured while trying to read the settings. Exiting in 10 seconds.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					System.out.println(e1.getClass().getName());
					e1.printStackTrace();
				}
				System.exit(-1);
			} catch (IOException e2) {
				System.err.println("An error occured while trying to read the settings. Exiting in 10 seconds.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					System.out.println(e1.getClass().getName());
					e1.printStackTrace();
				}
				System.exit(-1);
			} catch (ParseException e2) {
				System.err.println("An error occured while trying to read the settings. Exiting in 10 seconds.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					System.out.println(e1.getClass().getName());
					e1.printStackTrace();
				}
				System.exit(-1);
			}
			this.obj = (JSONObject) temp;
			
			try(FileWriter file = new FileWriter("settings.json"))
			{
				obj.put("serverport", "2332");
				file.write(obj.toJSONString());
				file.flush();
			}catch(IOException e1)
			{
				System.err.println("An error occured while trying to read the settings. Exiting in 10 seconds.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e2) {
					System.out.println(e1.getClass().getName());
					e1.printStackTrace();
				}
				System.exit(-1);
			}
			
		} catch (IOException e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		} catch (ParseException e) {
			obj = new JSONObject();
			try(FileWriter file = new FileWriter("settings.json"))
			{
				obj.put("serverport", "2332");
				file.write(obj.toJSONString());
				file.flush();
			} catch (IOException e1) {
				System.out.println(e1.getClass().getName());
				e1.printStackTrace();
			}
		}
			
		try {
			this.server = new ServerSocket(Integer.parseInt((String) obj.get("serverport")));
		} catch (IOException e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
		
		this.clientList = new ArrayList<Client>();
	}
	
	public static void main(String args[])
	{
		Server server = new Server();
		server.start();
	}
	
	public void start()
	{
		try {
			this.clientList.add(new Client(server.accept()));
			this.clientList.get(this.clientList.size() - 1).setName(this.clientList.get(this.clientList.size() - 1).getIP());
			this.clientList.get(this.clientList.size() - 1).start();
			System.out.println("[" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + this.clientList.get(this.clientList.size() - 1).getUsernameAtAddress() + ": Client connected" );
		} catch (IOException e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
		
	}
}
