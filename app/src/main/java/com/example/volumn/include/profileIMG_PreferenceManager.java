package com.example.volumn.include;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class profileIMG_PreferenceManager {

    public static final String PREFERENCES_NAME = "profileIMG_preference";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = 1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;




    private static SharedPreferences profileIMG_PreferenceManager(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
    }

    /**
     * String 값 저장
     *
     * @param context
     * @param userEmail
     * @param img


     */                                                      //방이름
    public static void setProfileIMG(Context context, String userEmail ,String img) throws JSONException {

        try{
            deleteString(context,userEmail);

        }catch (Exception e){

        }

            SharedPreferences prefs = profileIMG_PreferenceManager(context);
            SharedPreferences.Editor editor = prefs.edit();



            editor.putString(userEmail,img);

            editor.commit();


    }
    public static String getProfileIMG(Context context, String userEmail) {
        SharedPreferences prefs = profileIMG_PreferenceManager(context);
        String josn =prefs.getString(userEmail, null) ;

        return josn;
    }
    public static void deleteString(Context context, String userEmail)
    {
        SharedPreferences prefs = profileIMG_PreferenceManager(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(userEmail);
        editor.commit();
    }




}
