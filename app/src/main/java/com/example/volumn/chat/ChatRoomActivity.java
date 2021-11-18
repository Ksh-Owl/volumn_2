package com.example.volumn.chat;

import com.example.volumn.MainActivity;
import com.example.volumn.addSet.addSetRequest;
import com.example.volumn.addWorkout.Model;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.volumn.chat.chatRoom_Model;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    Context context;

    public BufferedReader in;
    public DataOutputStream out;
    public Socket s;
    Button btn_newRoom;
    RecyclerView rv_room;


    //서비스
    private Messenger mService;
    private final Messenger mMessenger = new Messenger(new IncomingHandelr());

    private class IncomingHandelr extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {

            //메시지 값에 따라
            //서비스에서 메시지 오면 반응
            switch(msg.what){
                case  ChatService.MSG_RESPONSE:
                    Bundle bundle = msg.getData();
                    String response = bundle.getString("response");
                    Log.e("TAG","response:"+response);
                    if(response.equals("160")){
                        getRoomList();

                    }

                    //editText2.setText(response);


                    break;

                default:
                    super.handleMessage(msg);

            }

        }
    }
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            try{
                Message msg = Message.obtain(null,ChatService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        btn_newRoom = (Button)findViewById(R.id.btn_newRoom);
        rv_room = (RecyclerView)findViewById(R.id.rv_room);
        context = this;

        //소켓연결 후 방만들기 코드 보낸후 방생성 코드 받아오면 방 리스트 업데이트


        //디비에서 방리스트 및 방정보 받아오기(방이름, 방인원원)

        //방만들기 기능
        btn_newRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, pop_addRoom.class);
                startActivity(intent);


            }
        });

//        Intent intent = new Intent(getApplicationContext(),ChatService.class);
//
//        intent.putExtra("test","test991");
//
//        startService(intent);



    }

    @Override
    protected void onResume() {
        super.onResume();

        //데이터베이스에서 방정보 받아오기
        getRoomList();


    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService(new Intent(this,ChatService.class),conn, Context.BIND_AUTO_CREATE);

//        try{
//            Message msg =Message.obtain(null,ChatService.SEND);
//            Bundle bundle = msg.getData();
//            bundle.putString("send","Start");
//            msg.replyTo = mMessenger;
//
//            Log.e("TAG", "msg :"+msg);
//
//            mService.send(msg);
//        }catch (Exception e){
//            e.printStackTrace();
//
//        }
    }

    private void getRoomList(){



        Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("chat");
                            chatRoom_Model chatRoom_Model ;// 모델 객체 생성
                            ArrayList<chatRoom_Model> room_list = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);

                                String room_ID = item.getString("room_ID");
                                String room_nm = item.getString("room_nm");
                                String member = item.getString("member");
                                String mem_count = item.getString("mem_count");
                                String CREATE_DATE = item.getString("CREATE_DATE");

                                chatRoom_Model = new chatRoom_Model(room_ID, room_nm, member, mem_count,CREATE_DATE);// 모델 객체 생성

                                room_list.add(chatRoom_Model);
                            }

                            RoomAdapter  adapter = new RoomAdapter(room_list);
                            adapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                    String roomName = room_list.get(position).getRoom_nm();
                                    String room_ID = room_list.get(position).getRoom_ID();



                                    Intent intent = new Intent(context, MainChatActivity.class);
                                    intent.putExtra("roomName", roomName);
                                    intent.putExtra("room_ID", room_ID);

                                    startActivity(intent);

                                }
                            });
                            rv_room.setAdapter(adapter);
                            rv_room.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                //String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오

        getRoomRequest getRoomRequest = new getRoomRequest( responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRoomRequest);
    }
}

