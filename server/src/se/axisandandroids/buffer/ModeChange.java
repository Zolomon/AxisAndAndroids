package se.axisandandroids.buffer;

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