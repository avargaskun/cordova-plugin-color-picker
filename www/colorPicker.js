module.exports = {
    isAvailable: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "ColorPicker", "isAvailable", []);
    },
    showDialog: function (options, successCallback, errorCallback) {
        const defaults = {
            title: 'Choose color',
            color: '#FFFFFF',
            withAlpha: false,
        };
        Object.assign(defaults, options);
        cordova.exec(successCallback, errorCallback, "ColorPicker", "showDialog", [defaults]);
    },
};
