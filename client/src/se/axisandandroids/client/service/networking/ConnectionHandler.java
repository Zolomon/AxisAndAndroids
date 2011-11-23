package se.axisandandroids.client.service.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import se.axisandandroids.client.CameraTunnel;
import se.axisandandroids.networking.Connection;


public class ConnectionHandler {

	private static int id = 0;

	//private HashMap<Integer, CameraTunnel> tunnels;	
	private ArrayList<CameraTunnel> tunnels;
	
	
	public ConnectionHandler() {
		 tunnels = new HashMap<Integer, CameraTunnel>();
	}
	
	public void add(Connection connection) {
		tunnels.put(id++, new CameraTunnel(connection));
	}
	
	public void remove(int id) {
		disconnect(id);
		tunnels.remove(id);
	}	
	
	public void disconnect(int id) {
		CameraTunnel c = tunnels.get(id);
		try {
			c.connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
