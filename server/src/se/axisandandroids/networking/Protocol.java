package se.axisandandroids.networking;



/**
 * The codes for the used network protocol.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class Protocol {
	
	public class COMMAND { // Choose according to your liking.
		public final static int IMAGE 		= 10;
		public final static int SYNC_MODE 	= 20;
		public final static int DISP_MODE 	= 30;
		public final static int CONNECTED 	= 40;
		public final static int CLOCK_SYNC  = 50;
	}
	
	public class SYNC_MODE { // DO NOT CHANGE USED FOR INDEXES
		public final static int AUTO 		= 0;
		public final static int SYNC 		= 1;
		public final static int ASYNC 		= 2;
	}

	public class DISP_MODE {// DO NOT CHANGE USED FOR INDEXES		
		public final static int AUTO 		= 0;
		public final static int IDLE 		= 1;
		public final static int MOVIE 		= 2;
	}	
}


