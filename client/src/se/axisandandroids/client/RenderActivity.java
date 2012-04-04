package se.axisandandroids.client;

import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.client.display.NewDisplayModeCallback;
import se.axisandandroids.client.display.NewSyncModeCallback;
import se.axisandandroids.client.display.Panel;
import se.axisandandroids.client.service.CtrlService;
import se.axisandandroids.client.service.CtrlService.LocalBinder;
import se.axisandandroids.client.service.networking.CameraTunnel;
import se.axisandandroids.client.service.networking.UDP_ClientConnection;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
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
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RenderActivity extends Activity {
	private static final String TAG = RenderActivity.class.getSimpleName();
	private CtrlService mService;
	private LinearLayout mLinearLayout;
	private LayoutInflater mLayoutInflater;
	private boolean mPaused;
	private int mSyncMode;
	private int mDisplayMode;

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

	
//	if (dm.getDispMode() != Protocol.DISP_MODE.AUTO) {
//		dm
//		.postToAllMailboxes(new ModeChange(
//				Protocol.COMMAND.DISP_MODE,
//				Protocol.DISP_MODE.AUTO));
//		dm.setDispMode(Protocol.DISP_MODE.AUTO);
//		dispBox.setSelectedIndex(Protocol.DISP_MODE.AUTO);
//	}
	protected void setSyncMode(int mode) {
		MenuItem syncItem = null;
		switch (mSyncMode) {
		case 0:
			syncItem = (MenuItem) findViewById(R.id.menu_sync_mode_auto);
			break;
		case 1:
			syncItem = (MenuItem) findViewById(R.id.menu_sync_mode_sync);
			break;
		case 2:
			syncItem = (MenuItem) findViewById(R.id.menu_sync_mode_async);
			break;
		}
		syncItem.setChecked(false);
		switch (mode) {
		case 0:
			syncItem = (MenuItem) findViewById(R.id.menu_sync_mode_auto);
			break;
		case 1:
			syncItem = (MenuItem) findViewById(R.id.menu_sync_mode_sync);
			break;
		case 2:
			syncItem = (MenuItem) findViewById(R.id.menu_sync_mode_async);
			break;
		}
		syncItem.setChecked(true);
		mSyncMode = mode;
	}

	protected void setDisplayMode(int mode) {
		MenuItem displayItem = null;
		switch (mDisplayMode) {
		case 0:
			displayItem = (MenuItem) findViewById(R.id.menu_display_mode_auto);
			break;
		case 1:
			displayItem = (MenuItem) findViewById(R.id.menu_display_mode_idle);
			break;
		case 2:
			displayItem = (MenuItem) findViewById(R.id.menu_display_mode_movie);
			break;
		}
		displayItem.setChecked(false);
		switch (mode) {
		case 0:
			displayItem = (MenuItem) findViewById(R.id.menu_display_mode_auto);
			break;
		case 1:
			displayItem = (MenuItem) findViewById(R.id.menu_display_mode_idle);
			break;
		case 2:
			displayItem = (MenuItem) findViewById(R.id.menu_display_mode_movie);
			break;
		}
		displayItem.setChecked(true);
		mDisplayMode = mode;

	}

	@Override
	protected void onRestart() {
		mService.playPanels();
		super.onRestart();
	}

	@Override
	protected void onStop() {
		mService.pausePanels();
		try {
			// mService.disconnect();
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

			for (UDP_ClientConnection c : mService.mConnectionHandler
					.connectionIterator()) {
				System.out.println("Connection ID: " + c.getId());
				addPanel(c);
			}

			mService.mConnectionHandler.clearConnections();

			mService.mDisplayMonitor
					.setNewDisplayModeCallback(new NewDisplayModeCallback() {

						public void callback(int mode) {
							setDisplayMode(mode);
						}
					});

			mService.mDisplayMonitor
					.setNewSyncModeCallback(new NewSyncModeCallback() {

						public void callback(int mode) {
							setSyncMode(mode);
						}
					});
		}

		public void onServiceDisconnected(ComponentName compName) {
		}
	};

	private void addPanel(UDP_ClientConnection c) {
		final FrameLayout theInflatedPanel = (FrameLayout) mLayoutInflater
				.inflate(R.layout.panel, null);
		Panel panel = (Panel) theInflatedPanel.findViewById(R.id.panel);

		mService.mConnectionHandler.add(c.getId(), new CameraTunnel(c, panel,
				mService.mDisplayMonitor, c.getId()));
		mLinearLayout.addView(theInflatedPanel, new LayoutParams(
				LayoutParams.WRAP_CONTENT, 320));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.render_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.menu_display_mode_auto).setChecked(true);
		menu.findItem(R.id.menu_sync_mode_auto).setChecked(true);
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
		case R.id.menu_connections:
			// finish();
			setVisible(false);
			return true;
		case R.id.menu_quit:
			finish();
			return true;
		case R.id.menu_display_mode_auto:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mDisplayMode = 0;
			System.out.println("DisplayMode: " + mDisplayMode + " SyncMode: "
					+ mSyncMode);
			return true;
		case R.id.menu_display_mode_idle:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mDisplayMode = 1;
			System.out.println("DisplayMode: " + mDisplayMode + " SyncMode: "
					+ mSyncMode);
			return true;
		case R.id.menu_display_mode_movie:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mDisplayMode = 2;
			System.out.println("DisplayMode: " + mDisplayMode + " SyncMode: "
					+ mSyncMode);
			return true;
		case R.id.menu_sync_mode_auto:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mSyncMode = 0;
			System.out.println("DisplayMode: " + mDisplayMode + " SyncMode: "
					+ mSyncMode);
			return true;
		case R.id.menu_sync_mode_sync:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mSyncMode = 1;
			System.out.println("DisplayMode: " + mDisplayMode + " SyncMode: "
					+ mSyncMode);
			return true;
		case R.id.menu_sync_mode_async:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			mSyncMode = 2;
			System.out.println("DisplayMode: " + mDisplayMode + " SyncMode: "
					+ mSyncMode);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
