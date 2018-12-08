package com.mjpegplayer;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;


public class RNMjpegPlayerManager extends SimpleViewManager<View> {
    public static final String REACT_CLASS = "MjpegPlayer";
    private ThemedReactContext mContext;
    private View view;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public View createViewInstance(ThemedReactContext context){
        mContext = context;
        view = new View(context);
        view.setBackgroundColor(Color.BLUE);
        return view;
    }

    @ReactProp(name = "exampleProp")
    public void setExampleProp(View view, String prop) {
        // TODO
    }

}
