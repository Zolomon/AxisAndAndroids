package se.axisandandroids.networking_samples;

/* ---------------------------------------------------
 * Simple TCP client for sending lines of user input 
 * to some server.
 * 
 * Based on this tutorial:
 * 		http://download.oracle.com/javase/tutorial/...
 * 				networking/sockets/readingWriting.html
 * --------------------------------------------------- */

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.lth.cs.fakecamera.Axis211A;


/**
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class UDP_Client {

	private Socket socket;
	private InetAddress host;	
	private final static int default_port = 5555;
	private int port;
	private int udp_port;

	public UDP_Client(InetAddress host, int port) {
		this.host = host;
		this.port = port;
		this.udp_port = port+1;
		connect();
	}

	public void connect() {
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			System.err.println("Unknown host.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("io-exception.");
			System.exit(1);
		}
		System.out.println("Connection Setup Complete");
	}

	public void disconnect() throws IOException {
		socket.close();
	}

	public static void main(String[] args) {
		InetAddress addr = null;
		int port = default_port;

		try {
			addr = InetAddress.getByName("localhost");
			if (args.length >= 1) {
				addr = InetAddress.getByName(args[0]);
			}
		} catch (UnknownHostException e) {
			System.err.println("Unknown host.");
			System.exit(1);
		}

		if (args.length >= 2) {
			port = Integer.parseInt(args[1]);
		}

		UDP_Client tcpclient = new UDP_Client(addr, port);

		// *** --- CHANGE HERE WHAT TO RUN --- ***

		tcpclient.testFakeCamUDP();

		// *** ------------------------------- ***
	}

	public void testFakeCamUDP() {
		new GUI();
	}		

	class GUI extends JFrame {
		private static final long serialVersionUID = 1L;
		ImagePanel imagePanel;
		boolean firstCall = true;
		//Connection con;
		byte [] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

		public GUI() {
			super();						
			imagePanel = new ImagePanel();			
			this.setSize(320, 200);
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(imagePanel, BorderLayout.NORTH);			
			this.setLocationRelativeTo(null);
			this.pack();

			//this.con = new Connection(socket);
			refreshImage();
		}

		public synchronized void refreshImage() {		
			System.out.println("** Refreshing Image ------------------------- ");			

			DatagramSocket server = null;
			try {
				server = new DatagramSocket(udp_port);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			DatagramPacket packet = new DatagramPacket(jpeg, jpeg.length);

			System.out.println("UDP socket up on port: " + udp_port);
			System.out.println("Waiting for images");
			
			while (true) {
				
				try { 
					server.receive(packet); 
					int len = packet.getLength();
					System.out.println("Received " + len + " bytes.");
					imagePanel.refresh(packet.getData());			
					packet.setLength(Axis211A.IMAGE_BUFFER_SIZE);
				} catch (IOException ex) {
					System.err.println(ex);
				} 
				if (firstCall) {
					this.pack();
					this.setVisible(true);
					firstCall = false;
				}
			} // end while

		}
	}



	class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		ImageIcon icon;
		public ImagePanel() {
			super();
			icon = new ImageIcon();
			JLabel label = new JLabel(icon);
			add(label, BorderLayout.CENTER);
			this.setSize(200, 200);
		}
		public void refresh(byte[] data) {
			Image theImage = getToolkit().createImage(data);
			getToolkit().prepareImage(theImage,-1,-1,null);	    
			icon.setImage(theImage);
			icon.paintIcon(this, this.getGraphics(), 5, 5);
		}
	}
	
}
