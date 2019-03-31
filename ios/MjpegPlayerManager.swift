//
//  MjpegPlayerManager.swift
//  RNMjpegPlayer
//
//  Created by Dev Segware on 30/03/19.
//  Copyright © 2019 Facebook. All rights reserved.
//

import Foundation

@objc(MjpegPlayerManager)
class MjpegPlayerManager: RCTViewManager {
  
  override func view() -> UIView! {
    return MjpegPlayerView()
  }
  
  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  @objc func stopVideo(_ node: NSNumber) {
    DispatchQueue.main.async {
      let component = self.bridge.uiManager.view(
        forReactTag: node
        ) as! MjpegPlayerView
      component.stopVideo()
    }
  }

}
