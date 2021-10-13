package com.example.volumn.addSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class pop_addSetActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pop_add_set);

        Intent intent = getIntent();

        String set = intent.getStringExtra("set");
        String name = intent.getStringExtra("name");
        String Date = intent.getStringExtra("Date");
        String workout_ID = intent.getStringExtra("workout_ID");

        TextView txt_info  = findViewById(R.id.txt_info);
        Button btn_save = findViewById(R.id.btn_save);
        Button btn_exit = findViewById(R.id.btn_exit);
        EditText etxt_kg = findViewById(R.id.etxt_kg);
        EditText etxt_count = findViewById(R.id.etxt_count);



        txt_info.setText(name+" "+set+"세트");

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try{

                    Response.Listener<String> responseListner = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                boolean success = jsonObject.getBoolean("success");
                                if (success) { // 운동기록에 성공
                                    //Toast.makeText(getApplicationContext(),"운동저장이 완료되었습니다.",Toast.LENGTH_SHORT).show();

                                    finish();//액티비티 종료
                                } else { // 운동저장에 실패했습니다.
                                    //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                                    return;
                                }




                            }catch (JSONException e)
                            {
                                e.printStackTrace();
                            }

                        }
                    };
                    String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                    pop_addSetRequest pop_addSetRequest = new pop_addSetRequest(workout_ID,Date,set,etxt_kg.getText().toString().trim() ,etxt_count.getText().toString().trim(), responseListner);//날짜,운동ID
                    RequestQueue queue = Volley.newRequestQueue(pop_addSetActivity.this);
                    queue.add(pop_addSetRequest);

                }catch (Exception ex)
                {

                }


            }
        });




    }
}