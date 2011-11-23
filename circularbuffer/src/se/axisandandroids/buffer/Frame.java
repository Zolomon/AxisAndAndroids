package se.axisandandroids.buffer;

/* Autoboxing around an image contained in a byte[]. This is meant to be
 * put in the buffer.														*/

class Frame {
	
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
		this.x = x;
//		System.arraycopy(x, 0, this.x, 0, len);	
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
		System.arraycopy(x, 0, other.x, 0, len);
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
}