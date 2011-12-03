package se.axisandandroids.buffer;

import se.axisandandroids.networking.Protocol;

public class ClockSync extends Command {
	public long time;

	
	public ClockSync() {
		super(Protocol.COMMAND.CLOCK_SYNC);
	}
	
	public ClockSync(long time) {
		super(Protocol.COMMAND.CLOCK_SYNC);
		this.time = time;
	}

	public byte[] getBytes() {
		long s = time / 1000;
		//long h = (time - s*1000) / 10;
		long ms = (time - s*1000);
		byte[] T = new byte[6];
		T[0] = (byte)((s & 0xff000000) >> 24);
		T[1] = (byte)((s & 0x00ff0000) >> 16);
		T[2] = (byte)((s & 0x0000ff00) >> 8);
		T[3] = (byte)( s & 0x000000ff);				
		T[4] = (byte)((ms & 0x0000ff00) >> 8);
		T[5] = (byte)( ms & 0xff);	
		return T;
	}

	public static long bytesToLong(byte[] T) {
		long seconds = ( ( (long)T[0]) << 24 ) & 0xff000000 | 
					   ( ( (long)T[1]) << 16 ) & 0x00ff0000 | 
					   ( ( (long)T[2]) << 8  ) & 0x0000ff00 | 
					   (   (long)T[3]		   & 0x000000ff ); 
		long millis  = ( ( (long)T[4]) << 8  ) & 0x0000ff00 |
					     ( ( (long)T[5])         & 0x000000ff );
		return seconds*1000 + millis;
	}

	public String toString() {
		String str = "Command " + cmd; 
		str += " Time " + time;
		return str;
	}
}