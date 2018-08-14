package net.globulus.simisync;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Generated class by @EasyPrefs . Do not modify this code!
 */
public class EasyPrefs {

    private EasyPrefs() {
    }

    public static SharedPreferences getPrefs(Context context) {
        return net.globulus.simisync.PrefUtil.getPrefs(context);
    }

    // Basic storage methods

    public static void removePreferencesField(Context context, String key) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(key);
        editor.commit();
    }

    public static void putPreferencesField(Context context, String key, int value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getPreferencesField(Context context, String key, int defaultValue) {
        return getPrefs(context).getInt(key, defaultValue);
    }

    public static void putPreferencesField(Context context, String key, long value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getPreferencesField(Context context, String key, long defaultValue) {
        return getPrefs(context).getLong(key, defaultValue);
    }

    public static void putPreferencesField(Context context, String key, float value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static float getPreferencesField(Context context, String key, float defaultValue) {
        return getPrefs(context).getFloat(key, defaultValue);
    }

    public static void putPreferencesField(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getPreferencesField(Context context, String key, boolean defaultValue) {
        return getPrefs(context).getBoolean(key, defaultValue);
    }

    public static void putPreferencesField(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreferencesField(Context context, String key, String defaultValue) {
        return getPrefs(context).getString(key, defaultValue);
    }

    public static void putPreferencesField(Context context, String key, Set<String> value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static Set<String> getPreferencesField(Context context, String key, Set<String> defaultValue) {
        return getPrefs(context).getStringSet(key, defaultValue);
    }

    public static void addToPreferencesField(Context context, String key, String value) {
        Set<String> set = getPreferencesField(context, key, new HashSet<String>());
        set.add(value);
        putPreferencesField(context, key, set);
    }

    public static void removeFromPreferencesField(Context context, String key, String value) {
        Set<String> set = getPreferencesField(context, key, new HashSet<String>());
        set.remove(value);
        putPreferencesField(context, key, set);
    }

    // Generated methods


    public static String getCookie(Context context) {
        return getPreferencesField(context, "cookie", (java.lang.String) null);
    }

    public static void putCookie(Context context, String value) {
        putPreferencesField(context, "cookie", value);
    }

    public static void clear(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove("cookie");
        editor.commit();
    }

    public static void clearAll(Context context) {
        clear(context);
    }
}
