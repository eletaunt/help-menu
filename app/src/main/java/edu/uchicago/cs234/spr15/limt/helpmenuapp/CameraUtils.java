package edu.uchicago.cs234.spr15.limt.helpmenuapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by limt on 5/31/2015.
 */
public class CameraUtils {

    private static final String TAG = "DBG_ " + CameraUtils.class.getName();

    //Check if the device has a camera
    public static boolean deviceHasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    //Get available camera
    public static Camera getCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "Cannot getCamera()");
            return null;
        }
    }
}
