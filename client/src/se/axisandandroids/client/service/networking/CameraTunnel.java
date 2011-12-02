package se.axisandandroids.client.service.networking;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.DisplayThread;
import se.axisandandroids.client.display.NewImageCallback;
import se.axisandandroids.client.display.Panel;
import se.axisandandroids.client.service.networking.ClientReceiveThread;
import se.axisandandroids.client.service.networking.ClientSendThread;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.UDP_ClientConnection;


/**
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class CameraTunnel {

	private UDP_ClientConnection	mConnection;
	private DisplayMonitor 			mDisplayMonitor;
	private DisplayThread 			mDisplayThread;
	private ClientSendThread 		mSendThread;
	private ClientReceiveThread 	mReceiveThread;
	private NewImageCallback 		mNewImageCallback;
	private int 					mId;
	private Panel 					mPanel;

	public CameraTunnel(UDP_ClientConnection c, Panel p, DisplayMonitor disp_monitor,
			int id) {
		this.mId = id;
		this.mConnection = c;
		this.mDisplayMonitor = disp_monitor;
		this.mNewImageCallback = p.getNewImageCallback();
		this.mPanel = p;
		createThreads();
	}

	/**
	 * For DESKTOP client which has no Panel.
	 * 
	 * @param c
	 * @param disp_monitor
	 * @param id
	 */
	public CameraTunnel(UDP_ClientConnection c, DisplayMonitor disp_monitor, int id) {
		this.mId = id;
		this.mConnection = c;
		this.mDisplayMonitor = disp_monitor;
		createThreads();
	}

	private void createThreads() {
		System.out.println("Creating Threads: DisplayThread, ReceiveThread, SendThread...");
		
		mSendThread = new ClientSendThread(mConnection, mDisplayMonitor);
		mDisplayThread = new DisplayThread(mDisplayMonitor, mNewImageCallback);
		mReceiveThread = new ClientReceiveThread(mConnection, mDisplayMonitor, mDisplayThread.mailbox, mSendThread.mailbox);
		startThreads();
	}

	private void startThreads() {
		System.out
				.println("Starting Threads: DisplayThread, ReceiveThread, SendThread...");
		mDisplayThread.start();
		mReceiveThread.start();
		mSendThread.start();
	}

	private void interruptThreads() {
		System.out
				.println("Interrupting Threads: DisplayThread, ReceiveThread, SendThread...");
		mDisplayThread.interrupt();
		mReceiveThread.interrupt();
		mSendThread.interrupt();
	}

	public CircularBuffer getSendMailbox() {
		return mSendThread.mailbox;
	}

	public void disconnect() {
		mConnection.disconnect();	
		interruptThreads(); // ?
	}

	public int getId() {
		return mId;
	}

	public void playPanel() {
		mPanel.play();
	}

	public void pausePanel() {
		mPanel.pause();
	}
}
