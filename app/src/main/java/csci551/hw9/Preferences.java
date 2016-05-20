package csci551.hw9;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Youngmin on 2016. 4. 11..
 */
public class Preferences {

    public static final String PREFS_NAME = "Youngmin";

    public void save(Context context, String name, String value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString(name, value); //3
        editor.commit(); //4
    }

    public String getValue(Context context, String name) {
        SharedPreferences settings;
        String value;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        value = settings.getString(name, null); //2
        return value;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context, String name) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(name);
        editor.commit();
    }

}
