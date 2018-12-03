package com.mjpegplayer;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;


public class RNMjpegPlayerManager extends SimpleViewManager<MjpegPlayerView> {
    public static final String REACT_CLASS = "MjpegPlayer";
    private ThemedReactContext mContext;
    private MjpegPlayerView mjpegPlayerView;
    private static final int COMMAND_STOP_VIDEO = 1;

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

    @Override
    public Map<String,Integer> getCommandsMap() {
        Log.d("React"," View manager getCommandsMap:");
        return MapBuilder.of(
                "stopVideo",
                COMMAND_STOP_VIDEO);
    }

    @Override
    public void receiveCommand(
            MjpegPlayerView mjpegPlayerView,
            int commandType,
            @Nullable ReadableArray args) {
        Assertions.assertNotNull(mjpegPlayerView);
        Assertions.assertNotNull(args);
        switch (commandType) {
            case COMMAND_STOP_VIDEO: {
                mjpegPlayerView.stop();
                return;
            }

            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported command %d received by %s.",
                        commandType,
                        getClass().getSimpleName()));
        }
    }

    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "datetimeChange",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onDateTimeChange")))
                .put(
                        "mjpegError",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onMjpegError")))
                .build();
    }
}
