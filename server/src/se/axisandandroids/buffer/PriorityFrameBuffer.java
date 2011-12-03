package se.axisandandroids.buffer;

import java.util.LinkedList;
import java.util.PriorityQueue;

import se.lth.cs.fakecamera.Axis211A;

/**
 * FrameBuffer, with priorities. Reusing Frame objects with a recycle list.
 * @author jg
 */
public class PriorityFrameBuffer {
	
	private final PriorityQueue<Frame> 	buffer;
	private final LinkedList<Frame> 	recycler;		
	private final int 					MAXSIZE;	
	private final int 					FRAMESIZE;


	/**
	 * Create a FrameBuffer with: 
	 * maximum frame size, Axis211A.IMAGE_BUFFER_SIZE and:1
	 * @param MAXSIZE, maximum buffer capacity. 
	 **/
	public PriorityFrameBuffer(int MAXSIZE) {		
		this.MAXSIZE = MAXSIZE;
		this.FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;		
		buffer = new PriorityQueue<Frame>(MAXSIZE);		
		recycler = new LinkedList<Frame>();
		for (int i = 0; i < MAXSIZE; ++i) {
			recycler.add(new Frame(FRAMESIZE));
		}
	}

	/**
	 * Create a FrameBuffer with: 
	 * @param FRAMESIZE, maximum frame size.
	 * @param MAXSIZE, maximum buffer capacity. 
	 **/
	public PriorityFrameBuffer(int MAXSIZE, int FRAMESIZE) {		
		this.MAXSIZE = MAXSIZE;
		this.FRAMESIZE = FRAMESIZE;
		buffer = new PriorityQueue<Frame>(MAXSIZE);	
		recycler = new LinkedList<Frame>();
		for (int i = 0; i < MAXSIZE; ++i) {
			recycler.add(new Frame(FRAMESIZE));
		}
	}

	public synchronized int nAvailable() {
		return buffer.size();
	}

	
	/**
	 * Put a frame in the buffer, blocks until it is put there.
	 * @param x, frame data.
	 * @param len, length of data. Less than or equal to framesize.
	 */
	public synchronized void put(byte[] x, int len) {
		try {
			//while (buffer.size() >= MAXSIZE) wait();
			while (recycler.isEmpty()) wait();
		} catch (InterruptedException e) {
			System.err.println("Put got interrupted");
			e.printStackTrace();
		}		
		Frame thenewframe = recycler.poll();		
		System.arraycopy(x, 0, thenewframe.x, 0, len);			
		thenewframe.len = len;
		buffer.offer(thenewframe);						
		notifyAll();
	}


	/**
	 * Tries to return the data of next frame from buffer, no blocking.
	 * @return a byte array with frame data. Note that all data in the 
	 * array belong to the frame. Return null if none available.
	 */
	public synchronized byte[] tryGet() {
		if (buffer.size() == 0) {
			return null;
		}
		/* Return a copy with correct length. */
		Frame thereturnframe = buffer.poll();		
		byte[] data = new byte[thereturnframe.len]; 
		System.arraycopy(thereturnframe.x, 0, data, 0, thereturnframe.len);		
		recycler.offer(thereturnframe);		
		notifyAll();
		return data;
	}

	/**
	 * Tries to return the next frame from buffer, no blocking.
	 * @return next frame if available, otherwise null.
	 */
	public synchronized Frame tryGetFrame() {
		if (buffer.size() == 0) {
			return null;
		}
		/* Return copy of the frame. */
		Frame frame = new Frame(buffer.peek());				
		recycler.offer(buffer.poll());		
		notifyAll();
		return frame;
	}


	/**
	 * Returns the data of next frame from buffer, blocks until available.
	 * @return a byte array with frame data. Note that all data in the 
	 * array belong to the frame.  
	 */
	public synchronized byte[] get() {
		try {
			while (buffer.size() == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		/* Return a copy with correct length. */
		Frame thereturnframe = buffer.poll();
		byte[] data = new byte[thereturnframe.len];
		System.arraycopy(thereturnframe.x, 0, data, 0, thereturnframe.len);
		recycler.offer(thereturnframe);		
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
			while (buffer.size() == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		
		Frame frametoget = buffer.poll();
		/* Write data to jpeg. */		
		int len = frametoget.len;
		System.arraycopy(frametoget.x, 0, jpeg, 0, len);		
		recycler.offer(frametoget);
		notifyAll();
		return len;
	}


	/**
	 * Returns the next frame from buffer, blocks until available.
	 * @return next frame.
	 */
	public synchronized Frame getFrame() {
		try {
			while (buffer.size() == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		Frame frametoget = buffer.poll();
		recycler.offer(frametoget);
		notifyAll();
		/* Return copy of the frame. */
		return new Frame(frametoget);
	}

	/**
	 * Wait for the buffer to be filled.
	 */
	public synchronized void awaitFilled() {
		try {
			while (buffer.size() < MAXSIZE) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		notifyAll();
	}

	/**
	 * Wait for buffer to be filled, abandon wait when time maxtime has passed.
	 * @param maxtime, max wait time in milliseconds.
	 */
	public synchronized void awaitBuffered(long maxtime) {
		try {
			long t0 = System.currentTimeMillis();
			long t;
			while ((t = System.currentTimeMillis() - t0) < maxtime 
					&& buffer.size() < MAXSIZE) {
				wait(t);
				System.out.printf("Buffering Capacty At: %5.2f percent, waited %d ms, ... \n", 100*buffer.size()/(double)MAXSIZE, t);
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
		if (buffer.size() == 0) return null;
		return buffer.peek().x;
	}

	/**
	 * Return the next frame in buffer without removing it.
	 * @return frame data.
	 */
	public synchronized final Frame firstFrame() {
		if (buffer.size() == 0) return null;
		return buffer.peek(); 
	}

	/**
	 * Print what's in the buffer.
	 */
	public synchronized void printBuffer() {				
		System.out.println("** Print Buffer");
		int l = 0;
		for (Frame f : buffer) {
			System.out.printf("%6d - %s\n", l++, f.toString());
		}	
		System.out.println("");
	}	

	public synchronized void flush() {
		while (! buffer.isEmpty()) {
			recycler.offer(buffer.poll());
		}
		notifyAll();
	}

	
	public static void main(String[] args) { /* Test Module */
		final byte[] raw_img = { 0,120,32,33,2,12,-23,32,-14,3 };

		int BUFFMAX = 10;
		final PriorityFrameBuffer fb = new PriorityFrameBuffer(BUFFMAX, raw_img.length);

		System.out.println("Put/Get test ------------");
		for (int i = BUFFMAX-1; i >= 0; --i) {
			final byte[] x = new byte[raw_img.length];
			System.arraycopy(raw_img, 0, x, 0, BUFFMAX);
			x[0] = x[i] = (byte) i;			
			fb.put(x, x.length);
		}		
		for (int i = 0; i < BUFFMAX; ++i) {
			Frame f = fb.getFrame();
			System.out.printf("%6d - %s\n", i, f.toString());
			assert(f.x[0] == f.x[i] && f.x[0] == i);
		}	
		System.out.println("DONE: Put/Get test ------------");

		// OTHER OLD TESTS...		
		for (int i = 0; i < BUFFMAX; ++i) {
			final byte[] x = new byte[raw_img.length];
			System.arraycopy(raw_img, 0, x, 0, BUFFMAX);
			x[0] = x[i] = (byte) i;			
			fb.put(x, x.length);
		}
		
		fb.printBuffer();

		Frame first = fb.firstFrame();
		System.out.println("First: " + first.toString());		

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

