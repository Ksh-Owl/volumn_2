package com.example.volumn.addSet;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class pop_addSetRequest extends StringRequest {


    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://54.180.163.5/volumn/insert_Set.php";
    private Map<String, String> map;


    public pop_addSetRequest(String workout_ID,String Date,String set,String count,String weight ,Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);


        map = new HashMap<>();
        map.put("workout_ID",workout_ID);
        map.put("Date",Date);
        map.put("set",set);
        map.put("count",count);
        map.put("weight",weight);



    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
