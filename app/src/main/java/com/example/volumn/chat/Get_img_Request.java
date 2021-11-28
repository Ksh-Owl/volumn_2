package com.example.volumn.chat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Get_img_Request extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://13.209.66.177/volumn/Select_ChatIMG.php";
    private Map<String, String> map;


    public Get_img_Request(String img_ID,   Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("img_ID", img_ID);
        //map.put("room_ID", room_ID);




    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
