package se.axisandandroids.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import se.axisandandroids.client.service.CtrlService;
import se.axisandandroids.client.service.CtrlService.LocalBinder;
import se.axisandandroids.client.service.networking.ConnectionHandler;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class ClientActivity extends Activity implements OnClickListener {
	private static final String TAG = ClientActivity.class.getSimpleName();

	private CtrlService mService;
	private boolean mBound;
	private ConnectionHandler ch;
	Button btnConnect, btnDisconnect;
	LayoutInflater linflater;
	TableLayout tl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		((Button) findViewById(R.id.btnConnect)).setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.client_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_displays:
			onShowDisplays();
			return true;
		case R.id.menu_quit:
			onQuit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		EditText etHost = (EditText) findViewById(R.id.etHost);
		EditText etPort = (EditText) findViewById(R.id.etPort);

		String host = etHost.getText().toString();
		String port = etPort.getText().toString();
		
		try {
			ch.add(new Connection(host, Integer.parseInt(port)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final TableLayout tl = (TableLayout) findViewById(R.id.tlConnections);
		LayoutInflater inflater = LayoutInflater.from(ClientActivity.this);
		final View theInflatedView = inflater.inflate(R.layout.connection_item,
				null);

		TextView tvHostAndPort = (TextView) theInflatedView
				.findViewById(R.id.etHostAndPort);
		tvHostAndPort.setText(host + ":" + port);

		Button btnDisconnect = (Button) theInflatedView
				.findViewById(R.id.btnDisconnect);

		btnDisconnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tl.removeView(theInflatedView);
			}
		});

		tl.addView(theInflatedView);
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
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			ch = mService.ch;
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName compName) {
			mBound = false;
		}
	};

	private void onShowDisplays() {
		Log.d(TAG, "onShowDisplays()");
		Intent intent = new Intent(this, RenderActivity.class);
		startActivity(intent);
	}

	private void onQuit() {
		finish();
	}
}