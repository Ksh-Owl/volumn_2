package com.example.volumn.rank;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Rank_month_Request extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://13.209.66.177/volumn/Select_Rank_month.php";
    private Map<String, String> map;


    public Rank_month_Request(String userEmail,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);


        map = new HashMap<>();
        map.put("userEmail",userEmail);



    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
