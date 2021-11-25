package com.example.volumn.include;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatCount_PreferenceManager {

    public static final String PREFERENCES_NAME = "ChatCount_preference";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = 1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;




    private static SharedPreferences ChatCount_PreferenceManager(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * String 값 저장
     *
     * @param context
     * @param key


     */                                                      //방이름
    public static void setChatCount(Context context, String key ,String msg,String lastTime,String mem_count) throws JSONException {

        String json  =  getChatCount(context,key);
        int Count=0;

        if(json != null)
        {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String item_count = item.getString("count");
                //String item_msg = item.getString("msg");
                Count = Integer.parseInt(item_count);

            }

        }



        SharedPreferences prefs = ChatCount_PreferenceManager(context);
        SharedPreferences.Editor editor = prefs.edit();
       // ArrayList<chat_msgCountModel> arrayList = new ArrayList<chat_msgCountModel>();
       // arrayList.add(value);
        //JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        Count++;
        String Count_string = Integer.toString(Count);

        jsonObject.put("count",Count_string);
        jsonObject.put("msg",msg);
        jsonObject.put("lastTime",lastTime);
        jsonObject.put("mem_count",mem_count);//방인원수

        jsonArray.put(jsonObject);


        editor.putString(key,jsonArray.toString());

        editor.commit();
    }
    public static void resetChatCount(Context context, String key) throws JSONException {

        String json  =  getChatCount(context,key);
        int Count = 0;
        String msg = "";
        String lastTime = "";
        String mem_count = "";


        if(json != null)
        {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);
                //String item_count = item.getString("count");
                String item_msg = item.getString("msg");
                lastTime = item.getString("lastTime");
                mem_count = item.getString("mem_count");

                ///Count = Integer.parseInt(item_count);
                 msg = item_msg;
            }

        }



        SharedPreferences prefs = ChatCount_PreferenceManager(context);
        SharedPreferences.Editor editor = prefs.edit();
        // ArrayList<chat_msgCountModel> arrayList = new ArrayList<chat_msgCountModel>();
        // arrayList.add(value);
        //JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        String Count_string = Integer.toString(Count);

        jsonObject.put("count",Count_string);
        jsonObject.put("msg",msg);
        jsonObject.put("lastTime",lastTime);
        jsonObject.put("mem_count",mem_count);//방인원수
        jsonArray.put(jsonObject);


        editor.putString(key,jsonArray.toString());

        editor.commit();
    }
    public static String getChatCount(Context context, String key) {
        SharedPreferences prefs = ChatCount_PreferenceManager(context);
        String josn =prefs.getString(key, null) ;

        return josn;
    }
    public static void deleteString(Context context, String key)
    {
        SharedPreferences prefs = ChatCount_PreferenceManager(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }




}
