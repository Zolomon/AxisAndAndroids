
/* Autoboxing around an image contained in a byte[]. This is meant to be
 * put in the buffer.														*/

class Frame {
	
	public byte[] x;
	public int len;

	public Frame(int FRAMESIZE) {
		x = new byte[FRAMESIZE]; 
		len = 0;
	}
	
	public Frame(byte[] x, int len) {
		this.x = x;
		this.len = len;
	}
	
	public Frame(Frame other) {
		this.len = other.len;
		this.x = new byte[other.x.length];
		System.arraycopy(x, 0, other.x, 0, len);
	}

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