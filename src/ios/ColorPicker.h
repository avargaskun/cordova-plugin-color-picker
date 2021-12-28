#import <Cordova/CDVPlugin.h>

@interface ColorPicker : CDVPlugin <UIColorPickerViewControllerDelegate>

- (void)isAvailable:(CDVInvokedUrlCommand *)command;
- (void)showDialog:(CDVInvokedUrlCommand *)command;

@end
