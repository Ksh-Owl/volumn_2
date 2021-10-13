package com.example.volumn.addSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.addWorkout.Model;
import com.example.volumn.addWorkout.addWorkoutActivity;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class addSetActivity extends AppCompatActivity {
    Button btn_add;
    TextView txt_choicedate;
    RecyclerView rv_addSet;
    String Date;
    public static Context CONTEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_set);

        CONTEXT = this;

        btn_add = (Button) findViewById(R.id.btn_add);
        txt_choicedate = (TextView) findViewById(R.id.txt_choicedate);

        Intent intent = getIntent();

        Date = intent.getStringExtra("date");

        txt_choicedate.setText(Date);


        rv_addSet = findViewById(R.id.rv_addSet);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //운동추가(운동종류추가 화면)
                Intent intent = new Intent(getApplicationContext(), addWorkoutActivity.class);//명시적 인텐트


                intent.putExtra("date", txt_choicedate.getText());

                startActivity(intent);


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = this;


        show_list(context);
    }

    public void show_list(Context context) {
        ArrayList<Model> choice_list = new ArrayList<>();

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("workout");

//                    if(jsonArray.length() <=0)
//                    {
//                        //운동추가(운동종류추가 화면)
//                        Intent intent = new Intent(context, addWorkoutActivity.class);//명시적 인텐트
//
//
//                        intent.putExtra("date", txt_choicedate.getText());
//
//                        startActivity(intent);
//
//                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String name = item.getString("workout_Name");
                        String workout_Part = item.getString("workout_Part");
                        String workout_ID = item.getString("workout_ID");
                        String SET_ID = item.getString("SET_ID");

                        Model model = new Model(name, workout_Part, workout_ID, SET_ID);// 모델 객체 생성

                        choice_list.add(model);// 모델넣기

                    }


                    if (choice_list != null) {
                        addSetAdapter adapter = new addSetAdapter(context, choice_list, Date);

                        rv_addSet.setAdapter(adapter);
                        rv_addSet.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        addSetRequest addSetRequest = new addSetRequest(userEmail, txt_choicedate.getText().toString(), responseListner);
        RequestQueue queue = Volley.newRequestQueue(addSetActivity.this);
        queue.add(addSetRequest);
    }

}