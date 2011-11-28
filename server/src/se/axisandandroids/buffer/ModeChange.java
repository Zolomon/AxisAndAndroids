package se.axisandandroids.buffer;

/**
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ModeChange extends Command {
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