package com.example.volumn.calendar;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CalenedarCheckRequest extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://54.180.163.5/volumn/Select_CalendarCheck.php";
    private Map<String, String> map;


    public CalenedarCheckRequest(String userEmail,String DATE, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);


        map = new HashMap<>();
        map.put("userEmail",userEmail);

        map.put("DATE",DATE);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
