package se.axisandandroids.buffer;



/**
 * Frame class is a wrapper around a byte array and an integer saying
 * how many of the byte arrays elements are valid. 
 * @author jg
 *
 */
public class Frame implements Comparable<Frame> {
	
	public int len;
	public byte[] x;

	
	/**
	 * Create an empty frame.
	 */
	public Frame() {
		x = null;
		len = 0;
	}
	
	/**
	 * Create an empty, but initialized frame.
	 */
	public Frame(int FRAMESIZE) {
		x = new byte[FRAMESIZE];
		len = 0;
	}
	
	/**
	 * Create a frame encapsulating frame data in x and where
	 * len is index if last element belonging to the frame.
	 * @param x, frame data buffer array.
	 * @param len, length of valid data.
	 */
	public Frame(byte[] x, int len) {
		//this.x = x; // no copy ?
		System.arraycopy(x, 0, this.x, 0, len); // copy ?
		this.len = len;
	}	
	
	public Frame(byte[] x, int len, int FRAMESIZE) {
		this.x = new byte[FRAMESIZE];
		System.arraycopy(x, 0, this.x, 0, len); // copy ?
		this.len = len;
		
	}	
	
	public Frame(byte[] x, int len, boolean copy) {
		if (copy) {
			System.arraycopy(x, 0, this.x, 0, len); // copy
		} else {
			this.x = x; 							// no copy
		}
		this.len = len;
	}	

	/**
	 * Copy Constructor. 
	 * Create a Frame object from another Frame object.
	 * @param other, Frame object to be copied.
	 */
	public Frame(Frame other) {
		this.len = other.len;
		this.x = new byte[other.x.length];
		System.arraycopy(other.x, 0, this.x, 0, len);
	}

	/**
	 * Print it all out.
	 */
	public String toString() {
		if (x == null) {
			return "null";
		}							
		String str = "[ ";
		for (int l = 0; l < len; ++l) {
			str += x[l] + " ";
		}	
		str += " ]";
		return str;
	}
	
	
	/**
	 * Extract timestamp from image byte array.
	 * @return timestamp in ms.
	 */
	protected long getTimestamp() {
		int offset = 0; // If you decide to use the header for piggy-backing.
		
		int pos = 25;
		if (x.length < 25) { 	// FOR DEBUG
			return x[0]; 		// FOR DEBUG
		} 						// FOR DEBUG
		
		/* Decode Timestamp */ 
		long seconds = ( ( (long)x[pos+offset]) << 24 ) & 0xff000000 | 
					   ( ( (long)x[pos+1+offset]) << 16 ) & 0x00ff0000 | 
					   ( ( (long)x[pos+2+offset]) << 8  ) & 0x0000ff00 | 
					   (   (long)x[pos+3+offset]		  & 0x000000ff ); 
		long hundreths = ( (long)x[pos+4+offset] & 0x000000ff );
		return 1000*seconds + 10*hundreths;
	}
	
	@Override
	public int compareTo(Frame other) {		
		return (int) (this.getTimestamp() - other.getTimestamp());
	}
}