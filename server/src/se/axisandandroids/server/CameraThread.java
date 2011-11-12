package se.axisandandroids.server;
import se.lth.cs.fakecamera.*;
import java.io.*;

public class CameraThread {
private Axis211A myCamera;
private byte[] jpeg;
final static int IDLE = 0;
final static int MOVIE = 1;
int mode;

	public CameraThread(){
		myCamera = new Axis211A();
		jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
		mode = IDLE;
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
	public boolean setMode(int mode){
		if(mode != 0 || mode != 1){
			System.out.println("Invalid mode!");
			return false;
		}
		else{
			this.mode = mode;
			return true;
		}
	}
	private void recieveJPEG(){
		if(mode == IDLE){
			int len = myCamera.getJPEG(jpeg,0);
			os.write(jpeg,0,len);
			myCamera.close();
		}
	}
	
    
}
