package se.axisandandroids.client.display;


import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.networking.Protocol;
import se.lth.cs.fakecamera.Axis211A;


/**
 * Great grand father of DisplayThreads.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class DisplayThreadSkeleton extends Thread {
			
		protected final int BUFFERSIZE = 3;
		protected final int INITIAL_BUFFER_WAIT_MS = 100;				
		protected final int FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;
		
		protected final byte[] jpeg = new byte[FRAMESIZE];

		protected final DisplayMonitor disp_monitor;
		public final FrameBuffer mailbox;
		public long delay;
				
		/**
		 * DisplayThread superclass.
		 * @param disp_monitor, display monitor shared between several DisplayThreads.
		 */
		public DisplayThreadSkeleton(DisplayMonitor disp_monitor) {
			this.disp_monitor = disp_monitor;
			mailbox = new FrameBuffer(BUFFERSIZE, FRAMESIZE);
			//this.setPriority(MAX_PRIORITY);			
		}
										
		@Override
		public void run() {
			int len = 0;
			long delay = -1;
			long timestamp = -1;
			
			
			mailbox.awaitBuffered(INITIAL_BUFFER_WAIT_MS);
			
						
			while (! interrupted()) {
				len = mailbox.get(jpeg);
				timestamp = getTimestamp();
				
				try {
					int sync_mode = disp_monitor.getSyncMode();
					if (sync_mode == Protocol.SYNC_MODE.SYNC) {
						delay = disp_monitor.syncFrames(timestamp);
					} else if (sync_mode == Protocol.SYNC_MODE.AUTO){
						//delay = asyncFrames(timestamp);
						delay = asyncAsFastAsPossible(timestamp);	
						disp_monitor.chooseSyncMode(Thread.currentThread().getId(), delay);	
					} else { /* CASE: sync_mode == Protocol.SYNC_MODE.ASYNC */
						//delay = asyncFrames(timestamp);
						delay = asyncAsFastAsPossible(timestamp);	
					}		
					showImage(timestamp, delay, len, sync_mode); // Override for Platform Dependent show image
				} catch (InterruptedException e) {
					System.err.println("syncFrames got interrupted!");
					e.printStackTrace();
					System.out.println("Flushing mailbox");
					mailbox.flush();

					try {
						join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		
		
		protected long asyncAsFastAsPossible(long timestamp) {
			delay = System.currentTimeMillis() - timestamp;
			return delay;
		}
		
		
		
		private long t0 = 0;
		private long lag = 0;		
						
		
		protected long asyncFrames(long timestamp) throws InterruptedException { // PUT IN LOCAL MONITOR ?
						
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
				sleep(diffTime);		
			} 		
			
			delay = System.currentTimeMillis() - timestamp;
									
			return delay; // The real delay
		}
		
				
		
		/**
		 * This is the place to show the image in jpeg[0:len].
		 * Override this for platform dependent GUI.
		 * @param delay, show time delay.
		 */
		protected void showImage(long timestamp, long delay, int len, int sync_mode) {			
			// Override for Platform Dependent show image
			System.out.printf("Delay: 	  %d\tSync Mode: 	 %d\n", delay, sync_mode);
		}
		
		
		/**
		 * Extract timestamp from image byte array.
		 * @return timestamp in ms.
		 */
		protected long getTimestamp() {
			
			/* Decode Timestamp */ /*
			int offset = 0;
			long seconds = ( ( (long)jpeg[25+offset]) << 24 ) & 0xff000000 | 
						   ( ( (long)jpeg[26+offset]) << 16 ) & 0x00ff0000 | 
						   ( ( (long)jpeg[27+offset]) << 8  ) & 0x0000ff00 | 
						   (   (long)jpeg[28+offset]		  & 0x000000ff ); 
			long hundreths = ( (long)jpeg[29+offset] & 0x000000ff );

			return 1000*seconds + 10*hundreths;
			*/
						
			return 1000L*(((jpeg[25]<0?256+jpeg[25]:jpeg[25])<<24)+((jpeg[26]<0?256+jpeg[26]:jpeg[26])<<16)+
					((jpeg[27]<0?256+jpeg[27]:jpeg[27])<<8)+(jpeg[28]<0?256+jpeg[28]:jpeg[28]))+
					10L*(jpeg[29]<0?256+jpeg[29]:jpeg[29]);			
		}
		
		
	}
