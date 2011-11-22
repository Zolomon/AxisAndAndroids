package se.axisandandroids.client;

import se.axisandandroids.client.service.CtrlService;
import se.axisandandroids.client.service.CtrlService.LocalBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

public class RenderActivity extends Activity {
	private static final String TAG = RenderActivity.class.getSimpleName();
	private CtrlService mService;
	private boolean mBound;
	private ImageView mDisplay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_grid);
		mDisplay = (ImageView) findViewById(R.id.ivDisplay);
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
			mService.cm.connect();
			new UpdateImageTask().execute(mService.cm);
		}

		public void onServiceDisconnected(ComponentName compName) {
			mBound = false;
		}
	};

	private class UpdateImageTask extends
	AsyncTask<DisplayMonitor, Bitmap, Void> {
		@Override
		protected Void doInBackground(final DisplayMonitor... cm) {
			new Thread(new Runnable() {

				public void run() {
					while (true) {
						publishProgress(cm[0].nextImage());
					}
				}
			}).start();

			return null;
		}

		@Override
		protected void onProgressUpdate(Bitmap... values) {
			mDisplay.setImageBitmap(values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}

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
