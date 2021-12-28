package com.plugin.colorpicker;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flask.colorpicker.*;
import com.flask.colorpicker.builder.*;

import android.content.DialogInterface;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;

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
        final Integer initial = Integer.parseInt(options.getString("color").substring(1), 16);
        final boolean withAlpha = options.getBoolean("withAlpha");
        final Integer[] color = {initial};
        final AlertDialog dialog = ColorPickerDialogBuilder
                .with(cordova.getActivity())
                .setTitle(title)
                .initialColor(withAlpha ? initial : initial | 0xFF000000)
                .showAlphaSlider(withAlpha)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        color[0] = selectedColor;
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                    }
                })
                .setNegativeButton("cancel", (DialogInterface.OnClickListener) (dialog1, which) -> color[0] = initial)
                .build();
        dialog.setOnDismissListener(dialog2 -> {
            try {
                Integer selectedColor = color[0];
                String hexColor = "#" + String.format("%08X", selectedColor);
                if (!withAlpha) {
                    selectedColor &= 0xFFFFFF;
                    hexColor = "#" + String.format("%06X", selectedColor);
                }
                final JSONObject result = new JSONObject();
                result.put("color", hexColor);
                callbackContext.success(result);
            } catch (Exception e) {
                LOG.e(TAG, "Failed to serialize result", e);
                callbackContext.error(e.getMessage());
            }
        });
        dialog.show();
    }
}

