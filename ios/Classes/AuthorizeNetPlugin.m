#import "AuthorizeNetPlugin.h"
#if __has_include(<authorize_net_plugin/authorize_net_plugin-Swift.h>)
#import <authorize_net_plugin/authorize_net_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "authorize_net_plugin-Swift.h"
#endif

@implementation AuthorizeNetPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAuthorizeNetPlugin registerWithRegistrar:registrar];
}
@end
