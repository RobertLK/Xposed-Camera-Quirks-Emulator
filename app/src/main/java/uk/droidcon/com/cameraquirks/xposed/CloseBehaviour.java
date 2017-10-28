package uk.droidcon.com.cameraquirks.xposed;

import android.os.Build;
import android.support.annotation.RequiresApi;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public enum CloseBehaviour implements Behaviour {
    DEFAULT(new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            XposedBridge.log("Calling super close");
        }
    }),
    EXCEPTION(new XC_MethodReplacement() {
        @Override
        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
            throw new RuntimeException("Xposed blocked camera close");
        }
    });

    private XC_MethodHook mHookAction;

    public static final String KEY = "close_behaviour";

    CloseBehaviour(XC_MethodHook action) {
        mHookAction = action;
    }

    @Override
    public void addCamera1Hook(ClassLoader classLoader) {
        findAndHookMethod("android.hardware.Camera", classLoader, "release", mHookAction);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void addCamera2Hook(ClassLoader classLoader) {
        findAndHookMethod("android.hardware.camera2.CameraDevice", classLoader, "close", mHookAction);
    }
}
