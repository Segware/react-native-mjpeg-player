package com.mjpegplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.widget.LinearLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;
import com.github.niqdev.mjpeg.MjpegView;

public class MjpegPlayerView extends LinearLayout {

    private static final int TIMEOUT = 5;
    private Context context;
    MjpegView mjpegView;
    private Uri videoUri;
    private String date = "";
    private String time = "";
    private int frameWidth;
    private int frameHeight;
    private Boolean isThreadRunning = false;

    public MjpegPlayerView(Context context) {
        super(context);
        this.context = context;
        mjpegView = (MjpegSurfaceView) findViewById(R.id.mjpegViewDefault);
    }


    public void setVideoURI(Uri uri) {
        this.videoUri = uri;
    }

    private DisplayMode calculateDisplayMode() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE ?
                DisplayMode.FULLSCREEN : DisplayMode.BEST_FIT;
    }

    public void start() {

        Mjpeg.newInstance()
                .open(this.videoUri.toString(), TIMEOUT)
                .subscribe(
                        inputStream -> {
                            mjpegView.setSource(inputStream);
                            mjpegView.setDisplayMode(calculateDisplayMode());
                            mjpegView.flipHorizontal(false);
                            mjpegView.flipVertical(false);
                            mjpegView.showFps(false);
                        },
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                            WritableMap event = Arguments.createMap();
                            event.putString("mjpeg_error", throwable.toString());
                            ReactContext reactContext = (ReactContext)getContext();
                            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                                    getId(),
                                    "mjpegError",
                                    event
                            );
                        });

        inflate(context, R.layout.layout, this);
    }

    public void stop(){
        new Thread(new Runnable() {
            public void run() {
                mjpegView.stopPlayback();
                isThreadRunning = false;
            }
        }).start();
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }
}
