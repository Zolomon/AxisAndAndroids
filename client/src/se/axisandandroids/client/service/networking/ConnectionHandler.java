package se.axisandandroids.client.service.networking;

import java.io.IOException;
import java.util.HashMap;

import se.axisandandroids.client.display.DisplayMonitor;


public class ConnectionHandler {

	private HashMap<Integer, CameraTunnel> tunnels;
	private DisplayMonitor disp_monitor;
	
	public ConnectionHandler(DisplayMonitor disp_monitor) {
		this.disp_monitor = disp_monitor;
		tunnels = new HashMap<Integer, CameraTunnel>();
	}
	
	public void add(int id, CameraTunnel tunnel) {		
		CameraTunnel ct = tunnel;
		tunnels.put(id, ct);	
	}
	
	public void remove(int id) {	// MAYBE I WAS WRONG AGAIN !
		disconnect(id);				// HARD TO TRACK ID=INDEX CHANGES IN AN ARRAY LIST		
		tunnels.remove(id);			// WHEN USER ADD AND REMOVE CONNECTION TUNNELS
									// AT THE SPEED OF LIGHT. /JAKOB
	}								// By the way I removed a ; just to get your attention.
	
	public void disconnect(int id) {
		CameraTunnel c = tunnels.get(id);
		try {
			c.connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
