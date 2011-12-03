package se.axisandandroids.buffer;

/** 
 * CameraServer can pass this to it's send thread. If camera only ever send
 * images a Frame will be enough.											
 * For commands without parameters an int might be sufficient.
 */
public class Command {
	public int cmd;
	
	public Command(int cmd) {
		this.cmd = cmd;		
	}

	public String toString() {
		String str = "Command " + cmd; 
		return str;
	}
}