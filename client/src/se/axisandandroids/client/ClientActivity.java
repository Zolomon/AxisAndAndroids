package se.axisandandroids.client;

import static se.axisandandroids.client.controller.ControllerProtocol.C_QUIT;
import static se.axisandandroids.client.controller.ControllerProtocol.C_SHOW_DISPLAYS;
import static se.axisandandroids.client.controller.ControllerProtocol.V_REQUEST_CONNECTIONS;
import static se.axisandandroids.client.controller.ControllerProtocol.V_REQUEST_DISPLAYS;
import static se.axisandandroids.client.controller.ControllerProtocol.V_REQUEST_QUIT;
import se.axisandandroids.client.model.ModelData;
import se.axisandandroids.client.service.CtrlService;
import se.axisandandroids.client.service.CtrlService.LocalBinder;
import se.axisandandroids.client.service.controller.Controller;
import se.axisandandroids.client.service.model.Model;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

public class ClientActivity extends Activity implements Handler.Callback,
		OnClickListener {
	private static final String TAG = ClientActivity.class.getSimpleName();

	private CtrlService mService;
	private Controller mController;
	private Model mModel;
	private boolean mBound;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		((Button) findViewById(R.id.btnConnect)).setOnClickListener(this);

		// controller = new Controller(new Model());
		// controller.addOutboxHandler(new Handler(this));
		//
		// controller.getInboxHandler().sendEmptyMessage(V_REQUEST_CONNECTIONS);
	}

	// @Override
	// public void onDestroy() {
	// try {
	// controller.dispose();
	// } catch (Throwable t) {
	// Log.e(TAG, "Failed to destroy the controller", t);
	// }
	//
	// super.onDestroy();
	// }

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
			//mController.getInboxHandler().sendEmptyMessage(V_REQUEST_DISPLAYS);
			Toast.makeText(this, "Displays", Toast.LENGTH_SHORT);
			return true;
		case R.id.menu_quit:
			//mController.getInboxHandler().sendEmptyMessage(V_REQUEST_QUIT);
			Toast.makeText(this, "Quit", Toast.LENGTH_SHORT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		TableLayout tl = (TableLayout) findViewById(R.id.tlConnections);

		/**
		 * You can also inflate views explicitly by using the LayoutInflater. In
		 * that case you have to: 1) Get an instance of the LayoutInflater 2)
		 * Specify the XML to inflate 3) Use the returned View
		 */

		// Inflate layout
		// 2 and 3
		LayoutInflater inflater = LayoutInflater.from(ClientActivity.this);
		View theInflatedView = inflater.inflate(R.layout.connection_item, tl);

		Button btnDisconnect = (Button) theInflatedView
				.findViewById(R.id.btnDisconnect);
		btnDisconnect.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Test",
						Toast.LENGTH_LONG).show();
			}
		});
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
			mController = (Controller) binder.getController();
			mModel = (Model) binder.getModel();
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName compName) {
			mBound = false;
		}
	};

	public boolean handleMessage(Message msg) {
		Log.d(TAG, "Received message: " + msg);

		switch (msg.what) {
		case C_QUIT:
			onQuit();
			return true;
		case C_SHOW_DISPLAYS:
			onShowDisplays((ModelData) msg.obj);
			return true;
		}
		return false;
	}

	private void onShowDisplays(ModelData data) {
		Log.d(TAG, "onShowDisplays()");
		Intent intent = new Intent(this, RenderActivity.class);
		startActivity(intent);
	}

	private void onQuit() {
		finish();
	}
}