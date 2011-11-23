package se.axisandandroids.client.display;

import se.axisandandroids.buffer.FrameBuffer;
import se.lth.cs.fakecamera.Axis211A;

public class DisplayThread extends Thread {
	
	
	private DisplayMonitor disp_monitor;
	private final int BUFFERSIZE = 10;
	private final int FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;
	private byte[] jpeg = new byte[FRAMESIZE];
	
	
	public FrameBuffer mailbox;
	
	public DisplayThread(DisplayMonitor disp_monitor) {
		this.disp_monitor = disp_monitor;
		mailbox = new FrameBuffer(BUFFERSIZE, FRAMESIZE);
	}
	
	@Override
	public void run() {
		int len = 0;
		long delay = -1;
		long timestamp = -1;
		
		while (! interrupted()) {
			len = mailbox.get(jpeg);
			timestamp = getTimestamp(jpeg);
			delay = disp_monitor.syncFrames(timestamp);
			
			// CALLBACK
			System.out.printf("ShowTime Delay: %d", delay);
		}
	}
	
	private long getTimestamp(byte[] jpeg) { // WORK IN PROGRESS !!!
		
		int offset = 0;

		/*		
 		// This was sent:
 		long seconds = stime / 1000;
		long hundredths = (stime - seconds*1000) / 10;		
		image[25+offset] = (byte)((seconds & 0xff000000) >> 24);
		image[26+offset] = (byte)((seconds & 0x00ff0000) >> 16);
		image[27+offset] = (byte)((seconds & 0x0000ff00) >> 8);
		image[28+offset] = (byte)(seconds & 0x000000ff);				
		image[29+offset] = (byte)(hundredths & 0xff);				
		*/

				
		
		/* Decode */
		long seconds = ( ( (long)jpeg[25+offset]) << 24 ) & 0xff000000 | 
					   ( ( (long)jpeg[26+offset]) << 16 ) & 0x00ff0000 | 
					   ( ( (long)jpeg[27+offset]) << 8  ) & 0x0000ff00 | 
					   (   (long)jpeg[28+offset]		  & 0x000000ff ); 
		long hundreths = ( (long)jpeg[29+offset] & 0x000000ff );

		//System.out.printf("Seconds: %d\n", seconds);
		//System.out.printf("Hundreths: %d\n", hundreths);
				
		return 1000*seconds + 10*hundreths;
	}
	
	
}
