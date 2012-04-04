package se.axisandandroids.client.service.networking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import se.axisandandroids.client.display.DisplayMonitor;


public class ConnectionHandler {

	private HashMap<Integer, CameraTunnel> tunnels;
	private ArrayList<UDP_ClientConnection> connections; 
	private DisplayMonitor disp_monitor;
	
	public ConnectionHandler(DisplayMonitor disp_monitor) {
		this.disp_monitor = disp_monitor;
		tunnels = new HashMap<Integer, CameraTunnel>();
		connections = new ArrayList<UDP_ClientConnection>();
	}

	public void add(int id, CameraTunnel tunnel) {
		CameraTunnel ct = tunnel;
		tunnels.put(id, ct);
	}

	public CameraTunnel get(int id) {
		return tunnels.get(id);
	}

	public void remove(int id) {
		disconnect(id);
		tunnels.remove(id);

	}
	
	public void disconnect() {
		for(CameraTunnel t : tunnels.values()) {
			t.disconnect();
		}
	}

	public void disconnect(int id) {
		CameraTunnel c = tunnels.get(id);
		c.disconnect();
	}

	public int tunnels() {
		return tunnels.size();
	}
	
	public void addConnection(UDP_ClientConnection c) {
		connections.add(c);
	}
	
	public void clearConnections() {
		connections.clear();
	}
	
	public List<UDP_ClientConnection> connectionIterator() {
		return Collections.unmodifiableList(connections);
	}

	public void playPanels() {
		for(CameraTunnel c : tunnels.values()) {
			c.playPanel();
		}
	}

	public void pausePanels() {
		for(CameraTunnel c : tunnels.values()) {
			c.pausePanel();
		}
	}
	
	public DisplayMonitor getDispMonitor() {
		return disp_monitor;
	}
}
