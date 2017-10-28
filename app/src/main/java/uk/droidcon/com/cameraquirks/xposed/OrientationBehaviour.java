package uk.droidcon.com.cameraquirks.xposed;

import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
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

    abstract int transformOrientation(int original);

    @Override
    public void addCamera1Hook(ClassLoader classLoader) {
        findAndHookMethod("android.hardware.Camera", classLoader, "getCameraInfo", int.class, Camera.CameraInfo.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Camera.CameraInfo result = (Camera.CameraInfo) param.args[1];
                XposedBridge.log("Original: " + result.orientation + " transformed: " + transformOrientation(result.orientation));
                result.orientation = transformOrientation(result.orientation);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void addCamera2Hook(ClassLoader classLoader) {
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
