package se.axisandandroids.client.controller;

public interface ControllerProtocol {
	int V_REQUEST_QUIT = 101; // empty
	int V_REQUEST_CONNECTIONS = 102; // empty
	int V_REQUEST_DISPLAYS = 103; // empty
	
	int C_QUIT = 201; // empty
	int C_SHOW_CONNECTIONS = 202; // empty
	int C_SHOW_DISPLAYS = 203; // empty
	//int C_DATA = 204; // obj = (ModelData) data
}
