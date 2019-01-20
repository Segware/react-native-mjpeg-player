import Foundation
import UIKit

open class MjpegStreamingController: NSObject, URLSessionDataDelegate {
  
  fileprivate enum Status {
    case stopped
    case loading
    case playing
  }
  
  fileprivate var receivedData: NSMutableData?
  fileprivate var dataTask: URLSessionDataTask?
  fileprivate var session: Foundation.URLSession!
  fileprivate var status: Status = .stopped
  
  open var authenticationHandler: ((URLAuthenticationChallenge) -> (Foundation.URLSession.AuthChallengeDisposition, URLCredential?))?
  open var didStartLoading: (()->Void)?
  open var didFinishLoading: (()->Void)?
  open var contentURL: URL?
  open var imageView: UIImageView
  open var frameDate: String?
  open var frameTime: String?
  open var didReceiveError: ()->()?
  open var hasError = false
  
  public init(imageView: UIImageView, didReceiveError: @escaping ()->()) {
    self.imageView = imageView
    self.didReceiveError = didReceiveError
    super.init()
    self.session = Foundation.URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
  }
  
  public convenience init(imageView: UIImageView, contentURL: URL, didReceiveError: @escaping ()->()) {
    self.init(imageView: imageView, didReceiveError: didReceiveError)
    self.contentURL = contentURL
  }
  
  deinit {
    dataTask?.cancel()
  }
  
  open func play(url: URL){
    if status == .playing || status == .loading {
      stop()
    }
    contentURL = url
    play()
  }
  
  open func play() {
    guard let url = contentURL , status == .stopped else {
      return
    }

    status = .loading
    DispatchQueue.main.async { self.didStartLoading?() }
    
    receivedData = NSMutableData()
    let request = URLRequest(url: url)
    dataTask = session.dataTask(with: request)
    dataTask?.resume()

  }
  
  open func stop(){
    status = .stopped
    dataTask?.cancel()
  }
  
  @objc(URLSession:task:didCompleteWithError:)
  open func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
    self.hasError = true
    didReceiveError()
  }
  
  open func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive response: URLResponse, completionHandler: @escaping (URLSession.ResponseDisposition) -> Void) {
    self.hasError = false
    if let imageData = receivedData , imageData.length > 0,
      let receivedImage = UIImage(data: imageData as Data) {

      if status == .loading {
        status = .playing
        DispatchQueue.main.async { self.didFinishLoading?() }
      }
      
      DispatchQueue.main.async { self.imageView.image = receivedImage }
    }
    
    receivedData = NSMutableData()
    completionHandler(.allow)
  }
  
  open func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive data: Data) {
    self.hasError = false
    receivedData?.append(data)
    
    if let httpUrlResponse = dataTask.response as? HTTPURLResponse
    {
      if let fDate = httpUrlResponse.allHeaderFields["DGF-FrameDate"] as? String {
        self.frameDate = fDate
      }
      if let fTime = httpUrlResponse.allHeaderFields["DGF-FrameTime"] as? String {
        self.frameTime = fTime
      }
    }
  }
  
  open func urlSession(_ session: URLSession, task: URLSessionTask, didReceive challenge: URLAuthenticationChallenge, completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
    
    var credential: URLCredential?
    var disposition: Foundation.URLSession.AuthChallengeDisposition = .performDefaultHandling
    
    if challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust {
      if let trust = challenge.protectionSpace.serverTrust {
        credential = URLCredential(trust: trust)
        disposition = .useCredential
      }
    } else if let onAuthentication = authenticationHandler {
      (disposition, credential) = onAuthentication(challenge)
    }
    
    completionHandler(disposition, credential)
  }
}
