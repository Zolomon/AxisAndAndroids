package se.axisandandroids.client.display;

import se.axisandandroids.client.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
	private static int id;
	private int myId;
	private Bitmap mBitmap;
	private ViewThread mThread;
	private Paint mPaintHUDText;
	private NewImageCallback mNewImageCallback;
	private String timeSinceLastImage;

	public Panel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Panel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Panel(Context context) {
		super(context);
		init();
	}

	private void init() {
		myId = Panel.id++;
		mBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		
		mNewImageCallback = new NewImageCallback() {
			
			public void newImage(Bitmap bitmap) {
				mBitmap = bitmap;
			}
		};
		
		getHolder().addCallback(this);
		mThread = new ViewThread(this);
		mPaintHUDText = new Paint();
		mPaintHUDText.setColor(Color.WHITE);
		mPaintHUDText.setTextSize(20);
	}
	
	public int getId() {
		return myId;
	}
	
	public NewImageCallback getNewImageCallback() {
		return mNewImageCallback;
	}
	
	public synchronized void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	public void doDraw(long elapsedTime, Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(mBitmap, 10, 10, null);
		
		doDrawHUD(elapsedTime, canvas);
	}
	
	private void doDrawHUD(long elapsedTime, Canvas canvas) {
		canvas.drawText("FPS: " + Math.round(1000f / elapsedTime), 20, 20, mPaintHUDText);
		canvas.drawText("Delay: " + timeSinceLastImage, 20, 30, mPaintHUDText);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	public void surfaceCreated(SurfaceHolder arg0) {
		if (!mThread.isAlive()) {
			mThread = new ViewThread(this);
			mThread.setRunning(true);
			mThread.start();
		}
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (mThread.isAlive()) {
			mThread.setRunning(false);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Do stuff here when touched.
		return super.onTouchEvent(event);
	}
}

class ViewThread extends Thread {
	private Panel mPanel;
	private SurfaceHolder mHolder;
	private boolean mRun = false;
	private long mStartTime;
	private long mElapsedTime;
	
	public ViewThread(Panel panel) {
		mPanel = panel;
		mHolder = mPanel.getHolder();
	}

	public void setRunning(boolean run) {
		mRun = run;
	}

	@Override
	public void run() {
	    Canvas canvas = null;
	    mStartTime = System.currentTimeMillis();
	    while (mRun) {
	        canvas = mHolder.lockCanvas();
	        if (canvas != null) {
	            mPanel.doDraw(mElapsedTime, canvas);
	            mElapsedTime = System.currentTimeMillis() - mStartTime;
	            mHolder.unlockCanvasAndPost(canvas);
	        }
	        mStartTime = System.currentTimeMillis();
	    }
	}
}
