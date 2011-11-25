package se.axisandandroids.buffer;

import se.lth.cs.fakecamera.Axis211A;

public class FrameBuffer {

	private Frame[] buffer;	
	private final int MAXSIZE;	
	private final int FRAMESIZE;
	private int nextToPut = 0;	
	private int nextToGet = 0;
	private int nAvailable = 0;


	/**
	 * Create a FrameBuffer with: 
	 * maximum frame size, Axis211A.IMAGE_BUFFER_SIZE and:1
	 * @param MAXSIZE, maximum buffer capacity. 
	 **/
	public FrameBuffer(int MAXSIZE) {		
		this.MAXSIZE = MAXSIZE;
		this.FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;		
		init_buffer();
	}

	/**
	 * Create a FrameBuffer with: 
	 * @param FRAMESIZE, maximum frame size.
	 * @param MAXSIZE, maximum buffer capacity. 
	 **/
	public FrameBuffer(int MAXSIZE, int FRAMESIZE) {		
		this.MAXSIZE = MAXSIZE;
		this.FRAMESIZE = FRAMESIZE;
		init_buffer();
	}

	private void init_buffer() {
		buffer = new Frame[MAXSIZE];		
		for (int i = 0; i < MAXSIZE; ++i) {
			buffer[i] = new Frame(FRAMESIZE);
		}
	}

	/**
	 * Put a frame in the buffer, blocks until it is put there.
	 * @param x, frame data.
	 * @param len, length of data. Less than or equal to framesize.
	 */
	public synchronized void put(byte[] x, int len) {
		try {
			while (nAvailable == MAXSIZE) wait();
		} catch (InterruptedException e) {
			System.err.println("Put got interrupted");
			e.printStackTrace();
		}		
		//if (nAvailable == 0) notifyAll();
		
		//buffer[nextToPut].x = x;
		System.arraycopy(x, 0, buffer[nextToPut].x, 0, len);	
		buffer[nextToPut].len = len;
		if (++nextToPut == MAXSIZE) nextToPut = 0;
		++nAvailable;				
		
		notifyAll();
				
		//System.out.printf("Buffering Capacty At: %5.2f percent, %d Frames ... \n", 100*nAvailable/(double)MAXSIZE, nAvailable);
	}


	/**
	 * Tries to return the data of next frame from buffer, no blocking.
	 * @return a byte array with frame data. Note that all data in the 
	 * array belong to the frame. Return null if none available.
	 */
	public synchronized byte[] tryGet() {
		if (nAvailable == 0) {
			return null;
		}
		/* Return a copy with correct length. */
		byte[] data = new byte[buffer[nextToGet].len];
		System.arraycopy(buffer[nextToGet].x, 0, data, 0, buffer[nextToGet].len);		
		if (nAvailable == MAXSIZE) notifyAll();
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;
		return data;
	}

	/**
	 * Tries to return the next frame from buffer, no blocking.
	 * @return next frame if available, otherwise null.
	 */
	public synchronized Frame tryGetFrame() {
		if (nAvailable == 0) {
			return null;
		}
		/* Return copy of the frame. */
		Frame frame = new Frame(buffer[nextToGet]);
		if (nAvailable == MAXSIZE) notifyAll();
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;
		return frame;
	}


