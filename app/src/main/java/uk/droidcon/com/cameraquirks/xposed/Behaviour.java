package uk.droidcon.com.cameraquirks.xposed;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

/**
 * Enum subclasses of this should define a default value called "DEFAULT" to be loaded when
 * no value is set. This will normally apply no hook, or a hook which just passes to the parent
 * method with no modification.
 *
 * Enum subclasses should also define a key for storing their value in SharedPreferences in a
 * standard way.
 *
 * Hooks are called with a Bundle to allow persisting data between hooked methods or across
 * Behaviour instances. Bundle keys should be prefixed with the class path to avoid clashing.
 */
public interface Behaviour {
    public static final String DEFAULT_VALUE_NAME = "DEFAULT";

    void addCamera1Hook(ClassLoader classLoader, Bundle processState);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void addCamera2Hook(ClassLoader classLoader, Bundle processState);
}
