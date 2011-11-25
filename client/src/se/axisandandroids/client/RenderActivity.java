package se.axisandandroids.client;

import se.axisandandroids.client.display.Panel;
import se.axisandandroids.client.service.CtrlService;
import se.axisandandroids.client.service.CtrlService.LocalBinder;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RenderActivity extends Activity {
	private static final String TAG = RenderActivity.class.getSimpleName();
	private CtrlService mService;
	private LinearLayout mLinearLayout;
	private LayoutInflater mLayoutInflater;
	
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
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		try {
			unbindService(mConnection);
		} catch (java.lang.IllegalArgumentException e) {
			// Print to log or make toast that it failed
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();

//			mLinearLayout = (LinearLayout) findViewById(R.id.gridview);
//			mLayoutInflater = LayoutInflater.from(RenderActivity.this);
//
//			for (int i = 0; i < mService.connections(); i++) {
//				addPanel(mLinearLayout, mLayoutInflater);
//			}
//			
//			mService.createTunnels();
		}

		public void onServiceDisconnected(ComponentName compName) {
		}
	};
	
	private void addPanel(final LinearLayout gv, LayoutInflater inflater) {
		final FrameLayout theInflatedPanel = (FrameLayout) inflater.inflate(
				R.layout.panel, null);
		Panel panel = (Panel) theInflatedPanel.findViewById(R.id.panel);
//
//		if(!mPanels.contains(panel)) {
//			gv.addView(theInflatedPanel); // add it to the view
//			mService.add(panel);		  // store the panel
//			mService.createTunnel(panel, 1); // create the tunnel;
//			mPanels.add(panel);	 // Store it
//		}	
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
