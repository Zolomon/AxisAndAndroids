package se.axisandandroids.networking;


public class Protocol {

	public class COMMAND {	
		public final static int IMAGE 		= 0;
		public final static int SYNC_MODE 	= 1;
		public final static int DISP_MODE 	= 2;
		public final static int CONNECTED 	= 3; 
		public final static int END_MSG 	= 4;
	}

	public class SYNC_MODE {		
		public final static int AUTO 		= 0;
		public final static int SYNC 		= 1;
		public final static int ASYNC 		= 2;
	}

	public class DISP_MODE {		
		public final static int AUTO 		= 0;
		public final static int IDLE 		= 1;
		public final static int MOVIE 		= 2;
	}
	
}
