package se.axisandandroids.client;

import se.axisandandroids.client.display.Panel;
import se.axisandandroids.client.service.CtrlService;
import se.axisandandroids.client.service.CtrlService.LocalBinder;
import se.axisandandroids.client.service.networking.CameraTunnel;
import se.axisandandroids.networking.Connection;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RenderActivity extends Activity {
	private static final String TAG = RenderActivity.class.getSimpleName();
	private CtrlService mService;
	private LinearLayout mLinearLayout;
	private LayoutInflater mLayoutInflater;
	private boolean mPaused;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_grid);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to CtrlService
		Intent intent = new Intent(this, CtrlService.class);
		boolean res = getApplicationContext().bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);
		Log.d(TAG, "" + res);
	}
	
	@Override
	protected void onRestart() {
		mService.playPanels();
		super.onRestart();
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// for (Connection c : mService.mConnectionHandler.connections) {
	// addPanel(c);
	// }
	//
	// mService.mConnectionHandler.connections.clear();
	// }

	@Override
	protected void onStop() {
		mService.pausePanels();
		try {
			//mService.disconnect();
			unbindService(mConnection);
		} catch (java.lang.IllegalArgumentException e) {
			// Print to log or make toast that it failed
		}
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// Unbind from the service
		try {
			mService.disconnect();
			unbindService(mConnection);
		} catch (java.lang.IllegalArgumentException e) {
			// Print to log or make toast that it failed
		}
		super.onDestroy();
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();

			mLinearLayout = (LinearLayout) findViewById(R.id.gridview);
			mLayoutInflater = LayoutInflater.from(RenderActivity.this);

			for (Connection c : mService.mConnectionHandler
					.connectionIterator()) {
				System.out.println("Connection ID: " + c.getId());
				addPanel(c);
			}

			mService.mConnectionHandler.clearConnections();
		}

		public void onServiceDisconnected(ComponentName compName) {
		}
	};

	private void addPanel(Connection c) {
		final FrameLayout theInflatedPanel = (FrameLayout) mLayoutInflater
				.inflate(R.layout.panel, null);
		Panel panel = (Panel) theInflatedPanel.findViewById(R.id.panel);

		mService.mConnectionHandler.add(c.getId(), new CameraTunnel(c, panel,
				mService.dm, c.getId()));
		mLinearLayout.addView(theInflatedPanel, new LayoutParams(
				LayoutParams.WRAP_CONTENT, 320));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.client_menu, menu);
		return true;
	}

	protected Panel createPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_displays:
			finish();
			return true;
		case R.id.menu_quit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
