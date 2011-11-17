package se.axisandandroids.client;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class RenderActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_grid);
		
//		Intent intent = getIntent();
//		// Receiving connections
//		int connectionCount = Integer.parseInt(intent.getStringExtra("connectionCount"));
//		ArrayList<HostPort> connections = new ArrayList<HostPort>();
//		for(int i = 0; i < connectionCount; i++) {
//			connections.add(new HostPort(intent.getStringExtra("host"), Integer.parseInt(intent.getStringExtra("port"))));
//			System.out.println(connections.get(i).toString());
//		}
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
		case R.id.menu_connections:
			Toast.makeText(getApplicationContext(), "Connections",
					Toast.LENGTH_SHORT);
			return true;
		case R.id.menu_quit:
			Toast.makeText(getApplicationContext(), "Quit",
					Toast.LENGTH_SHORT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
