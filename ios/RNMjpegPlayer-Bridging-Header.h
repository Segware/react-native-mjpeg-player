//
//  RNMjpegPlayer-Bridging-Header.h
//  RNMjpegPlayer
//
//  Created by Dev Segware on 30/03/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

#ifndef RNMjpegPlayer_Bridging_Header_h
#define RNMjpegPlayer_Bridging_Header_h

/// import RCTBridgeModule
#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#elif __has_include("RCTBridgeModule.h")
#import “RCTBridgeModule.h”
#else
#import "React/RCTBridgeModule.h" // Required when used as a Pod in a Swift project
#endif

// import RCTEventEmitter
#if __has_include(<React/RCTViewManager.h>)
#import <React/RCTViewManager.h>
#elif __has_include("RCTViewManager.h")
#import “RCTViewManager.h”
#else
#import "React/RCTViewManager.h" // Required when used as a Pod in a Swift project
#endif

#import <React/RCTUIManager.h>

#endif /* RNMjpegPlayer_Bridging_Header_h */
