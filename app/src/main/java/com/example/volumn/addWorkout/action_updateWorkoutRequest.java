package com.example.volumn.addWorkout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class action_updateWorkoutRequest extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://54.180.163.5/volumn/update_Workout.php";
    private Map<String, String> map;


    public action_updateWorkoutRequest(String workoutID,String userEmail,String workoutName, String check_name, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);


        map = new HashMap<>();
        map.put("workout_ID",workoutID);
        map.put("workout_Name",workoutName);
        map.put("workout_Part", check_name);
        map.put("userEmail",userEmail);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
