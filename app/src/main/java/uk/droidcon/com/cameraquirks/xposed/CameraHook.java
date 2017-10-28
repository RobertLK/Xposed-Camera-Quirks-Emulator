package uk.droidcon.com.cameraquirks.xposed;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
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

        //TODO: Load correct behaviour from prefs
        final OpenBehaviour openBehaviour = OpenBehaviour.NULL;
        openBehaviour.addCamera1Hook(lpparam.classLoader);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            openBehaviour.addCamera2Hook(lpparam.classLoader);
        }
    }
}
