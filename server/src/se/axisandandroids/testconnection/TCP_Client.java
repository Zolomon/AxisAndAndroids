package se.axisandandroids.testconnection;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.lth.cs.fakecamera.Axis211A;

public class TCP_Client {


	private Socket socket;
	private InetAddress host;	
	private final static int default_port = 5555;
	private int port;

	public TCP_Client(InetAddress host, int port) {
		this.host = host;
		this.port = port;
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

		TCP_Client tcpclient = new TCP_Client(addr, port);

		try {


			// *** --- CHANGE HERE WHAT TO RUN --- ***


			int testcase = 3;

			switch (testcase) {
			case 0: 
				tcpclient.testSendInt();
				break;
			case 1: 
				tcpclient.connection_test();
				break;
			case 2:
				tcpclient.testFakeCam();
				break;
			case 3:
				tcpclient.testFakeCamInteractive();
				break;
			default:
				tcpclient.connection_test();
				tcpclient.testFakeCam();
				tcpclient.testFakeCamInteractive();			
			}

			// *** ------------------------------- ***

			//tcpclient.disconnect();
		} catch (IOException e) {
			System.err.println("io-exception");
			System.exit(1);
		}
	}


	public void connection_test() throws IOException {		
		System.out.println("Connection Test");

		Connection con = new Connection(socket);

		// Test sendInt()
		System.out.println("\n** Sending int...");
		int nbr = 983745;
		System.out.printf("Sending int: %d\n", nbr);
		con.sendInt(nbr);

		// Test recvInt()
		System.out.println("\n** Receiving int...");
		nbr = con.recvInt();
		System.out.printf("Got int: %d\n", nbr);	

		// Test sendSyncMode
		System.out.println("\n** Sending SyncMode...");
		con.sendSyncMode(Protocol.SYNC_MODE.AUTO);

		// Test sendDisplayMode
		System.out.println("\n** Sending DisplayMode...");
		con.sendDisplayMode(Protocol.DISP_MODE.AUTO);

		// Test recvImage
		byte[] c = { 12,43,34,120,21,32,100,34 };			
		System.out.println("\n** Receiving Image...");
		int cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		System.out.println("Command: " + cmd);			
		byte[] b = new byte[Axis211A.IMAGE_BUFFER_SIZE];;
		int len = con.recvImage(b);		
		System.out.printf("Length: %d\n", len);
		for (int i = 0; i < len; ++i) {
			System.out.printf("%d ", b[i]);
			assert(b[i] == c[i]);
		}
		System.out.println();

		// Test sendImage
		System.out.println("\n** Sending Image...");
		con.sendImage(c,0,c.length);
	}

	public void testSendInt() {
		System.out.println("**Test send int");

		Connection con = new Connection(socket);
		try {
			for (int i = -1000; i < 1000; ++i) {
				con.sendInt(127*i);			
				System.out.printf("Sent: %d\n",  127*i);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public void testFakeCam() throws IOException {
		System.out.println("**Test FakeCam");

		Connection con = new Connection(socket);


		// Get image 1
		int cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		byte[] img = new byte[Axis211A.IMAGE_BUFFER_SIZE];
		int len = con.recvImage(img);	
		System.out.println("Received " + len + " bytes.");

		jframe_show_jpeg("FakeCamSplatt 1", img);

		// Get image 2
		cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		len = con.recvImage(img);
		System.out.println("Received " + len + " bytes.");

		jframe_show_jpeg("FakeCamSplatt 2", img);

		// Get image 3		
		cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		len = con.recvImage(img);
		System.out.println("Received " + len + " bytes.");

		jframe_show_jpeg("FakeCamSplatt 3", img);

		// Get image 4
		cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		len = con.recvImage(img);
		System.out.println("Received " + len + " bytes.");

		jframe_show_jpeg("FakeCamSplatt 4", img);
	}

	public void jframe_show_jpeg(String framename, byte[] data) {
		JFrame f = new JFrame(framename); 		
		ImageIcon img = new javax.swing.ImageIcon(data,"jpeg frame");		
		f.getContentPane().add(new javax.swing.JLabel(img)); 				
		f.setSize(img.getIconWidth(),img.getIconHeight()); 
		f.setVisible(true); 
	}



	public void testFakeCamInteractive() {
		new GUI();
	}		

	class GUI extends JFrame {
		private static final long serialVersionUID = 1L;
		ImagePanel imagePanel;
		JButton button;
		boolean firstCall = true;
		boolean play = true;
		Connection con;
		byte [] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

		public GUI() {
			super();						
			imagePanel = new ImagePanel();
			button = new JButton("Stop");
			button.addActionListener(new ButtonHandler(this));
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(imagePanel, BorderLayout.NORTH);
			this.getContentPane().add(button, BorderLayout.SOUTH);
			this.setLocationRelativeTo(null);
			this.pack();

			this.con = new Connection(socket);
						
			refreshImage();
		}

		public void refreshImage() {		
			//			System.out.println("** Refreshing Image ------------------------- ");			
			while (play) {
				int cmd, len = 0;

				try {			
					//				System.out.println("Requesting Image");
					con.sendInt(Protocol.COMMAND.IMAGE); // Request Image	

					//				System.out.println("Waiting for Answer");
					cmd = con.recvInt(); 				 // Wait for Answer

					if (cmd == Protocol.COMMAND.IMAGE) {
						//					System.out.println("Getting Image...");
						len = con.recvImage(jpeg);	
					} else if (cmd == Protocol.COMMAND.NOTOK) {
						System.err.println("Server says not ok!");
						System.exit(1);
					} else {
						System.err.println("Protocol Voilation!");
						System.exit(1);
					}				
				} catch (IOException e) {
					System.err.println("Errornous err...");
					e.printStackTrace();
					System.exit(1);
				}			
				System.out.println("Received " + len + " bytes.");

				imagePanel.refresh(jpeg);			
				if (firstCall) {
					this.pack();
					this.setVisible(true);
					firstCall = false;
				}
			}
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
	class ButtonHandler implements ActionListener {
		GUI gui;

		public ButtonHandler(GUI gui) {
			this.gui = gui;
		}
		public void actionPerformed(ActionEvent evt) {
			if (gui.play) gui.play = false;
			else gui.play = true;
		}
	}
}
