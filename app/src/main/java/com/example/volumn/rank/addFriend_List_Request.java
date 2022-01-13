package com.example.volumn.rank;

import com.android.volley.toolbox.StringRequest;


import com.android.volley.Response;
import com.android.volley.AuthFailureError;

import java.util.HashMap;
import java.util.Map;

public class addFriend_List_Request extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://15.164.50.211/volumn/Select_FriendList.php";
    private Map<String, String> map;


    public addFriend_List_Request(String userEmail, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);


        map = new HashMap<>();
        map.put("userEmail",userEmail);


    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
