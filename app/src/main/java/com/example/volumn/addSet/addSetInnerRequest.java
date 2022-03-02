package com.example.volumn.addSet;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class addSetInnerRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://54.180.2.213/volumn/Select_PerformList.php";
    private Map<String, String> map;


    public addSetInnerRequest(String SET_ID, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);


        map = new HashMap<>();
        map.put("SET_ID",SET_ID);


    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
