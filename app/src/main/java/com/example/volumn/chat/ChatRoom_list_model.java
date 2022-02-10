package com.example.volumn.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ChatRoom_list_model extends ViewModel {

    private MutableLiveData<ArrayList<chatRoom_Model>>  room_list;

    public MutableLiveData<ArrayList<chatRoom_Model>> getChatRoom(){
        if(room_list == null)
        {
            room_list = new MutableLiveData<ArrayList<chatRoom_Model>>();
        }

        return room_list;
    }

}
