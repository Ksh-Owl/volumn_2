package com.example.volumn.include;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.volumn.chat.chat_msgCountModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Chat_PreferenceManager {

    public static final String PREFERENCES_NAME = "Chat_preference";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = 1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;




    private static SharedPreferences Chat_PreferenceManager(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * String 값 저장
     *
     * @param context
     * @param key
     * @param msg
     * @param time
     */                                                      //방이름                              //채팅내용
    public static void setChatArrayPref(Context context, String key, String msg,String time) throws JSONException {

        //deleteString(context,key);
       String json =  getChatArrayPref(context,key);

        SharedPreferences prefs = Chat_PreferenceManager(context);
        SharedPreferences.Editor editor = prefs.edit();

        if(json == null)
        {
            JSONArray jsonArray = new JSONArray();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg",msg);
            jsonObject.put("time",time);

            jsonArray.put(jsonObject);

            editor.putString(key,jsonArray.toString());

            editor.commit();
        }else
        {
            JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject = new JSONObject();




            jsonObject.put("msg",msg);
            jsonObject.put("time",time);


            jsonArray.put(jsonObject);

            editor.putString(key,jsonArray.toString());

            editor.commit();
        }


    }

    public static String getChatArrayPref(Context context, String key) {
        SharedPreferences prefs = Chat_PreferenceManager(context);
        String json = prefs.getString(key, null);

        return json;
    }
    public static void deleteString(Context context, String key)
    {
        SharedPreferences prefs = Chat_PreferenceManager(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }




}
