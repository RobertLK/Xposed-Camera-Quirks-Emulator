package uk.droidcon.com.cameraquirks.xposed;

import android.os.Build;
import android.support.annotation.RequiresApi;

public interface Behaviour {
    void addCamera1Hook(ClassLoader classLoader);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void addCamera2Hook(ClassLoader classLoader);
}
