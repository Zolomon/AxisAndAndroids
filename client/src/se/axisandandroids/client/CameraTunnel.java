package se.axisandandroids.client;

import java.net.Socket;

import se.axisandandroids.networking.Connection;

public class CameraTunnel {
	public Connection connection;

	public CameraTunnel(Connection c) {
		this.connection  = c;						
	}
	
	private void createThreads() {
		// Create the threads here !!!
	}
}
