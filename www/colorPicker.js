module.exports = {
    isAvailable: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "ColorPicker", "isAvailable", []);
    },
    showDialog: function (options, successCallback, errorCallback) {
        const defaults = {
            title: 'Choose color',
            okText: 'ok',
            cancelText: 'cancel',
            color: '#FFFFFF',
            withAlpha: false,
            withProgress: false,
        };
        Object.assign(defaults, options);
        cordova.exec(successCallback, errorCallback, "ColorPicker", "showDialog", [defaults]);
    },
};
