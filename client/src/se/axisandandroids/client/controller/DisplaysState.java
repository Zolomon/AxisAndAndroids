package se.axisandandroids.client.controller;

import android.os.Message;
import static se.axisandandroids.client.controller.ControllerProtocol.*;

public class DisplaysState implements ControllerState {
	private final Controller controller;
	
	public DisplaysState(Controller controller) {
		this.controller = controller;
	}
	
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
		case V_REQUEST_QUIT:
			onRequestQuit();
			return true;
		case V_REQUEST_CONNECTIONS:
			controller.changeState(new ConnectionsState(controller));
			return true;
		}
		return false;
	}

	private void onRequestQuit() {
		controller.quit();
	}
}
 