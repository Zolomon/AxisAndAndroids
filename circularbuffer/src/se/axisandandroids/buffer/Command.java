package se.axisandandroids.buffer;
import se.axisandandroids.networking.Protocol;




/* CameraServer can pass this to it's send thread. If camera only ever send
 * images a Frame will be enough.											
 * For commands without parameters an int might be sufficient.
 * */
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


class ModeChange extends Command {
	public int mode;

	public ModeChange(int cmd, int mode) {
		super(cmd);
		this.mode = mode;
	}

	public String toString() {
		String str = "Command " + cmd; 
		str += " Mode " + mode;
		return str;
	}
}



/* Autoboxing around an image contained in a byte[]. This is meant to be
 * put in the buffer.														*/
class FrameCommand extends Command {
	
	public int len;
	public byte[] x;

	/**
	 * Create an empty frame.
	 */
	public FrameCommand() {
		super(Protocol.COMMAND.IMAGE);
		x = null;
		len = 0;
	}
	
	/**
	 * Create a frame encapsulating frame data in x and where
	 * len is index if last element belonging to the frame.
	 * @param x, frame data buffer array.
	 * @param len, length of valid data.
	 */
	public FrameCommand(byte[] x, int len) {
		super(Protocol.COMMAND.IMAGE);
		this.x = x;
		this.len = len;
	}
	
	/**
	 * Copy Constructor. 
	 * Create a Frame object from another Frame object.
	 * @param other, Frame object to be copied.
	 */
	public FrameCommand(FrameCommand other) {
		super(Protocol.COMMAND.IMAGE);
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

