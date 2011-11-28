package se.axisandandroids.buffer;

/**
 * Bonus buffer :)
 * @author jg
 *
 */
public class LongCircularBuffer {

	private long[] buffer;	
	private final int MAXSIZE;	
	private int nextToPut = 0;	
	private int nextToGet = 0;
	private int nAvailable = 0;

	public LongCircularBuffer(int MAXSIZE) {		
		this.MAXSIZE = MAXSIZE;
		buffer = new long[MAXSIZE];
	}
	
	public synchronized long differenceFirstLast() {
		if (nAvailable == 0) return 0;
		return Math.abs(buffer[nextToGet] - buffer[(MAXSIZE+nextToPut-1) % MAXSIZE]);
	}

	public synchronized void put(long x) {
		try {
			while (nAvailable == MAXSIZE) wait();
		} catch (InterruptedException e) {
			System.err.println("Put got interrupted");
			e.printStackTrace();
		}
		//if (nAvailable == 0) notifyAll();
		buffer[nextToPut] = x;	
		if (++nextToPut == MAXSIZE) nextToPut = 0;
		++nAvailable;
		notifyAll();		
	}

	public synchronized void putOverwriting(long x) {
		if (nAvailable == MAXSIZE) {
			buffer[nextToPut] = x;	
			if (++nextToPut == MAXSIZE) nextToPut = 0;
			if (++nextToGet == MAXSIZE) nextToGet = 0;
		} else {		
			buffer[nextToPut] = x;	
			if (++nextToPut == MAXSIZE) nextToPut = 0;
			++nAvailable;
		}
		notifyAll();
	}

	public synchronized long get() {
		try {
			while (nAvailable == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		//if (nAvailable == MAXSIZE) notifyAll();
		long ret = buffer[nextToGet];
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;
		notifyAll();
		
		return ret;
	}

	public synchronized long tryGet() {
		if (nAvailable == 0) return Long.MIN_VALUE;
//		if (nAvailable == MAXSIZE) notifyAll();
		long ret = buffer[nextToGet];
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;		
		notifyAll();
		return ret;
	}

	public synchronized long first() {
		if (nAvailable == 0) return Long.MIN_VALUE;
		return buffer[nextToGet];
	}

	public synchronized long sneakpeek(int i) {
		if (i >= nAvailable) {
			System.err.println("Out of bounds.");			
		} 
		return buffer[(nextToGet+i) % MAXSIZE];
	}

	public synchronized void printBuffer() {
		System.out.println("** Print Buffer");
		for (int l = 0; l < nAvailable; ++l) {
			System.out.printf("%6d - %d\n", l, buffer[(nextToGet + l) % MAXSIZE]);
		}	
		System.out.println("");
	}	
	
	public synchronized void flush() {		
		nAvailable = 0;
		notifyAll();
	}

	public static void main(String[] args) {
		int BUFFMAX = 10;
		LongCircularBuffer fb = new LongCircularBuffer(BUFFMAX);

		for (int i = 0; i < BUFFMAX; ++i) {			
			fb.put(i);
		}

		fb.printBuffer();

		long first = fb.first();
		System.out.println("First: " + first);

		for (int i = 0; i < BUFFMAX; ++i) {
			long y = fb.sneakpeek(i);
			System.out.println("Index: " + i + " - " + y);
		}

		Long z;
		z = fb.get();
		assert(z == 0);

		z = fb.get();
		assert(z == 1);

		z = fb.get();
		assert(z == 2);

		fb.printBuffer();

		for (int i = 3; i < BUFFMAX; ++i) {
			z = fb.get();
			assert(z == i);
		}

		fb.put(BUFFMAX);	
		fb.printBuffer();

		z = fb.get();
		assert(z == 10);

		fb.printBuffer();	
	}
}