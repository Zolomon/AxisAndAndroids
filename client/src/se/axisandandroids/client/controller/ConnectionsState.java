package se.axisandandroids.client.controller;

import android.os.Message;
import static se.axisandandroids.client.controller.ControllerProtocol.*;

public class ConnectionsState implements ControllerState {
	private final Controller controller;

	public ConnectionsState(Controller controller) {
		this.controller = controller;
	}

	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case V_REQUEST_QUIT:
			onRequestQuit();
			return true;
		case V_REQUEST_DISPLAYS:
			onRequestDisplays();
			controller.changeState(new DisplaysState(controller));
			return true;
		}
		return false;
	}

	private void onRequestDisplays() {

		// send the data to the outbox handlers (view)
		controller.notifyOutboxHandlers(C_SHOW_DISPLAYS, 0, 0, controller
				.getModel().getData());
	}

	private void onRequestQuit() {
		controller.quit();
	}
}
