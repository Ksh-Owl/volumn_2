package com.example.volumn.include;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class myRoom_PreferenceManager {

    public static final String PREFERENCES_NAME = "myRoom_preference";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = 1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;




    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
    }

    /**
     * String 값 저장
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value) throws JSONException {

        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        String json = getString(context,key);
        JSONArray jsonArray;
        if(json !=null )
        {
             jsonArray = new JSONArray(json);

             for (int i = 0; i < jsonArray.length(); i++){
                 JSONObject item = jsonArray.getJSONObject(i);
                String room_name = item.getString("value");

                if(room_name.equals(value))
                {
                    return;
                }
             }

        }else
        {
            jsonArray = new JSONArray();
        }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("value",value);

        jsonArray.put(jsonObject);

        editor.putString(key, jsonArray.toString());
        editor.commit();




    }

    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, null);
        return value;
    }
    public static void deleteString(Context context, String key,String room_nm) throws JSONException {
        String json = getString(context,key);

        JSONArray jsonArray = new JSONArray(json);
        JSONArray jsonArray_save = new JSONArray();

        for (int i = 0; i < jsonArray.length() ; i++){

            JSONObject item = jsonArray.getJSONObject(i);
            String roomName = item.getString("value");

            if(!roomName.equals(room_nm)){

                jsonArray_save.put(item);

            }

        }
        Log.e("쉐어드","저장된 방 삭제");
        Log.e("쉐어드","삭제 방이름:"+room_nm);
        Log.e("쉐어드","저장되는 방이름:"+jsonArray_save.toString());

        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, jsonArray_save.toString());
        editor.commit();
    }




}
