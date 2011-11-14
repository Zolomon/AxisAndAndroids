package se.axisandandroids.server;
import se.axisandandroids.networking.Connection.DISP_MODE;
import se.lth.cs.fakecamera.*;
import java.io.*;

public class CameraThread {
	private Axis211A myCamera;
	private byte[] jpeg;
//	final static int IDLE = 0;		// USE ENUMS INSTEAD ...
//	final static int MOVIE = 1;
	DISP_MODE display_mode;

	
	public CameraThread(){
		myCamera = new Axis211A();
		jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
		display_mode = DISP_MODE.IDLE;
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
	
	public boolean setDisplayMode(DISP_MODE display_mode){
		if(display_mode != DISP_MODE.MOVIE || 
				display_mode !=  DISP_MODE.IDLE || 
				display_mode != DISP_MODE.AUTO){
			System.out.println("Invalid mode!");
			return false;
		}
		else{
			this.display_mode = display_mode;
			return true;
		}
	}
	
	private void recieveJPEG(){
		if(display_mode == DISP_MODE.IDLE){
			int len = myCamera.getJPEG(jpeg,0);
			//os.write(jpeg,0,len);
			myCamera.close();
		}
	}


}
