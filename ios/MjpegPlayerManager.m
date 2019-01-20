#import <Foundation/Foundation.h>
#import "React/RCTViewManager.h"

@interface RCT_EXTERN_MODULE(MjpegPlayerManager, RCTViewManager)
  RCT_EXPORT_VIEW_PROPERTY(settings, NSDictionary)
  RCT_EXPORT_VIEW_PROPERTY(onDateTimeChange, RCTBubblingEventBlock)
  RCT_EXPORT_VIEW_PROPERTY(onMjpegError, RCTBubblingEventBlock)
  RCT_EXTERN_METHOD(stopVideo:(nonnull NSNumber *)node)
@end

