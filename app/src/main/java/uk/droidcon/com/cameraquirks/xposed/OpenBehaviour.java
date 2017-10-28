package uk.droidcon.com.cameraquirks.xposed;

import android.os.Build;
import android.support.annotation.RequiresApi;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public enum OpenBehaviour implements Behaviour {
    DEFAULT(new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("Calling super open");
        }
    }),
    NULL(new XC_MethodReplacement() {
        @Override
        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
            return null;
        }
    }),
    EXCEPTION(new XC_MethodReplacement() {
        @Override
        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
            throw new RuntimeException("Xposed blocked camera open");
        }
    });

    private XC_MethodHook mHookAction;

    public static final String KEY = "open_behaviour";

    OpenBehaviour(XC_MethodHook action) {
        mHookAction = action;
    }

    @Override
    public void addCamera1Hook(ClassLoader classLoader) {
        findAndHookMethod("android.hardware.Camera", classLoader, "open", mHookAction);
        findAndHookMethod("android.hardware.Camera", classLoader, "open", int.class, mHookAction);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void addCamera2Hook(ClassLoader classLoader) {
        findAndHookMethod("android.hardware.camera2.CameraManager", classLoader, "openCamera", String.class, android.hardware.camera2.CameraDevice.StateCallback.class, android.os.Handler.class, mHookAction);
    }
}
