
package com.segware;

import android.net.Uri;
import android.view.View;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class RNMjpegplayerManager extends SimpleViewManager<View> {
    public static final String REACT_CLASS = "MjpegPlayer";
    private ThemedReactContext mContext;
    private MjpegPlayerView mjpegPlayerView;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public MjpegPlayerView createViewInstance(ThemedReactContext context){
        mContext = context;
        mjpegPlayerView = new MjpegPlayerView(context);
        return mjpegPlayerView;
    }

    @ReactProp(name = "settings")
    public void setVideoPath(MjpegPlayerView mjpegPlayerView, ReadableMap settings) {
        String url = settings.getString("url");
        int width = settings.getInt("width");
        int height = settings.getInt("height");
        Uri uri = Uri.parse(url);
        mjpegPlayerView.setFrameWidth(width);
        mjpegPlayerView.setFrameHeight(height);
        mjpegPlayerView.setVideoURI(uri);
        mjpegPlayerView.start();
    }

}
