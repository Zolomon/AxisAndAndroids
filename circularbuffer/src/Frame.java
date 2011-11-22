
/* Autoboxing around an image contained in a byte[]. This is meant to be
 * put in the buffer.														*/

class Frame {
	public byte[] x;

	public Frame(byte[] x) {
		this.x = x;
	}

	public String toString() {
		if (x == null) {
			return "null";
		}							
		String str = "[ ";
		for (int l = 0; l < x.length; ++l) {
			str += x[l] + " ";
		}	
		str += " ]";
		return str;
	}
}