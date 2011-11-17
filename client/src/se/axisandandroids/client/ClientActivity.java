package se.axisandandroids.client;

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
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

public class ClientActivity extends Activity {

	private CtrlService mService;
	private Controller mController;
	private Model mModel;
	private boolean mBound;
	private static final String TAG = ClientActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button btn = (Button) findViewById(R.id.btnConnect);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				TableLayout tl = (TableLayout) findViewById(R.id.tlConnections);

				/**
				 * You can also inflate views explicitly by using the
				 * LayoutInflater. In that case you have to: 1) Get an instance
				 * of the LayoutInflater 2) Specify the XML to inflate 3) Use
				 * the returned View
				 */

				LayoutInflater inflater = LayoutInflater
						.from(ClientActivity.this); // Inflate layout
				View theInflatedView = inflater.inflate(
						R.layout.connection_item, tl); // 2 and 3

				Button btnDisconnect = (Button) theInflatedView
						.findViewById(R.id.btnDisconnect);
				btnDisconnect.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "Test",
								Toast.LENGTH_LONG).show();
					}
				});
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
}