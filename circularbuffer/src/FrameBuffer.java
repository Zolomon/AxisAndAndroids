import se.lth.cs.fakecamera.Axis211A;



public class FrameBuffer {

	private Frame[] buffer;	
	private final int MAXSIZE;	
	private final int FRAMESIZE;
	private int nextToPut = 0;	
	private int nextToGet = 0;
	private int nAvailable = 0;

	
	public FrameBuffer(int MAXSIZE) {		
		this.MAXSIZE = MAXSIZE;
		this.FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;		
		init_buffer();
	}
	
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

	public synchronized void put(byte[] x) {
		try {
			while (nAvailable == MAXSIZE) wait();
		} catch (InterruptedException e) {
			System.err.println("Put got interrupted");
			e.printStackTrace();
		}
		buffer[nextToPut].x = x;		
		if (++nextToPut == MAXSIZE) nextToPut = 0;
		++nAvailable;
		notifyAll();
	}

	public synchronized byte[] get() {
		try {
			while (nAvailable == 0) wait();
		} catch (InterruptedException e) {
			System.err.println("Get got interrupted");
			e.printStackTrace();
		}
		if (++nextToGet == MAXSIZE) nextToGet = 0;
		--nAvailable;
		notifyAll();
		return buffer[nextToGet].x;
	}

	public synchronized byte[] first() {
		if (nAvailable == 0) return null;
		return buffer[nextToGet].x;
	}

	public synchronized byte[] sneakpeek(int i) {
		if (i >= nAvailable) {
			System.err.println("Out of bounds.");			
		} 
		return buffer[(nextToGet+i) % MAXSIZE].x;
	}

	public synchronized void printBuffer() {
		System.out.println("** Print Buffer");
		for (int l = 0; l < nAvailable; ++l) {
			System.out.printf("%6d - %s\n", l, buffer[(nextToGet + l) % MAXSIZE].toString());
		}	
		System.out.println("");
	}	
	
	
	/* Test Module */		
	public static void main(String[] args) {
		int BUFFMAX = 10;
		FrameBuffer fb = new FrameBuffer(BUFFMAX);
		byte[] raw_img = { 0,120,32,33,2,12,-23,32,-14,3 };
				
		for (int i = 0; i < BUFFMAX; ++i) {
			byte[] x = new byte[raw_img.length];
			System.arraycopy(raw_img, 0, x, 0, BUFFMAX);
			x[0] = x[i] = (byte) i;			
			fb.put(x);
		}

		fb.printBuffer();
		
		byte[] first = fb.first();
		System.out.println("First: " + first.toString());
		
		for (int i = 0; i < BUFFMAX; ++i) {
			byte[] y = fb.sneakpeek(i);
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
		fb.put(x);

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