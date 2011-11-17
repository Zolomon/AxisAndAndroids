package se.axisandandroids.server;

import se.axisandandroids.networking.Protocol;
import se.lth.cs.fakecamera.*;


public class CameraMonitor {

	private int display_mode;
	private int sync_mode;
	
	public CameraMonitor(){
		display_mode = Protocol.DISP_MODE.IDLE;
	}
	
	public synchronized boolean setDisplayMode(int display_mode){
		if(display_mode != Protocol.DISP_MODE.MOVIE || 
				display_mode !=  Protocol.DISP_MODE.IDLE || 
				display_mode != Protocol.DISP_MODE.AUTO){
			System.out.println("Invalid Display mode!");
			return false;
		}
		else{
			this.display_mode = display_mode;
			return true;
		}
	}
	
	public synchronized int getDislayMode(){
		return display_mode;
	}
	
//	public synchronized boolean setSyndMode(int sync_mode){
//		if(sync_mode != Protocol.SYNC_MODE.ASYNC || sync_mode != Protocol.SYNC_MODE.SYNC || sync_mode != Protocol.SYNC_MODE.AUTO)
//			System.out.println("Invalid Sync mode!");
//		else{
//			this.sync_mode = sync_mode;
//			return true;
//		}
//	}
//	
//	public synchronized int getSyncMode(){
//		return sync_mode;
//	}
}
