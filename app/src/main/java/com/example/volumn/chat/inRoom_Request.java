package com.example.volumn.chat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class inRoom_Request extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://54.180.2.213/volumn/update_Room.php";
    private Map<String, String> map;


    public inRoom_Request(String room_ID, String member,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("room_ID", room_ID);
        map.put("member", member);


    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
