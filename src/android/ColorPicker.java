package com.plugin.colorpicker;

import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ColorPicker extends CordovaPlugin {
    private static final String TAG = "ColorPicker";
    private static final String IS_AVAILABLE = "isAvailable";
    private static final String SHOW_DIALOG = "showDialog";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, action);
        if (action.equals(IS_AVAILABLE)) {
            callbackContext.success(new JSONObject("true"));
            return true;
        } else if (action.equals(SHOW_DIALOG)) {
            showDialog(args, callbackContext);
            return true;
        } else {
            return false;
        }
    }

    private void showDialog(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final JSONObject options = args.getJSONObject(0);
        final String title = options.getString("title");
        final Integer initial = Integer.parseUnsignedInt(options.getString("color").substring(1), 16);
        final boolean withAlpha = options.getBoolean("withAlpha");
        final boolean withProgress = options.getBoolean("withProgress");
        final String okText = options.getString("okText");
        final String cancelText = options.getString("cancelText");
        cordova.getActivity().runOnUiThread(() -> {
            showDialog(callbackContext, title, okText, cancelText, initial, withAlpha, withProgress);
        });
    }

    private void showDialog(
            final CallbackContext callbackContext,
            final String title,
            final String okText,
            final String cancelText,
            final Integer initial,
            final boolean withAlpha,
            final boolean withProgress)
    {
        final Integer[] color = {initial};
        final boolean[] cancelled = {false};
        final AlertDialog dialog = ColorPickerDialogBuilder
                .with(cordova.getActivity())
                .setTitle(title)
                .initialColor(withAlpha ? initial : initial | 0xFF000000)
                .showAlphaSlider(withAlpha)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                    color[0] = selectedColor;
                    if (withProgress) {
                        updateProgress(callbackContext, withAlpha, selectedColor);
                    }
                })
                .setOnColorChangedListener(changedColor -> {
                    color[0] = changedColor;
                    if (withProgress) {
                        updateProgress(callbackContext, withAlpha, changedColor);
                    }
                })
                .setPositiveButton(okText, (dialog12, selectedColor, allColors) -> {})
                .setNegativeButton(cancelText, (dialog1, which) -> cancelled[0] = true)
                .build();
        dialog.setOnDismissListener(dialog2 -> {
            if (cancelled[0]) {
                callbackContext.error("CANCELLED");
                return;
            }
            try {
                Integer selectedColor = color[0];
                String hexColor = "#" + String.format("%08X", selectedColor);
                if (!withAlpha) {
                    selectedColor &= 0xFFFFFF;
                    hexColor = "#" + String.format("%06X", selectedColor);
                }
                final JSONObject result = new JSONObject();
                result.put("color", hexColor);
                result.put("dismissed", true);
                callbackContext.success(result);
            } catch (Exception e) {
                LOG.e(TAG, "Failed to serialize result", e);
                callbackContext.error(e.getMessage());
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private static void updateProgress(CallbackContext callbackContext, boolean withAlpha, int selectedColor) {
        try {
            String hexColor = "#" + String.format("%08X", selectedColor);
            if (!withAlpha) {
                selectedColor &= 0xFFFFFF;
                hexColor = "#" + String.format("%06X", selectedColor);
            }
            final JSONObject result = new JSONObject();
            result.put("color", hexColor);
            result.put("dismissed", false);
            final PluginResult message = new PluginResult(PluginResult.Status.OK, result);
            message.setKeepCallback(true);
            callbackContext.sendPluginResult(message);
        } catch (Exception e) {
            LOG.e(TAG, "Failed to serialize result", e);
        }
    }
}

