

/* CameraServer can pass this to it's send thread. If camera only ever send
 * images a Frame will be enough.											
 * For commands without parameters an int might be sufficient.
 * */
public class Command {
	public int cmd;
	public int len;
	public byte[] x;

	public Command(int cmd, byte[] x, int len) {
		this.cmd = cmd;
		this.len = len;
		this.x = x;
	}

	public String toString() {
		String str = "Command " + cmd; 
		if (x == null) {
			return str;
		}									
		str += "data: [ ";
		for (int l = 0; l < x.length; ++l) {
			str += x[l] + " ";
		}	
		str += " ]";
		return str;
	}
}
