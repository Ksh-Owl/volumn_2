package com.example.volumn.rank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.volumn.R;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class addFriendActivity extends AppCompatActivity {


    Context context;
    ImageView img_back2;
    Button btn_request;
    EditText etxt_email;
    EditText etxt_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        context = this;
        img_back2 = (ImageView) findViewById(R.id.img_back2);
        etxt_email = (EditText) findViewById(R.id.etxt_email_f);
        etxt_name = (EditText) findViewById(R.id.etxt_name_f);
        btn_request = (Button) findViewById(R.id.btn_request);

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //이메일
                String email = etxt_email.getText().toString().trim();
                //사용할 이름
                String name = etxt_name.getText().toString().trim();

                if(email.equals("") || name.equals(""))
                {
                    return;
                }

                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 친구등록에 성공
                                Toast.makeText(getApplicationContext(), "친구요청을 하였습니다.", Toast.LENGTH_SHORT).show();

                                // finish();//액티비티 종료
                            } else { // 운동저장에 실패했습니다.
                                //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                addFriendRequest addFriendRequest = new addFriendRequest(userEmail, etxt_email.getText().toString().trim(), etxt_name.getText().toString().trim(), responseListner);
                RequestQueue queue = Volley.newRequestQueue(addFriendActivity.this);
                queue.add(addFriendRequest);


            }
        });

        img_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}