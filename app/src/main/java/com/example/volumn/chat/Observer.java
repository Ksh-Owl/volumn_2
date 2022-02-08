package com.example.volumn.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.include.ChatCount_PreferenceManager;
import com.example.volumn.include.myRoom_PreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Observer implements DefaultLifecycleObserver {
    private static final String TAG = "MainActivityObserver";


    @Override
    public void onCreate(@NonNull @NotNull LifecycleOwner owner) {

        Log.d(owner.toString(), "onCreate");

    }

    @Override
    public void onStart(@NonNull @NotNull LifecycleOwner owner) {
        Log.d(owner.toString(), "onStart");

    }

    @Override
    public void onResume(@NonNull @NotNull LifecycleOwner owner) {
        Log.d(owner.toString(), "onResume");
        try {
            ((ChatRoomActivity)ChatRoomActivity.context).noReadCount();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause(@NonNull @NotNull LifecycleOwner owner) {
        Log.d(owner.toString(), "In LifecycleObserver - onPause");

    }

    @Override
    public void onStop(@NonNull @NotNull LifecycleOwner owner) {
        Log.d(owner.toString(), "In LifecycleObserver - onStop");

    }

    @Override
    public void onDestroy(@NonNull @NotNull LifecycleOwner owner) {
        Log.i(TAG, "Observer ON_DESTROY");

    }



}

