package se.axisandandroids.client;

public class DrawThread extends Thread {
	private CameraMonitor cm;

	public DrawThread(CameraMonitor cm) {
		this.cm = cm;
	}

	@Override
	public void run() {
		cm.connect();

		while (cm.isConnected()) {
			cm.nextImage();
			System.out.println("nextImage");
		}

		cm.close();
	}
}
