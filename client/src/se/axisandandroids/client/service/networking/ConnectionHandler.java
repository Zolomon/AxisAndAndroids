package se.axisandandroids.client.service.networking;

import java.util.HashMap;

import se.axisandandroids.networking.Connection;

public class ConnectionHandler {

	private static int id = 0;
	private HashMap<Integer, ConnectionMock> connections = new HashMap<Integer, ConnectionMock>();
	
	public ConnectionHandler() {
		
	}
	
	public void add(ConnectionMock connection) {
		connection.setId(id);
		connections.put(id++, connection);
	}
	
	public void remove(int id) {
		connections.remove(id);
	}
	
	@Override
	public String toString() {
		String result = "";
		
		for(ConnectionMock c : connections.values()) {
			result += c;
		}
		
		return result;
	}
	
	public void test() {
		add(new ConnectionMock());
		
	}
	
	
}
