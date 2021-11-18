package com.example.volumn.chat;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.addSet.setDataModel;
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
                    String response2 = bundle2.getString("response");
                    Log.e("TAG", "MainChatActivity Response::" + response2);
                    clientMsg_list.add(response2);
                    adapter = new MsgAdapter(clientMsg_list);
                    adapter.notifyDataSetChanged();
                    //Toast.makeText(context, "메시지", Toast.LENGTH_SHORT).show();
                    rv_msgList.scrollToPosition(rv_msgList.getAdapter().getItemCount() - 1);

                    //editText2.setText(response);


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

        img_send = findViewById(R.id.img_send);
        etxt_msgBox = findViewById(R.id.etxt_msgBox);
        rv_msgList = findViewById(R.id.rv_msgList);
        txt_nowRoom = findViewById(R.id.txt_nowRoom);

        Intent intent = getIntent();//인텐트 받아오기
        room = intent.getStringExtra("roomName");
        room_ID = intent.getStringExtra("room_ID");
        context = this;

        userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        //  connect();


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

        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg_txt = etxt_msgBox.getText().toString();


                //메시지 보내기 서비스에 전달
                try{
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


    public void connect() {//(소켓)서버연결 요청

        if (nickName == null) {
            nickName = "클라이언트";
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //Socket s = new Socket(String host<서버ip>, int port<서비스번호>);

                    Socket s = new Socket("13.209.66.177", 5000);//연결시도
                    Log.v("", "클라이언트 : 서버 연결됨.");

                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //in: 서버메시지 읽기객체    서버-----msg------>클라이언트

                    out = new DataOutputStream(s.getOutputStream());
                    //out: 메시지 보내기, 쓰기객체    클라이언트-----msg----->서버


                    Log.v("", "클라이언트 : 메시지 전송완료");
                    sendMsg("100|");//(대기실)접속 알림

                    nickName = userEmail;
                    sendMsg("150|" + nickName);//대화명 전달
                    sendMsg("200|" + room);
                    txt_nowRoom.setText(room);


                    //데이터 베이스 룸 업데이트
                    inRoom(room_ID, userEmail);


                    clientMsg_list = new ArrayList<>();
                    adapter = new MsgAdapter(clientMsg_list);

                    rv_msgList.setAdapter(adapter);
                    rv_msgList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

                    //방에 접속


                    while (true) {

                        if (room != null) {
                            room = null;
                        }


                        String msg = in.readLine();//msg: 서버가 보낸 메시지
                        Log.v("", msg);

                        //msg==> "300|안녕하세요"  "160|자바방--1,오라클방--1,JDBC방--1"

                        String msgs[] = msg.split("\\|");

                        String protocol = msgs[0];

                        switch (protocol) {

                            case "300":
                                //메시지 받기
                                //메시지를 adapter에 넣는다


                                //  etxt_msgBox.setText(msgs[1]);

                                clientMsg_list.add(msgs[1]);
                                adapter = new MsgAdapter(clientMsg_list);

                                handler.sendEmptyMessage(0);


//                                cc.ta.append(msgs[1]+"\n");
//
//                                cc.ta.setCaretPosition(cc.ta.getText().length());

                                break;


//                            case "160"://방만들기
//
//                                //방정보를 List에 뿌리기
//
//                                if (msgs.length > 1) {
//
//                                    //개설된 방이 한개 이상이었을때 실행
//
//                                    //개설된 방없음 ---->  msg="160|" 였을때 에러
//
//                                    String roomNames[] = msgs[1].split(",");
//
//                                    //"자바방--1,오라클방--1,JDBC방--1"
//                                    //받아온 방이름 어레이리스트에 넣기
//                                    room_list = new ArrayList<>();
//
//                                    for (int i = 0; i < roomNames.length; i++) {
//
//                                        room_list.add(roomNames[i]);
//
//                                    }
//                                   // adapter = new RoomAdapter(room_list);
//
//                                    adapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
//                                        @Override
//                                        public void onItemClick(View v, int position) {
//
//                                            room = room_list.get(position);
//                                            String selectedRoom = room.substring(0,room.indexOf("-"));
//
//                                            sendMsg("200|" + selectedRoom);
//                                            //txt_nowRoom.setText(room);
//
//                                            handler.sendEmptyMessage(1);
//
//                                            //   sendMsg("175|");//대화방내 인원정보 요청
//
//
//                                        }
//                                    });
//
//
//                                    handler.sendEmptyMessage(0);
//                                    //rv_room.setAdapter(adapter);
//
//                                    // roomInfo.setListData(roomNames);
//
//                                }
//
//                                break;
//
//
//
                            case "170"://(대기실에서) 대화방 인원정보

                                String roomInwons[] = msgs[1].split(",");

                                //roomInwon.setListData(roomInwons);

                                break;


//
                            case "175"://(대화방에서) 대화방 인원정보

                                String myRoomInwons[] = msgs[1].split(",");

//                                cc.li_inwon.setListData(myRoomInwons);

                                break;

//                            case "180"://대기실 인원정보
//
//                                String waitNames[] = msgs[1].split(",");
//
//                                waitInfo.setListData(waitNames);
//
//                                break;

                            case "200"://대화방 입장

//                                cc.ta.append("=========["+msgs[1]+"]님 입장=========\n");
//
//                                cc.ta.setCaretPosition(cc.ta.getText().length());

                                break;
//
//
//
                            case "400"://대화방 퇴장

//                                cc.ta.append("=========["+msgs[1]+"]님 퇴장=========\n");
//
//                                cc.ta.setCaretPosition(cc.ta.getText().length());

                                break;
//
//
//
                            case "202"://개설된 방의 타이틀 제목 얻기

                                //cc.setTitle("채팅방-["+msgs[1]+"]");

                                break;


                        }//클라이언트 switch

                    }
                } catch (UnknownHostException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }
        }).start();


    }//connect

    public void sendMsg(String msg) {//서버에 메시지 보내기

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.write((msg + "\n").getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }//sendMsg

    @Override
    public void finish() {
        super.finish();

        sendMsg("400|");

        //방나가기
        //방인원수 업데이트트
        outRoom(room_ID, userEmail);


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