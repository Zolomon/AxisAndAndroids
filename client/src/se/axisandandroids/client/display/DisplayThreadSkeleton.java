package se.axisandandroids.client.display;


import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.networking.Protocol;
import se.lth.cs.fakecamera.Axis211A;


public class DisplayThreadSkeleton extends Thread {
			
		protected final int BUFFERSIZE = 30;
		protected final int INITIAL_BUFFER_WAIT_MS = 1500;
		protected final int FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;
		protected final byte[] jpeg = new byte[FRAMESIZE];

		protected DisplayMonitor disp_monitor;
		public FrameBuffer mailbox;
				
				
		/**
		 * DisplayThread superclass.
		 * @param disp_monitor, display monitor shared between several DisplayThreads.
		 */
		public DisplayThreadSkeleton(DisplayMonitor disp_monitor) {
			this.disp_monitor = disp_monitor;
			mailbox = new FrameBuffer(BUFFERSIZE, FRAMESIZE);
		}
				
		
		@Override
		public void run() {
			int len = 0;
			long delay = -1;
			long timestamp = -1;
			
			mailbox.awaitBuffered(INITIAL_BUFFER_WAIT_MS);
			
			int sync = disp_monitor.getSyncMode();

			while (! interrupted()) {
				len = mailbox.get(jpeg);
				timestamp = getTimestamp();
								
				try {	
					if (disp_monitor.getSyncMode() == Protocol.SYNC_MODE.SYNC) {
						delay = disp_monitor.syncFrames(timestamp);
					} else {
						delay = asyncFrames(timestamp);
					}										
				} catch (InterruptedException e) {
					System.err.println("syncFrames got interrupted");
					e.printStackTrace();
				}								
				showImage(delay, len); // Override for Platform Dependent show image
				sync = disp_monitor.chooseSyncMode(delay); 
			}
		}
		
		
		private long t0 = 0;
		private long lag = 0;
		
		protected synchronized long asyncFrames(long timestamp) throws InterruptedException {
			/* No old showtime exists for ANY frame, display now! */
			if (t0 <= 0) {
				t0 = System.currentTimeMillis();
				lag = t0 - timestamp;
				return t0 - timestamp;	
			}
			
			/* Calculate showtime for this thread in relation to FIRST SHOWN FRAME. */
			long showtime = lag + timestamp;				
			long diffTime;	// Time to showtime_new
		
			/* Wait until it is:
			 * 1) The right time. */
			while ((diffTime = showtime - System.currentTimeMillis()) > 0) {
				wait(diffTime);		
			} 		
			
			return System.currentTimeMillis() - timestamp; // The real delay
		}
		
		
		/**
		 * This is the place to show the image in jpeg[0:len].
		 * Override this for platform dependent GUI.
		 * @param delay, show time delay.
		 */
		protected void showImage(long delay, int len) {
			// Override for Platform Dependent show image
			System.out.printf("Delay: %d\n", delay);
		}
		
		
		/**
		 * Extract timestamp from image byte array.
		 * @return timestamp in ms.
		 */
		protected long getTimestamp() {		
			int offset = 0;

			/* Decode Timestamp */
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
