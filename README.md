# Color Picker Plugin for Cordova (cordova-plugin-color-picker)

## Description

This plugin allows you to display a color-picker native dialog in iOS and Android. You can control certain properties of the
dialog to be displayed, such as whether to show an alpha-level slider or the initial color picked. Inputs and outputs use the
HTML color hex syntax, e.g. `#F8F8F8`

Example:

```javascript
let color = '#F8F8F8';
cordova.plugins.colorPicker.showDialog({ 
    color,
    withAlpha: false 
}, (result) => {
    console.log(`New color: ${result.color}`);
}, (error) => {
    console.log(`Something went wrong: ${error}`);
});
```

## Installation

```
cordova plugin add cordova-plugin-color-picker
```

## Supported platforms

* Android
* iOS

### iOS Notes

The native dialog for iOS relies on [UIColorPickerViewController](https://developer.apple.com/documentation/uikit/uicolorpickerviewcontroller) which is only available on iOS 14 or newer. You can check if the current device supports the feature by calling the `isSupported` function in the plug-in.

### Android Notes

The native dialog for Android relies on https://github.com/QuadFlask/colorpicker

## Methods

### isAvailable

Checks whether the device supports showing the color picker dialog. Currently this always return `true` on Android. On iOS it will return `true` only if the device is running iOS 14 or later

```javascript
cordova.plugins.colorPicker.isAvailable((available) => {
    if (available) {
        console.log('Color picker is available');
    } else {
        console.log('Color picker is NOT available');
    }
}, (error) => {
    console.log(`Something went wrong: ${error}`);
});
```

### showDialog

Displays the color selection dialog and returns the color picked by the user.

```javascript
let color = '#F8F8F8';
cordova.plugins.colorPicker.showDialog({ 
    color,
    withAlpha: false 
}, (result) => {
    console.log(`New color: ${result.color}`);
}, (error) => {
    console.log(`Something went wrong: ${error}`);
});
```

The options available are:

* `title`: The string to show as title of the dialog. Defaults to `'Choose color'`
* `color`: The initial color to pick. Defaults to `'#FFFFFF'`
* `withAlpha`: Whether to display the alpha slider. Defaults to `false`

The result object has the following values:

* `color`: The color picked by the user. If the initial option `withAlpha` was set to `true` the alpha level will be the first 2 hex characters of the string. For example, black color with 100% opacity: `'#FF000000'`
