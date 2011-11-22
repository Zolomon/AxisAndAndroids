package se.axisandandroids.client.service;

import se.lth.cs.fakecamera.Axis211A;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class CameraMock {
	Axis211A cam = new Axis211A();
	byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

	public CameraMock() {
		
	}
	
	public void test(ImageView view) {
		int len = cam.getJPEG(jpeg, 0);
		Bitmap bmp = BitmapFactory.decodeByteArray(jpeg, 0, len);
		view.setImageBitmap(bmp);
	}

	public Axis211A getCamera() {
		return cam;
	}
}
