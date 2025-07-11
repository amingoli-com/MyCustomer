package ir.amingoli.mycoustomer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import java.util.Map;
import java.util.Set;

public class Session {

    private SharedPreferences ExtrasPref;
    private Editor extraEditor;

    private static final String PREF_EXTRAS = "SSP";

    Session(Context context) {
        ExtrasPref = context.getSharedPreferences(PREF_EXTRAS, Context.MODE_PRIVATE);
        extraEditor = ExtrasPref.edit();
    }

    public static Session getInstance(Context context) {
        return new Session(context.getApplicationContext());
    }

    public void putExtra(String key, String value) {
        extraEditor.putString(key, value);
        extraEditor.commit();
    }

    public void putExtra(String key, Boolean value) {
        extraEditor.putBoolean(key, value);
        extraEditor.commit();
    }

    public void putExtra(String key, Integer value) {
        extraEditor.putInt(key, value);
        extraEditor.commit();
    }

    public void putExtra(String key, Long value) {
        extraEditor.putLong(key, value);
        extraEditor.commit();
    }

    public void putExtra(String key, Set<String> list) {
        extraEditor.putStringSet(key, list);
        extraEditor.commit();
    }

    public String getString(String key) {
        return ExtrasPref.getString(key, "");
    }

    public Set<String> getSet(String key) {
        return ExtrasPref.getStringSet(key, null);
    }

    public int getInt(String key) {
        return ExtrasPref.getInt(key, 0);
    }

    public boolean getBoolean(String key) {
        return ExtrasPref.getBoolean(key, false);
    }

    public Long getLong(String key) {
        return ExtrasPref.getLong(key, 0);
    }

    public Map<String, ?> getExtras() {
        return ExtrasPref.getAll();
    }

    public boolean clearExtras() {
        extraEditor.clear();
        return extraEditor.commit();
    }

    public void remove(String key) {
        extraEditor.remove(key);
        extraEditor.apply();
    }
}