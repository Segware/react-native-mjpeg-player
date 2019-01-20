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
