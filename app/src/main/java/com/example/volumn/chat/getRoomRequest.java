package com.example.volumn.chat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class getRoomRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://13.209.66.177/volumn/Select_Room.php";
    private Map<String, String> map;


    public getRoomRequest( Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
       // map.put("room_nm", room_nm);
       // map.put("member", member);
       // map.put("mem_count", mem_count);


    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
