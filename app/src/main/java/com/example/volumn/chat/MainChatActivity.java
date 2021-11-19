package com.example.volumn.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.addSet.setDataModel;
import com.example.volumn.include.ChatCount_PreferenceManager;
import com.example.volumn.include.Chat_PreferenceManager;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainChatActivity extends AppCompatActivity {

    Context context;
    //소켓 입출력객체
    BufferedReader in;
    private DataOutputStream out;

    //서비스
    private Messenger mService;
    private final Messenger mMessenger = new Messenger(new IncomingHandelr());

    private class IncomingHandelr extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {

            //메시지 값에 따라
            //서비스에서 메시지 오면 반응
            switch (msg.what) {
                case ChatService.MSG_RESPONSE:
                    Bundle bundle = msg.getData();
                    String response = bundle.getString("response");
                    Log.e("TAG", "MainChatActivity Response::" + response);

                    //editText2.setText(response);


                    break;
                case ChatService.MSG_SENDMSG:
                    Bundle bundle2 = msg.getData();
                    String title = bundle2.getString("title");
                    String response2 = bundle2.getString("response");

                    Log.e("TAG", "MainChatActivity Response::" + response2);
                    //ArrayList<String> Msg_list = new ArrayList<>();
                    if(title.equals(txt_nowRoom.getText().toString()))
                    {
                        clientMsg_list.add(response2);


                        adapter = new MsgAdapter(clientMsg_list);
                        adapter.notifyDataSetChanged();
                        //Toast.makeText(context, "메시지", Toast.LENGTH_SHORT).show();
                        rv_msgList.scrollToPosition(rv_msgList.getAdapter().getItemCount() - 1);
                    }


                    //editText2.setText(response);
                    //Chat_PreferenceManager.setChatArrayPref(getApplicationContext(),msgs[1],msgs[2]);


                    break;

                default:
                    super.handleMessage(msg);

            }

        }
    }

    //클라세팅
    ArrayList<String> clientMsg_list;

    TextView txt_nowRoom;//현재 방이름

    private String clientMsg;
    private String nickName;
    ImageView img_send;
    EditText etxt_msgBox;
    RecyclerView rv_msgList;
    MsgAdapter adapter;


    String room = null;//채팅방 이름
    String room_ID = null;//채팅방 ID
    String userEmail;

    Thread th;


    //핸들러 스레드가 메인ui 수정가능하도록

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {



                // rv_room.setAdapter(adapter);
                // rv_room.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

            }
            if (msg.what == 1) {
                //txt_nowRoom.setText(room);

            }
        }
    };
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, ChatService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                inRoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, ChatService.class), conn, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        context = this;

        img_send = findViewById(R.id.img_send);
        etxt_msgBox = findViewById(R.id.etxt_msgBox);
        rv_msgList = findViewById(R.id.rv_msgList);
        txt_nowRoom = findViewById(R.id.txt_nowRoom);

        Intent intent = getIntent();//인텐트 받아오기
        room = intent.getStringExtra("roomName");
        txt_nowRoom.setText(room);
        //읽지않은 메시지 카운트 삭제
        ChatCount_PreferenceManager.deleteString(context,room);


        room_ID = intent.getStringExtra("room_ID");

        userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        //  connect();
        String json  = Chat_PreferenceManager.getChatArrayPref(context,room);

        clientMsg_list = new ArrayList<>(); //메시지 리스트 객체생성

        if(json != null)
        {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i =0 ; i < jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);

                    String msg = item.getString("msg");
                    clientMsg_list.add(msg);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        adapter = new MsgAdapter(clientMsg_list);

        rv_msgList.setAdapter(adapter);
        rv_msgList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));


        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg_txt = etxt_msgBox.getText().toString();


                //메시지 보내기 서비스에 전달fdf
                try{
                    if(msg_txt.equals("")){msg_txt = " ";}
                    Message msg =Message.obtain(null,ChatService.MSG_MSG);
                    Bundle bundle = msg.getData();
                    bundle.putString("send",""+msg_txt);
                    mService.send(msg);
                    Log.e("TAG", "채팅메시지 서비스에 전달 :"+ ""+msg_txt);


                }catch (Exception e){
                    e.printStackTrace();
                }
                //sendMsg("300|" + msg);
                //sendMsg(msg);

                etxt_msgBox.setText("");
            }
        });


    }
    private void inRoom(){
        //방 들어왔다 서비스에 전달
        try{
            Message msg =Message.obtain(null,ChatService.MSG_IN_ROOM);
            Bundle bundle = msg.getData();
            bundle.putString("send",""+room);
            mService.send(msg);
            Log.e("TAG", "채팅방 입장 :"+ ""+userEmail);


        }catch (Exception e){
            e.printStackTrace();
        }
    }




    @Override
    public void finish() {
        super.finish();

        try {
            ChatCount_PreferenceManager.resetChatCount(context,room);//메시지 읽음 처리
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //sendMsg("400|");

        //방나가기
        //방인원수 업데이트트
        //outRoom(room_ID, userEmail);


    }


    private void inRoom(String room_ID, String addMem) {

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    boolean success = jsonObject.getBoolean("success");
                    if (success) { //방 인원수 업데이트
                        //Toast.makeText(getApplicationContext(),"운동저장이 완료되었습니다.",Toast.LENGTH_SHORT).show();

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
        //String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        inRoom_Request inRoom_Request = new inRoom_Request(room_ID, addMem, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(inRoom_Request);

    }

    private void outRoom(String room_ID, String addMem) {

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    boolean success = jsonObject.getBoolean("success");
                    if (success) { //방 인원수 업데이트
                        //Toast.makeText(getApplicationContext(),"운동저장이 완료되었습니다.",Toast.LENGTH_SHORT).show();

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
        //String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        outRoom_Request outRoom_Request = new outRoom_Request(room_ID, addMem, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(outRoom_Request);

    }

}