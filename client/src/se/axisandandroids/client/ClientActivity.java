package se.axisandandroids.client;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class ClientActivity extends Activity {
	/** Called when the activity is first created. */
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
				 * LayoutInflater. In that case you have to: 
				 * 1) Get an instance of the LayoutInflater 
				 * 2) Specify the XML to inflate 
				 * 3) Use the returned View
				 */
				
				LayoutInflater inflater = LayoutInflater.from(ClientActivity.this); // Inflate layout
				View theInflatedView = inflater.inflate(R.layout.connection_item, tl); // 2 and 3
				
				Button btnDisconnect = (Button) theInflatedView.findViewById(R.id.btnDisconnect);
				btnDisconnect.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
}