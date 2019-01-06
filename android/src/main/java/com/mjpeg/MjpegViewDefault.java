package com.mjpeg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/*
 * I don't really understand and want to know what the hell it does!
 * Maybe one day I will refactor it ;-)
 * <p/>
 * https://code.google.com/archive/p/android-camera-axis
 */
public class MjpegViewDefault extends AbstractMjpegView {
    private static final String TAG = MjpegViewDefault.class.getSimpleName();

    private SurfaceHolder.Callback mSurfaceHolderCallback;
    private SurfaceView mSurfaceView;
    private boolean transparentBackground;
    private MjpegViewThread thread;
    private MjpegInputStreamDefault mIn = null;
    private boolean showFps = false;
    private boolean flipHorizontal = false;
    private boolean flipVertical = false;
    private volatile boolean mRun = false;
    private volatile boolean surfaceDone = false;
    private Paint overlayPaint;
    private int overlayTextColor;
    private int overlayBackgroundColor;
    private int backgroundColor;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private int displayMode;
    private int frameWidth;
    private int frameHeight;
    private boolean hasError = false;

    private OnFrameCapturedListener onFrameCapturedListener;

    // no more accessible
    class MjpegViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private long start;
        private Bitmap ovl;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        private String date = "";
        private String time = "";

        // no more accessible
        MjpegViewThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        private Rect destRect(int bmw, int bmh) {

            int tempx;
            int tempy;
            if (displayMode == MjpegViewDefault.SIZE_STANDARD) {
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegViewDefault.SIZE_BEST_FIT) {
                float bmasp = (float) bmw / (float) bmh;
                bmw = dispWidth;
                bmh = (int) (dispWidth / bmasp);
                if (bmh > dispHeight) {
                    bmh = dispHeight;
                    bmw = (int) (dispHeight * bmasp);
                }
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegViewDefault.SIZE_FULLSCREEN)
                return new Rect(0, 0, dispWidth, dispHeight);
            return null;
        }

