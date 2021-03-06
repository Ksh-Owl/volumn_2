package com.example.volumn.chat;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Upload_img_Request extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://54.180.2.213/volumn/insert_ChatIMG.php";
    private Map<String, String> map;


    public Upload_img_Request(String room_nm, String room_ID,String user_name,String encodeImgString , Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("room_nm", room_nm);
        map.put("room_ID", room_ID);

        map.put("user_name", user_name);
        map.put("encodeImgString", encodeImgString);


    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