	/**
	 * Returns the data of next frame from buffer, blocks until available.
	 * @return a byte array with frame data. Note that all data in the 
	 * array belong to the frame.  
	 */
	public synchronized byte[] get() {
		try {
			while (nAvailable == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		/* Return a copy with correct length. */
		byte[] data = new byte[buffer[nextToGet].len];
		System.arraycopy(buffer[nextToGet].x, 0, data, 0, buffer[nextToGet].len);
		
		//if (nAvailable == MAXSIZE) notifyAll();
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;
		
		notifyAll();		
		return data;
	}
	
	/**
	 * Returns the data of next frame from buffer, blocks until available.
	 * @return a byte array with frame data. Note that all data in the 
	 * array belong to the frame.  
	 */
	public synchronized int get(byte[] jpeg) {
		try {
			while (nAvailable == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		/* Write data to jpeg. */
		int len = buffer[nextToGet].len;
		System.arraycopy(buffer[nextToGet].x, 0, jpeg, 0, len);		
		if (nAvailable == MAXSIZE) notifyAll();
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;		
		return len;
	}
	

	/**
	 * Returns the next frame from buffer, blocks until available.
	 * @return next frame.
	 */
	public synchronized Frame getFrame() {
		try {
			while (nAvailable == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		Frame f = new Frame(buffer[nextToGet]);
		if (nAvailable == MAXSIZE) notifyAll();
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;

		/* Return copy of the frame. */
		return f;
	}
	
	public synchronized void awaitFilled() {
		try {
			while (nAvailable < MAXSIZE) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		notifyAll();
	}
	
	public synchronized void awaitBuffered(long maxtime) {
		try {
			long t0 = System.currentTimeMillis();
			long t;
			while ((t = System.currentTimeMillis() - t0) < maxtime 
					&& nAvailable < MAXSIZE) {
				wait(t);
				System.out.printf("Buffering Capacty At: %5.2f percent, waited %d ms, ... \n", 100*nAvailable/(double)MAXSIZE, t);
			}
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		notifyAll();
	}
	

	/**
	 * Return frame data of the next frame in buffer without removing it.
	 * @return frame data.
	 */
	public synchronized final byte[] first() {
		if (nAvailable == 0) return null;
		return buffer[nextToGet].x;
	}

	/**
	 * Return the next frame in buffer without removing it.
	 * @return frame data.
	 */
	public synchronized final Frame firstFrame() {
		if (nAvailable == 0) return null;
		return buffer[nextToGet]; 
	}

	/**
	 * Sneak a peek for the frame data at index i in buffer.
	 * @param i, index of frame in buffer.
	 * @return frame data.
	 */
	public synchronized final byte[] sneakpeek(int i) {
		if (i >= nAvailable) {
			System.err.println("Out of bounds.");			
		} 
		return buffer[(nextToGet+i) % MAXSIZE].x; // Return copy ?
	}

	/**
	 * Sneak a peek for the frame at index i in buffer.
	 * @param i, index of frame in buffer.
	 * @return frame data.
	 */
	public synchronized final Frame sneakpeekFrame(int i) {
		if (i >= nAvailable) {
			System.err.println("Out of bounds.");			
		} 
		return buffer[(nextToGet+i) % MAXSIZE]; // Return copy ?
	}

	/**
	 * Print what's in the buffer.
	 */
	public synchronized void printBuffer() {
		System.out.println("** Print Buffer");
		for (int l = 0; l < nAvailable; ++l) {
			System.out.printf("%6d - %s\n", l, buffer[(nextToGet + l) % MAXSIZE].toString());
		}	
		System.out.println("");
	}	
			
	public static void main(String[] args) { /* Test Module */
		int BUFFMAX = 10;
		final FrameBuffer fb = new FrameBuffer(BUFFMAX);
		final byte[] raw_img = { 0,120,32,33,2,12,-23,32,-14,3 };

		for (int i = 0; i < BUFFMAX; ++i) {
			final byte[] x = new byte[raw_img.length];
			System.arraycopy(raw_img, 0, x, 0, BUFFMAX);
			x[0] = x[i] = (byte) i;			
			fb.put(x, x.length);
		}

		fb.printBuffer();

		Frame first = fb.firstFrame();
		System.out.println("First: " + first.toString());

		for (int i = 0; i < BUFFMAX; ++i) {
			Frame y = fb.sneakpeekFrame(i);
			System.out.println("Index: " + i + " - " + y.toString());
		}

		byte[] z;
		z = fb.get();
		assert(z[0] == 0);

		z = fb.get();
		assert(z[0] == 1);

		byte[] x = new byte[raw_img.length];
		System.arraycopy(raw_img, 0, x, 0, BUFFMAX);
		x[0] = (byte) 10;			
		fb.put(x, x.length);

		z = fb.get();
		assert(z[0] == 2);

		fb.printBuffer();

		for (int i = 3; i < BUFFMAX; ++i) {
			z = fb.get();
			assert(z[0] == i);
			assert(z[i] == i);			
		}

		fb.printBuffer();

		z = fb.get();
		assert(z[0] == 10);

		fb.printBuffer();	
	}
}