        // no more accessible
        void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }

        private Bitmap makeFpsOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth = b.width() + 2;
            int bheight = b.height() + 2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(overlayTextColor);
            c.drawText(text, -b.left + 1,
                    (bheight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
            return bm;
        }

        public void run() {
            start = System.currentTimeMillis();
            PorterDuffXfermode mode = new PorterDuffXfermode(
                    PorterDuff.Mode.DST_OVER);
            Bitmap bm;
            int width;
            int height;
            Rect destRect;
            Canvas c = null;
            Paint p = new Paint();
            String fps = "";
            while (mRun) {
                if (surfaceDone && !hasError) {
                    try {
                        hasError = false;
                        c = mSurfaceHolder.lockCanvas();

                        if (c == null) {
                            Log.w(TAG, "null canvas, skipping render");
                            continue;
                        }else {
                            synchronized (mSurfaceHolder) {
                                try {
                                    if (flipHorizontal || flipVertical) {
                                        bm = flip(mIn.readMjpegFrame(frameWidth, frameHeight));
                                    } else {
                                        bm = mIn.readMjpegFrame(frameWidth, frameHeight);
                                        if (mIn != null && mIn.getDate() != null && !mIn.getDate().equals("") && mIn.getTime() != null && !mIn.getTime().equals("")) {
                                            setDate(mIn.getDate());
                                            setTime(mIn.getTime());
                                        }
                                    }
                                    _frameCaptured(bm);
                                    destRect = destRect(bm.getWidth(),
                                            bm.getHeight());

                                    if (transparentBackground) {
                                        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                                    } else {
                                        c.drawColor(backgroundColor);
                                    }

                                    c.drawBitmap(bm, null, destRect, p);

                                    if (showFps) {
                                        p.setXfermode(mode);
                                        if (ovl != null) {
                                            height = ((ovlPos & 1) == 1) ? destRect.top
                                                    : destRect.bottom
                                                    - ovl.getHeight();
                                            width = ((ovlPos & 8) == 8) ? destRect.left
                                                    : destRect.right
                                                    - ovl.getWidth();
                                            c.drawBitmap(ovl, width, height, null);
                                        }
                                        p.setXfermode(null);
                                        frameCounter++;
                                        if ((System.currentTimeMillis() - start) >= 1000) {
                                            fps = String.valueOf(frameCounter)
                                                    + "fps";
                                            frameCounter = 0;
                                            start = System.currentTimeMillis();
                                            ovl = makeFpsOverlay(overlayPaint, fps);
                                        }
                                    }
                                } catch (IOException e) {
                                    Log.e(TAG, "encountered exception during render", e);
                                    hasError = true;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Thread error", e);
                    } finally {
                        try {
                            if (c != null && mSurfaceHolder != null) {
                                mSurfaceHolder.unlockCanvasAndPost(c);
                            } else {
                                Log.w(TAG, "couldn't unlock surface canvas");
                            }
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "mSurfaceHolder error!!", e);
                        }

                    }
                }
            }
        }
    }

    Bitmap flip(Bitmap src) {
        Matrix m = new Matrix();
        float sx = flipHorizontal ? -1 : 1;
        float sy = flipVertical ? -1 : 1;
        m.preScale(sx, sy);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    private void init() {
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(mSurfaceHolderCallback);
        thread = new MjpegViewThread(holder);
        mSurfaceView.setFocusable(true);
        overlayPaint = new Paint();
        overlayPaint.setTextAlign(Paint.Align.LEFT);
        overlayPaint.setTextSize(12);
        overlayPaint.setTypeface(Typeface.DEFAULT);
        overlayTextColor = Color.WHITE;
        overlayBackgroundColor = Color.BLACK;
        backgroundColor = Color.BLACK;
        ovlPos = MjpegViewDefault.POSITION_LOWER_RIGHT;
        displayMode = MjpegViewDefault.SIZE_STANDARD;
        dispWidth = mSurfaceView.getWidth();
        dispHeight = mSurfaceView.getHeight();
    }

    /* all methods/constructors below are no more accessible */

    void _startPlayback() {
        if (mIn != null && !mRun) {
            mRun = true;
            mSurfaceView.destroyDrawingCache();
            thread.start();
        }
    }

    /*
     * @see https://github.com/niqdev/ipcam-view/issues/14
     */
    synchronized void _stopPlayback() {
        mRun = false;
        boolean retry = true;
        while (retry) {
            try {
                // make sure the thread is not null
                if (thread != null) {
                    thread.join(500);
                }
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "error stopping playback thread", e);
            }
        }

        // close the connection
        if (mIn != null) {
            try {
                mIn.close();
            } catch (IOException e) {
                Log.e(TAG, "error closing input stream", e);
            }
            mIn = null;
        }
    }

    void _surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
        if (thread != null) {
            thread.setSurfaceSize(w, h);
        }
    }

    void _surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        new Thread(new Runnable() {
            public void run() {
                _stopPlayback();
            }
        }).start();
        if (thread != null) {
            thread = null;
        }
    }

    void _frameCaptured(Bitmap bitmap) {
        if (onFrameCapturedListener != null) {
            onFrameCapturedListener.onFrameCaptured(bitmap);
        }
    }

    MjpegViewDefault(SurfaceView surfaceView, SurfaceHolder.Callback callback, boolean transparentBackground) {
        this.mSurfaceView = surfaceView;
        this.mSurfaceHolderCallback = callback;
        this.transparentBackground = transparentBackground;
        init();
    }

    void _surfaceCreated(SurfaceHolder holder) {
        surfaceDone = true;
    }

    void _showFps(boolean b) {
        showFps = b;
    }

    void _flipHorizontal(boolean b) {
        flipHorizontal = b;
    }

    void _flipVertical(boolean b) {
        flipVertical = b;
    }

    /*
     * @see https://github.com/niqdev/ipcam-view/issues/14
     */
    void _setSource(MjpegInputStreamDefault source, int frameWidth, int frameHeight) {
        Log.e("Setting source", "<<<<<<<<<<<<<<<<<<<<<<<");
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.hasError = false;
        mIn = source;
        _startPlayback();
    }

    void _setOverlayPaint(Paint p) {
        overlayPaint = p;
    }

    void _setOverlayTextColor(int c) {
        overlayTextColor = c;
    }

    void _setOverlayBackgroundColor(int c) {
        overlayBackgroundColor = c;
    }

    void _setOverlayPosition(int p) {
        ovlPos = p;
    }

    void _setDisplayMode(int s) {
        displayMode = s;
    }

    /* override methods */

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
        _surfaceCreated(holder);
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        _surfaceChanged(holder, format, width, height);
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
        _surfaceDestroyed(holder);
    }

    @Override
    public void setSource(MjpegInputStream stream, int frameWidth, int frameHeight ) {
        if (!(stream instanceof MjpegInputStreamDefault)) {
            throw new IllegalArgumentException("stream must be an instance of MjpegInputStreamDefault");
        }
        _setSource((MjpegInputStreamDefault) stream, frameWidth, frameHeight);
    }

    @Override
    public void setDisplayMode(DisplayMode mode) {
        _setDisplayMode(mode.getValue());
    }

    @Override
    public void showFps(boolean show) {
        _showFps(show);
    }

    @Override
    public void flipSource(boolean flip) {
        _flipHorizontal(flip);
    }

    @Override
    public void flipHorizontal(boolean flip) {
        _flipHorizontal(flip);
    }

    @Override
    public void flipVertical(boolean flip) {
        _flipVertical(flip);
    }

    @Override
    public void stopPlayback() {
        _stopPlayback();
    }

    @Override
    public boolean isStreaming() {
        return mRun;
    }

    @Override
    public void setResolution(int width, int height) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void freeCameraMemory() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setOnFrameCapturedListener(OnFrameCapturedListener onFrameCapturedListener) {
        this.onFrameCapturedListener = onFrameCapturedListener;
    }

    @Override
    public void setCustomBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void setFpsOverlayBackgroundColor(int overlayBackgroundColor) {
        this.overlayBackgroundColor = overlayBackgroundColor;
    }

    @Override
    public void setFpsOverlayTextColor(int overlayTextColor) {
        this.overlayTextColor = overlayTextColor;
    }

    @Override
    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    @Override
    public void resetTransparentBackground() {
        mSurfaceView.setZOrderOnTop(false);
        mSurfaceView.getHolder().setFormat(PixelFormat.OPAQUE);
    }

    @Override
    public void setTransparentBackground() {
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void clearStream() {
        Canvas c = null;

        try {
            c = mSurfaceView.getHolder().lockCanvas();
            c.drawColor(0, PorterDuff.Mode.CLEAR);
        } finally {
            if (c != null) {
                mSurfaceView.getHolder().unlockCanvasAndPost(c);
            } else {
                Log.w(TAG, "couldn't unlock surface canvas");
            }
        }
    }

    @Override
    public String getDate() {
        if (thread != null && thread.getDate() != null) {
            return thread.getDate();
        }
        return "";
    }

    @Override
    public String getTime() {
        if (thread != null && thread.getTime() != null) {
            return thread.getTime();
        }
        return "";
    }


}

