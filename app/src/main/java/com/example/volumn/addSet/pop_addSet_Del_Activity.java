package com.example.volumn.addSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class pop_addSet_Del_Activity extends AppCompatActivity {

    Button btn_del;
    Button btn_exit;
    TextView txt_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_add_set_del);


        btn_del = (Button) findViewById(R.id.btn_del);
        btn_exit = (Button) findViewById(R.id.btn_exit);
        txt_name = (TextView) findViewById(R.id.txt_name);


        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String SET_ID = intent.getStringExtra("SET_ID");

        txt_name.setText(name);


        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //삭제 처리


                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 운동기록에 성공
                                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();


                            } else { // 운동삭제에 실패했습니다.
                                Toast.makeText(getApplicationContext(), "삭제중에 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();

                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };
                String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                addSetRequest_del addSetRequest_del = new addSetRequest_del(SET_ID, responseListner);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(addSetRequest_del);


            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }
}