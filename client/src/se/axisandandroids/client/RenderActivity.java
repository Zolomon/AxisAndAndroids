package se.axisandandroids.client;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class RenderActivity extends Activity {
	private static final String TAG = RenderActivity.class.getSimpleName();
	private CtrlService mService; 
	private boolean mBound;
	
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
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName compName) {
			mBound = false;
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.client_menu, menu);
		return true;
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
