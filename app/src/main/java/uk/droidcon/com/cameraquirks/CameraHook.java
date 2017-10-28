package uk.droidcon.com.cameraquirks;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class CameraHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        /*
        If the package loads other packages, XPosed gets confused, so ignore this
         */
        if (!lpparam.isFirstApplication) {
            return;
        }

        findAndHookMethod("android.hardware.Camera", lpparam.classLoader, "open", int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("Camera Open");
                throw new RuntimeException("CameraQuirks disabled open!");
            }
        });
        findAndHookMethod("android.hardware.camera2.CameraManager", lpparam.classLoader, "openCamera", String.class, android.hardware.camera2.CameraDevice.StateCallback.class, android.os.Handler.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("camera2 open");
                throw new RuntimeException("CameraQuirks disabled open!");
            }
        });
    }
 }
