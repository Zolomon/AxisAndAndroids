package se.axisandandroids.client.controller;

import android.os.Message;

public interface ControllerState {
	boolean handleMessage(Message msg);
}
