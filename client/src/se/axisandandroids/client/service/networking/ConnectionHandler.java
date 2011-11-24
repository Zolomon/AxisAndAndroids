package se.axisandandroids.client.service.networking;

import java.io.IOException;
import java.util.ArrayList;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.Panel;
import se.axisandandroids.networking.Connection;


public class ConnectionHandler {

	private ArrayList<CameraTunnel> tunnels;
	private DisplayMonitor disp_monitor;
	
	public ConnectionHandler(DisplayMonitor disp_monitor) {
		this.disp_monitor = disp_monitor;
		tunnels = new ArrayList<CameraTunnel>();
	}
	
	public void add(CameraTunnel tunnel) {		
		int id = -1; // tunnels.size(); // NOT IMPLEMENTED !!!
		CameraTunnel ct = tunnel;
		tunnels.add(ct);	
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
