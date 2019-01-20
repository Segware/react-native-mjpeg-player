
package com.segware;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class RNMjpegPlayerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNMjpegPlayerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNMjpegPlayer";
  }
}