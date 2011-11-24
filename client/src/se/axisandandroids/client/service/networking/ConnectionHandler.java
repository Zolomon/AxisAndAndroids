package se.axisandandroids.client.service.networking;

import java.util.HashMap;

import se.axisandandroids.client.display.DisplayMonitor;

public class ConnectionHandler {

	private HashMap<Integer, CameraTunnel> tunnels;

	public ConnectionHandler(DisplayMonitor disp_monitor) {
		tunnels = new HashMap<Integer, CameraTunnel>();
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

	public void disconnect(int id) {
		CameraTunnel c = tunnels.get(id);
		c.disconnect();
	}

	public int tunnels() {
		return tunnels.size();
	}

}
