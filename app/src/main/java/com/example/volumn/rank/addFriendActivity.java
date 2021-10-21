package com.example.volumn.rank;

import com.example.volumn.addSet.addSetAdapter;
import com.example.volumn.addSet.addSetRequest;

import org.json.JSONArray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.volumn.addWorkout.Model;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class addFriendActivity extends AppCompatActivity {


    public static Context context;
    ImageView img_back2;
    Button btn_request;
    EditText etxt_email;
   // EditText etxt_name;
    RecyclerView rv_friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        context = this;
        img_back2 = (ImageView) findViewById(R.id.img_back2);
        etxt_email = (EditText) findViewById(R.id.etxt_email_f);
        //etxt_name = (EditText) findViewById(R.id.etxt_name_f);
        btn_request = (Button) findViewById(R.id.btn_request);

        rv_friendList = (RecyclerView) findViewById(R.id.rv_friendList);

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //이메일
                String email = etxt_email.getText().toString().trim();
                //사용할 이름
               // String name = etxt_name.getText().toString().trim();

                if (email.equals("") ) {
                    return;
                }

                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 친구등록에 성공
                                Toast.makeText(context, "친구요청을 하였습니다.", Toast.LENGTH_SHORT).show();
                                onResume();
                                // finish();//액티비티 종료
                            } else { // 운동저장에 실패했습니다.
                                Toast.makeText(context, "이미 친구요청이 된 메일입니다.", Toast.LENGTH_SHORT).show();

                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                addFriendRequest addFriendRequest = new addFriendRequest(userEmail, etxt_email.getText().toString().trim(), responseListner);
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

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<dModel_Friend> friend_list = new ArrayList<>();

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("friend");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String ID = item.getString("ID");
                        String user_ID = item.getString("user_ID");
                        String userName = item.getString("userName");

                        String friend_ID = item.getString("friend_ID");
                        String friend_name = item.getString("friend_name");
                        String meID = item.getString("meID");

                        String state = "";
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject item_2 = jsonArray.getJSONObject(j);
                            String user_ID_2 = item_2.getString("user_ID");
                            String friend_ID_2 = item_2.getString("friend_ID");

                            if (user_ID.equals(friend_ID_2) && friend_ID.equals(user_ID_2)) {
                                state = "친구";

                            }

                        }


                        if (state.equals("") && user_ID.equals(meID) ) {
                            state = "수락대기";

                        } else if (state.equals("") && !user_ID.equals(meID) ) {
                            state = "친구요청";
                            friend_ID = user_ID;
                            friend_name =userName;
                        }
                        //String email = item.getString("email");

                        if(state.equals("친구") && !user_ID.equals(meID))
                        {

                        }else
                        {
                            dModel_Friend model = new dModel_Friend(friend_name, state,friend_ID ,ID);// 모델 객체 생성

                            friend_list.add(model);
                        }


                    }
                    if (friend_list != null) {
                        addFriendAdapter adapter = new addFriendAdapter(context, friend_list);

                        rv_friendList.setAdapter(adapter);
                        rv_friendList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        addFriend_List_Request addFriend_list_request = new addFriend_List_Request(userEmail, responseListner);
        RequestQueue queue = Volley.newRequestQueue(addFriendActivity.this);
        queue.add(addFriend_list_request);
    }
}