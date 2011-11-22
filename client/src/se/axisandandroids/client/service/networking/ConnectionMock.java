package se.axisandandroids.client.service.networking;

import android.graphics.Bitmap;
import se.lth.cs.fakecamera.Axis211A;

public class ConnectionMock {
	private Axis211A camera;
	private byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
	private int id;
	
	public ConnectionMock() {
		camera = new Axis211A();
		camera.connect();
	}
	
	public Bitmap nextFrame() {
		int len = camera.getJPEG(jpeg, 0);
		
		return null;
	}
	
	public void setId(int id) {
		this.id = id;
	}

}
