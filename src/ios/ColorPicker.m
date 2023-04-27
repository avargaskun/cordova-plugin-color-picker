#import "ColorPicker.h"

@interface ColorPicker()
@property(strong, nonatomic) CDVInvokedUrlCommand *showCommand;
@property (nonatomic, assign) Boolean withProgress;
- (UIColor *)colorFromHexString:(NSString *)hexString;
@end

@implementation ColorPicker

- (void) isAvailable:(CDVInvokedUrlCommand*)command
{
    BOOL available = FALSE;
    if (@available(iOS 14, *))
    {
        available = TRUE;
    }
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:available];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) showDialog:(CDVInvokedUrlCommand*)command
{
    NSDictionary *options = [command argumentAtIndex:0];
    if (@available(iOS 14, *))
    {
        NSString* title = [[options objectForKey:@"title"] stringValue];
        NSString* hexColor = [[options objectForKey:@"color"] stringValue];
        UIColor* color = [self colorFromHexString:hexColor];
        UIColorPickerViewController *picker = [UIColorPickerViewController new];
        picker.delegate = self;
        picker.title = title;
        picker.selectedColor = color;
        picker.supportsAlpha = [[options objectForKey:@"withAlpha"] boolValue];
        self.withProgress = [[options objectForKey:@"withProgress"] boolValue];
        self.showCommand = command;
        [self.viewController presentViewController:picker animated:TRUE completion:nil];
    }
    else
    {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"NOT_AVAILABLE"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) colorPickerViewControllerDidFinish:(UIColorPickerViewController*)viewController
API_AVAILABLE(ios(14.0)){
    if (self.showCommand != nil) {
        NSDictionary *result = @{
            @"color": [self selectedColorHex:viewController],
            @"dismissed": @TRUE
        };
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.showCommand.callbackId];
    }
}

- (void) colorPickerViewController:(UIColorPickerViewController*)viewController didSelectColor:(UIColor *)color continuously:(BOOL)continuously
API_AVAILABLE(ios(14.0)){
    if (self.withProgress && !continuously && self.showCommand != nil) {
        NSDictionary *result = @{
            @"color": [self selectedColorHex:viewController],
            @"dismissed": @FALSE
        };
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
        pluginResult.keepCallback = @TRUE;
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.showCommand.callbackId];
    }
}

- (NSString *)selectedColorHex:(UIColorPickerViewController*)viewController
API_AVAILABLE(ios(14.0)) {
    const CGFloat* components = CGColorGetComponents(viewController.selectedColor.CGColor);
    const CGFloat alpha = CGColorGetAlpha(viewController.selectedColor.CGColor);
    NSString* hexValue;
    if (viewController.supportsAlpha) {
        hexValue = [NSString stringWithFormat:@"#%02lX%02lX%02lX%02lX", lround(alpha * 255), lround(components[0] * 255), lround(components[1] * 255), lround(components[2] * 255)];
    } else {
        hexValue = [NSString stringWithFormat:@"#%02lX%02lX%02lX", lround(components[0] * 255), lround(components[1] * 255), lround(components[2] * 255)];
    }
    return hexValue;
}

- (UIColor *)colorFromHexString:(NSString *)hexString {
    unsigned rgbValue = 0;
    float alphaValue = 1.0;
    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    [scanner setScanLocation:1]; // bypass '#' character
    [scanner scanHexInt:&rgbValue];
    if (hexString.length==9) {
        alphaValue = ((rgbValue & 0xFF000000) >> 24)/255.0;
    }
    return [UIColor colorWithRed:((rgbValue & 0x00FF0000) >> 16)/255.0 green:((rgbValue & 0x0000FF00) >> 8)/255.0 blue:(rgbValue & 0x000000FF)/255.0 alpha:alphaValue];
}

@end
