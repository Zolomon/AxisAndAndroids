package se.axisandandroids.client.service;

import java.net.Socket;
import java.util.List;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CtrlService extends android.app.Service {
	private static final String TAG = CtrlService.class.getSimpleName();
	private final IBinder mBinder = new LocalBinder();
	private int counter = 0;
	private List<Socket> sockets;
	
	public class LocalBinder extends Binder {
		public CtrlService getService() {
			return CtrlService.this;
		}
	}
	
	public void addSocket(String hostname, int port) {
		
	}
	
	public void removeSocket(Socket socket) {
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "CtrlService Bound", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onBind");
		return mBinder;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "CtrlService Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "CtrlService Destroyed", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}
	
	/* public methods for client */
	public int increase() {
		return counter++;
	}
}
