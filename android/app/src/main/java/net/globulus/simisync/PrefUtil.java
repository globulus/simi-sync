package net.globulus.simisync;

import android.content.Context;
import android.content.SharedPreferences;

import net.globulus.easyprefs.annotation.Pref;
import net.globulus.easyprefs.annotation.PrefClass;
import net.globulus.easyprefs.annotation.PrefMaster;

@PrefClass(staticClass = false, origin = true)
public class PrefUtil {
    @Pref(nullable = true) String cookie = null;

    @PrefMaster
    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("beers", Context.MODE_PRIVATE);
    }
}
