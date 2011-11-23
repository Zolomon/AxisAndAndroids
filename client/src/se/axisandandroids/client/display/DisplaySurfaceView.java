package se.axisandandroids.client.display;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
 
 
public class DisplaySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private SurfaceViewThread surfaceViewThread;
    private boolean hasSurface;
 
    public DisplaySurfaceView(Context context) {
        super(context);
        init();
    }
 
    // Implementing all callback methods below
    public void resume() {
        // Create and start the graphics update thread
        if (surfaceViewThread == null) {
            surfaceViewThread = new SurfaceViewThread();
            if (hasSurface)
                surfaceViewThread.start();
        }
    }
 
    public void pause() {
        // Stop the graphics update thread
        if (surfaceViewThread != null) {
            surfaceViewThread.requestExitAndWait();
            surfaceViewThread = null;
        }
    }
 
    public void surfaceCreated(SurfaceHolder holder) {
        hasSurface = true;
 
        if (surfaceViewThread != null)
            surfaceViewThread.start();
    }
 
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        pause();
    }
 
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (surfaceViewThread != null)
            surfaceViewThread.onWindowResize(w, h);
    }
 
    private void init() {
        // Create a new SurfaceHolder and assign this class as its callback
        holder = getHolder();
        holder.addCallback(this);
        hasSurface = false;
    }
 
    private final class SurfaceViewThread extends Thread {
        private boolean done;
 
        SurfaceViewThread() {
            super();
            done = false;
        }
 
        @Override
        public void run() {
            SurfaceHolder surfaceHolder = holder;
 
            // Repeat the drawing loop until the thread is stopped
            while (!done) {
                // Lock the surface and return the canvas to draw onto
                Canvas canvas = surfaceHolder.lockCanvas();
 
                // TODO: Perform some Draws on the Canvas. Whatever you want!
                // Unlock the canvas and render the result
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
 
        public void requestExitAndWait() {
            // Mark this thread as complete and wait it to finish himself
            done = true;
            try {
                join();
            } catch (InterruptedException ignored) {
            }
        }
 
        public void onWindowResize(int w, int h) {
            // Deal with a change in the new surface size
        }
    }
}