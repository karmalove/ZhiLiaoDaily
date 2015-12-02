package kevin.app.zhiliaodaily.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Kevin on 2015/11/26.
 */
public class PreUtils {
    public static void putStringToDefault(Context context,String key,String value){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(key,value).commit();
    }
    public static String getStringFromDefault(Context context,String key,String defValue){
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key,defValue);
    }
}
