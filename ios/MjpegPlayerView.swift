import UIKit

@objc(MjpegPlayerView)
class MjpegPlayerView: UIView {
  
  var url = ""
  var width = 0.0
  var height = 0.0
  var streamingController: MjpegStreamingController!
  @objc var onDateTimeChange: RCTBubblingEventBlock?
  @objc var onMjpegError: RCTBubblingEventBlock?
  var frameDateScheduledTimer : Timer?
  var datetimeFrame:String!
  
  func getImageView(width:Double, height: Double) -> UIImageView{
    let imageView = UIImageView()
    imageView.frame = CGRect(x: 0, y: 0, width: width, height: height)
    return imageView
  }
  
  @objc func stopVideo(){
    self.removeFromSuperview();
    if self.streamingController != nil {
      self.streamingController.stop()
    }
    self.stopFrameDateScheduledTimer()
  }
  
  @objc func sendDatetimeChange() {
    if let frameDate = self.streamingController.frameDate, let frameTime = self.streamingController.frameTime {
      let timeSize = frameTime.index(frameTime.startIndex, offsetBy: 7)
      self.datetimeFrame = "\(frameDate.replacingOccurrences(of: ".", with: "-")) \(frameTime[...timeSize].replacingOccurrences(of: ".", with: ":"))"
      if self.onDateTimeChange != nil {
        self.onDateTimeChange!(["datetime": self.datetimeFrame!])
      }
    }
  }
  
  @objc func didReceiveError(){
    if self.streamingController != nil {
      self.streamingController.stop()
    }
    self.stopFrameDateScheduledTimer()
    if self.onMjpegError != nil {
      self.onMjpegError!(["hasError": true])
    }
  }
  
  func updateCamera(url: String, mjpegStreaming:  MjpegStreamingController){
    stopFrameDateScheduledTimer()
    startFrameDateScheduledTimer()
    mjpegStreaming.contentURL = URL(string: url)
    mjpegStreaming.play()
    if self.streamingController.hasError {
        stopFrameDateScheduledTimer()
        didReceiveError()
    }
    self.addSubview(mjpegStreaming.imageView)
  }
  
  func startFrameDateScheduledTimer(){
    if self.frameDateScheduledTimer == nil {
      self.frameDateScheduledTimer = Timer.scheduledTimer(timeInterval: 0.4, target: self, selector: #selector(self.sendDatetimeChange), userInfo: nil, repeats: true)
    }
  }
  
  func stopFrameDateScheduledTimer(){
    if self.frameDateScheduledTimer != nil {
      self.frameDateScheduledTimer!.invalidate()
      self.frameDateScheduledTimer = nil
    }
  }

  @objc func setSettings(_ val: NSDictionary) {
    self.url = val["url"] as! String
    self.width = val["width"] as! Double
    self.height = val["height"] as! Double
    self.streamingController = MjpegStreamingController(imageView: self.getImageView(width: self.width, height: self.height), didReceiveError: self.didReceiveError)
    self.updateCamera(url: self.url, mjpegStreaming: self.streamingController)
  }
  
  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
  }
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    self.streamingController = MjpegStreamingController(imageView: self.getImageView(width: self.width, height: self.height), didReceiveError: self.didReceiveError)
    if !self.url.isEmpty {
        self.updateCamera(url: self.url, mjpegStreaming: self.streamingController)
    }
    self.startFrameDateScheduledTimer()
  }

}
