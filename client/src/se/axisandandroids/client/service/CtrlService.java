package se.axisandandroids.client.service;

import java.util.ArrayList;
import java.util.List;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.Panel;
import se.axisandandroids.client.service.networking.ConnectionHandler;
import se.axisandandroids.networking.Connection;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CtrlService extends android.app.Service {
	private static final String TAG = CtrlService.class.getSimpleName();
	private final IBinder mBinder = new LocalBinder();

	public DisplayMonitor mDisplayMonitor = new DisplayMonitor();
	public ConnectionHandler mConnectionHandler = new ConnectionHandler(mDisplayMonitor);
	private List<Connection> mConnections;
	private List<Panel> mPanels;
	
	public class LocalBinder extends Binder {
		public CtrlService getService() {
			return CtrlService.this;
		}
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
		
		mConnections = new ArrayList<Connection>();
		mPanels = new ArrayList<Panel>();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "CtrlService Destroyed", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	public void disconnect() {
		mConnectionHandler.disconnect();
	}

	public void playPanels() {
		mConnectionHandler.playPanels();
	}

	public void pausePanels() {
		mConnectionHandler.pausePanels();
	}
	
	/* public methods for client */
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public void add(Connection connection) {
//		mConnections.add(connection);
//		System.out.println("Connection added:" + connection);
//	}
//	
//	public void add(Panel panel) {
//		mPanels.add(panel);
//		System.out.println("Panel added");
//	}
//	
//	public void remove(Connection connection) {
//		mConnections.remove(connection);
//	}
//	
//	public void remove(Panel panel) {
//		mPanels.remove(panel);
//	}
//	
//	public int connections() {
//		return mConnections.size();
//	}
//	
//	public int panels() {
//		return mPanels.size();
//	}	
//	
//	public void createTunnels() {
//		for(int id = 0; id < mConnections.size(); id++) {
//			mConnectionHandler.add(id, new CameraTunnel(mConnections.get(id), mPanels.get(id), dm, id));
//			System.out.println("Tunnel added:" + mConnections.get(id));
//		}
//	}
//	
//	public void createTunnel(Connection connection, Panel panel, int id) {
//		mConnectionHandler.add(id, new CameraTunnel(connection, panel, dm, id));
//		System.out.println("Tunnel added:" + mConnections.get(id));
//	}
//
//	public void createTunnel(Panel panel, int id) {
//		Connection c = mConnections.get(id);
//		createTunnel(c, panel, id);
//	}
}
