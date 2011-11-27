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
import android.view.View;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
	private static int id;
	private int myId;
	private Bitmap mBitmap;
	private ViewThread mThread;
	private Paint mPaintHUDText;
	private NewImageCallback mNewImageCallback;
	private String timeSinceLastImage;
	private int mHeight;
	private int mWidth;
	private static final int MAXSAMPLES = 1000;
	int tickindex = 0;
	long ticksum = 0;
	long[] ticklist = new long[MAXSAMPLES];

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
				mStartTime = System.currentTimeMillis();
			}
		};

		getHolder().addCallback(this);
		mThread = new ViewThread(this);
		mPaintHUDText = new Paint();
		mPaintHUDText.setColor(Color.WHITE);
		mPaintHUDText.setTextSize(20);
	}

	long mStartTime = System.currentTimeMillis();
	long mElapsedTime = System.currentTimeMillis();

	double calcAverageTick(long newtick) {
		ticksum -= ticklist[tickindex]; /* subtract the value falling off */
		ticksum += newtick; /* add new value */
		ticklist[tickindex] = newtick; /*
										 * save new value so it can be
										 * subtracted later
										 */
		if (++tickindex == MAXSAMPLES) /* inc buffer index */
			tickindex = 0;

		/* return average */
		return (double) ticksum / MAXSAMPLES;
	}

	public int getId() {
		return myId;
	}

	public NewImageCallback getNewImageCallback() {
		return mNewImageCallback;
	}

	public void doDraw(long elapsedTime, Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(mBitmap, 0, 0, null);
		doDrawHUD(elapsedTime, canvas);
	}

	private void doDrawHUD(long elapsedTime, Canvas canvas) {
		canvas.drawText("FPS: " + Math.round(1000f / elapsedTime), 20, 20,
				mPaintHUDText);
		mPaintHUDText.setColor(Color.argb(200, 255, 0, 0));
		this.mElapsedTime = System.currentTimeMillis() - this.mStartTime;

		canvas.drawText("Delay: " + calcAverageTick(mElapsedTime), 20, 35,
				mPaintHUDText);
		mPaintHUDText.setColor(Color.WHITE);
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mHeight = 800 / (id + 1);
		mWidth = 480;

		setMeasuredDimension(mWidth, mHeight);
		mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, false);
	}

	public void play() {
		mThread.setIsPlaying(true);
	}

	public void pause() {
		mThread.setIsPlaying(true);
	}
}

class ViewThread extends Thread {
	private Panel mPanel;
	private SurfaceHolder mHolder;
	private boolean mRun = false;
	private long mStartTime;
	private long mElapsedTime;
	private boolean mIsPlaying = true;

	public ViewThread(Panel panel) {
		mPanel = panel;
		mHolder = mPanel.getHolder();
	}

	public void setIsPlaying(boolean isPlaying) {
		mIsPlaying = isPlaying;
	}

	public void setRunning(boolean run) {
		mRun = run;
	}

	@Override
	public void run() {
		Canvas canvas = null;
		System.out.println("Running ViewThread");
		this.mStartTime = System.currentTimeMillis();
		while (mRun) {
			if (mIsPlaying) {
				canvas = mHolder.lockCanvas();
				if (canvas != null) {
					mPanel.doDraw(this.mElapsedTime, canvas);
					this.mElapsedTime = System.currentTimeMillis()
							- this.mStartTime;
					mHolder.unlockCanvasAndPost(canvas);
				}
				this.mStartTime = System.currentTimeMillis();
			}
		}
	}
}
