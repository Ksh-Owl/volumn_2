package com.example.volumn.rank;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class addFriend_accpetRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://13.209.66.177/volumn/insert_Friend_accept.php";
    private Map<String, String> map;


    public addFriend_accpetRequest(String userEmail,String friend_ID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);


        map = new HashMap<>();
        map.put("friend_ID",friend_ID);
        map.put("userEmail",userEmail);


    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
