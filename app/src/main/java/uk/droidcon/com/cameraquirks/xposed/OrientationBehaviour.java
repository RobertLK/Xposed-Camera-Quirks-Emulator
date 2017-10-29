package uk.droidcon.com.cameraquirks.xposed;

import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Override how camera frames are interpreted via their reported orientation. Changes camera
 * behaviour, but may not be consistent between camera1 and camera2.
 */
public enum OrientationBehaviour implements Behaviour {
    DEFAULT {
        @Override
        int transformOrientation(int original) {
            return original;
        }
    },
    INVERTED {
        @Override
        int transformOrientation(int original) {
            return original + 180 % 360;
        }
    };

    public static final String KEY = "orientation_behaviour";
    private static final String BUNDLE_KEY_SAVED_ORIENTATION = "uk.droidcon.com.xposed.OrientationBehaviour.saved_orientation";

    abstract int transformOrientation(int original);

    @Override
    public void addCamera1Hook(ClassLoader classLoader, final Bundle state) {
        findAndHookMethod("android.hardware.Camera", classLoader, "getCameraInfo", int.class, Camera.CameraInfo.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Camera.CameraInfo result = (Camera.CameraInfo) param.args[1];
                /* Apps can call getCameraInfo multiple times and always get the same internal object,
                 * so need to avoid transforming multiple times */
                int transformedRotation = state.getInt(BUNDLE_KEY_SAVED_ORIENTATION, -1);
                if (transformedRotation == -1) {
                    XposedBridge.log("Original: " + result.orientation + " transformed: " + transformOrientation(result.orientation));
                    transformedRotation = transformOrientation(result.orientation);
                    state.putInt(BUNDLE_KEY_SAVED_ORIENTATION, transformedRotation);
                }

                result.orientation = transformedRotation;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void addCamera2Hook(ClassLoader classLoader, final Bundle state) {
        findAndHookMethod("android.hardware.camera2.CameraCharacteristics", classLoader, "get", CameraCharacteristics.Key.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final CameraCharacteristics.Key key = (CameraCharacteristics.Key) param.args[0];
                if (CameraCharacteristics.SENSOR_ORIENTATION.equals(key)) {
                    final Integer resultVal = (Integer) param.getResult();
                    param.setResult(transformOrientation(resultVal));
                }
            }
        });
    }
}
