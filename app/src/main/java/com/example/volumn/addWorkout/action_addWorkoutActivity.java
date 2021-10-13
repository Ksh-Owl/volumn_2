package com.example.volumn.addWorkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class action_addWorkoutActivity extends AppCompatActivity {

    Button btn_addWorkout;//운동신규추가
    Button btn_delWorkout;//운동삭제
    Button btn_update;//운동업데이트
    EditText txt_workoutName;

    CheckBox ch_chest;
    CheckBox ch_shoulder;
    CheckBox ch_back;
    CheckBox ch_lowbody;
    CheckBox ch_biceps;
    CheckBox ch_triceps;
    CheckBox ch_hip;
    CheckBox ch_trapezius;
    CheckBox ch_abs;

    String check_name;//선택한 운동부위
    String workout_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_add_workout);

        Intent intent = getIntent();


         btn_addWorkout = (Button) this.findViewById(R.id.btn_addWorkout);//운동신규추가
         btn_update = (Button) this.findViewById(R.id.btn_update);//운동업데이트
        btn_delWorkout = (Button) this.findViewById(R.id.btn_delWorkout);//운동삭제


         txt_workoutName = (EditText) this.findViewById(R.id.txt_info);

        ch_chest = (CheckBox) findViewById(R.id.ch_chest);
        ch_shoulder = (CheckBox) findViewById(R.id.ch_shoulder);
        ch_back = (CheckBox) findViewById(R.id.ch_back);
        ch_lowbody = (CheckBox) findViewById(R.id.ch_lowbody);
        ch_biceps = (CheckBox) findViewById(R.id.ch_biceps);
        ch_triceps = (CheckBox) findViewById(R.id.ch_triceps);
        ch_hip = (CheckBox) findViewById(R.id.ch_hip);
        ch_trapezius = (CheckBox) findViewById(R.id.ch_trapezius);
        ch_abs = (CheckBox) findViewById(R.id.ch_abs);

        btn_addWorkout.setVisibility(View.VISIBLE);

        btn_delWorkout.setVisibility(View.GONE);
        btn_update.setVisibility(View.GONE);

        //인텐트 값 받아오기
        if(intent != null)
        {
            txt_workoutName.setText(intent.getStringExtra("workout_Name"));
            workout_ID = intent.getStringExtra("workout_ID");
            String workout_Part = intent.getStringExtra("workout_Part");

            if(workout_Part != null  && !workout_Part.equals("")){
                btn_delWorkout.setVisibility(View.VISIBLE);
                btn_update.setVisibility(View.VISIBLE);

                btn_addWorkout.setVisibility(View.GONE);

                switch (workout_Part)
                {
                    case "가슴":
                        check_name = "가슴";
                        ch_chest.setChecked(true);


                        //체크 해제

                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case "어깨":
                        check_name = "어깨";
                        ch_shoulder.setChecked(true);

                        //체크 해제
                        ch_chest.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case "등":
                        check_name = "등";
                        ch_back.setChecked(true);

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case "하체":
                        check_name = "하체";
                        ch_lowbody.setChecked(true);

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case "이두":
                        check_name = "이두";
                        ch_biceps.setChecked(true);

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case "삼두":
                        check_name = "삼두";
                        ch_triceps.setChecked(true);

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case "엉덩이":
                        check_name = "엉덩이";
                        ch_hip.setChecked(true);

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case "승모근":
                        check_name = "승모근";
                        ch_trapezius.setChecked(true);

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case "복근":
                        check_name = "복근";
                        ch_abs.setChecked(true);

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);

                        break;
                }

            }
        }


        View.OnClickListener Check = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.ch_chest:
                        check_name = "가슴";
                        //체크 해제
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case R.id.ch_shoulder:
                        check_name = "어깨";

                        //체크 해제
                        ch_chest.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case R.id.ch_back:
                        check_name = "등";

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        //ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case R.id.ch_lowbody:
                        check_name = "하체";

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        //ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case R.id.ch_biceps:
                        check_name = "이두";

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        //ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case R.id.ch_triceps:
                        check_name = "삼두";

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        //ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case R.id.ch_hip:
                        check_name = "엉덩이";

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        //ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case R.id.ch_trapezius:
                        check_name = "승모근";

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        //ch_trapezius.setChecked(false);
                        ch_abs.setChecked(false);

                        break;
                    case R.id.ch_abs:
                        check_name = "복근";

                        //체크 해제

                        ch_chest.setChecked(false);
                        ch_shoulder.setChecked(false);
                        ch_back.setChecked(false);
                        ch_lowbody.setChecked(false);
                        ch_biceps.setChecked(false);
                        ch_triceps.setChecked(false);
                        ch_hip.setChecked(false);
                        ch_trapezius.setChecked(false);
                        //ch_abs.setChecked(false);

                        break;


                }
            }
        };
        ch_chest.setOnClickListener(Check);
        ch_shoulder.setOnClickListener(Check);
        ch_back.setOnClickListener(Check);
        ch_lowbody.setOnClickListener(Check);
        ch_biceps.setOnClickListener(Check);
        ch_triceps.setOnClickListener(Check);
        ch_hip.setOnClickListener(Check);
        ch_trapezius.setOnClickListener(Check);
        ch_abs.setOnClickListener(Check);


        //저장했을때 쉐어드에 저장된 아이디로 운동 등록

        btn_addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workoutName = txt_workoutName.getText().toString();




                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 운동기록에 성공
                                Toast.makeText(getApplicationContext(),"운동저장이 완료되었습니다.",Toast.LENGTH_SHORT).show();

                                finish();//액티비티 종료
                            } else { // 운동저장에 실패했습니다.
                                Toast.makeText(getApplicationContext(),"중복된 운동 입니다.",Toast.LENGTH_SHORT).show();

                                return;
                            }




                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                };
                String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                action_addWorkoutRequest action_addWorkoutRequest = new action_addWorkoutRequest(userEmail,workoutName, check_name, responseListner);
                RequestQueue queue = Volley.newRequestQueue(action_addWorkoutActivity.this);
                queue.add(action_addWorkoutRequest);


            }
        });
        btn_delWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workoutName = txt_workoutName.getText().toString();




                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 운동기록에 성공
                                Toast.makeText(getApplicationContext(),"운동삭제가 완료되었습니다.",Toast.LENGTH_SHORT).show();

                                finish();//액티비티 종료
                            } else { // 운동삭제에 실패했습니다.

                                return;
                            }




                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                };
                String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                action_delWorkoutRequest delWorkoutRequest = new action_delWorkoutRequest(userEmail,workoutName, check_name, responseListner);
                RequestQueue queue = Volley.newRequestQueue(action_addWorkoutActivity.this);
                queue.add(delWorkoutRequest);

            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workoutName = txt_workoutName.getText().toString();




                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 운동기록에 성공
                                Toast.makeText(getApplicationContext(),"운동수정이 완료되었습니다.",Toast.LENGTH_SHORT).show();

                                finish();//액티비티 종료
                            } else { // 운동삭제에 실패했습니다.
                                Toast.makeText(getApplicationContext(),"중복된 운동 입니다.",Toast.LENGTH_SHORT).show();

                                return;
                            }




                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                };
                String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                action_updateWorkoutRequest updateWorkoutRequest = new action_updateWorkoutRequest(workout_ID,userEmail,workoutName, check_name, responseListner);
                RequestQueue queue = Volley.newRequestQueue(action_addWorkoutActivity.this);
                queue.add(updateWorkoutRequest);

            }
        });

    }
}