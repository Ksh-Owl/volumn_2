package com.example.volumn.setting;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class get_ProfileIMG_Request extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://15.164.50.211/volumn/Select_profileIMG.php";
    private Map<String, String> map;


    public get_ProfileIMG_Request(String user_Email,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("user_Email", user_Email);




    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
