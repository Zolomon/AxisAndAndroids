package se.axisandandroids.server;

import se.axisandandroids.networking.Protocol;
import se.lth.cs.fakecamera.Axis211A;

public class CameraThread extends Thread{
	private Axis211A myCamera;
	private byte[] jpeg;
	private CameraMonitor cm;


	public CameraThread(CameraMonitor cm){
		this.cm = cm;
		myCamera = new Axis211A();
		jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
	}
	
	public void run(){
		if(cameraConnect()){
			while(true){
				recieveJPEG();
			}
		}
	}
	
	private boolean cameraConnect(){
		if(!myCamera.connect()){
			System.out.println("Failed to connect to camera!");
			System.exit(1);
			return false;
		}	
		else{
			System.out.println("Camera connected!");
			return true;
		}
	}
	
	private void recieveJPEG(){
		if(cm.getDislayMode() == Protocol.DISP_MODE.IDLE){
			int len = myCamera.getJPEG(jpeg,0);
			//os.write(jpeg,0,len);
			try {
				this.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(cm.getDislayMode() == Protocol.DISP_MODE.MOVIE){
			while(cm.getDislayMode() == Protocol.DISP_MODE.MOVIE){
				int len = myCamera.getJPEG(jpeg,0);
			}

		}
	}


}
