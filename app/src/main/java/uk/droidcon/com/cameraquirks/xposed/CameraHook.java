package uk.droidcon.com.cameraquirks.xposed;

import android.os.Build;
import android.os.Bundle;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import uk.droidcon.com.cameraquirks.Constants;

public class CameraHook implements IXposedHookLoadPackage {
    private XSharedPreferences mPrefs;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        /*
        If the package loads other packages, XPosed gets confused, so ignore this
         */
        if (!lpparam.isFirstApplication) {
            return;
        }

        initPrefs();

        Bundle processState = new Bundle();
        applyBehaviour(OpenBehaviour.KEY, OpenBehaviour.class, lpparam, processState);
        applyBehaviour(CloseBehaviour.KEY, CloseBehaviour.class, lpparam, processState);
        applyBehaviour(OrientationBehaviour.KEY, OrientationBehaviour.class, lpparam, processState);
    }

    private <Type extends Enum & Behaviour> void applyBehaviour(String prefsKey, Class<? extends Type> subClass, XC_LoadPackage.LoadPackageParam lpparam, Bundle state) {
        Behaviour behaviour = loadBehaviour(prefsKey, subClass);
        behaviour.addCamera1Hook(lpparam.classLoader, state);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            behaviour.addCamera2Hook(lpparam.classLoader, state);
        }
    }

    private <Type extends Enum & Behaviour> Behaviour loadBehaviour(String prefsKey, Class<? extends Type> subClass) {
        String name = mPrefs.getString(prefsKey, Behaviour.DEFAULT_VALUE_NAME);
        /*Intellij says this cast is redundant, but it doesn't compile without it. I'm probably
         * doing something wrong with generics
         */
        return (Behaviour) Enum.valueOf(subClass, name);
    }

    private void initPrefs() {
        if (mPrefs == null) {
            mPrefs = new XSharedPreferences(Constants.ROOT_PACKAGE, Constants.SHARED_PREFS_NAME);
            mPrefs.makeWorldReadable();
        }
    }
}
