package uk.droidcon.com.cameraquirks.xposed;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

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

        applyBehaviour(OpenBehaviour.KEY, OpenBehaviour.class, lpparam);
        applyBehaviour(CloseBehaviour.KEY, CloseBehaviour.class, lpparam);
        applyBehaviour(OrientationBehaviour.KEY, OrientationBehaviour.class, lpparam);
    }

    private <Type extends Enum & Behaviour> void applyBehaviour(String prefsKey, Class<? extends Type> subClass, XC_LoadPackage.LoadPackageParam lpparam) {
        Behaviour behaviour = loadBehaviour(prefsKey, subClass);
        behaviour.addCamera1Hook(lpparam.classLoader);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            behaviour.addCamera2Hook(lpparam.classLoader);
        }
    }

    private <Type extends Enum & Behaviour> Behaviour loadBehaviour(String prefsKey, Class<? extends Type> subClass) {
        String name = mPrefs.getString(prefsKey, "DEFAULT");
        return Enum.valueOf(subClass, name);
    }

    private void initPrefs() {
        if (mPrefs != null) {
            mPrefs = new XSharedPreferences(getClass().getPackage().getName());
            mPrefs.makeWorldReadable();
        }
    }
}
