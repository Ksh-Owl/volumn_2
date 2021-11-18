package com.example.volumn.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.addWorkout.addWorkoutActivity;
import com.example.volumn.addWorkout.addWorkoutRequest;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class pop_addRoom extends AppCompatActivity {

    Context context;

    public BufferedReader in;
    public DataOutputStream out;
    public Socket s;
    EditText etxt_roomName;
    TextView btn_newRoom2;

    String nickName;
    String title;
    //서비스
    private Messenger mService;
   // private final Messenger mMessenger = new Messenger(new IncomingHandelr());

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            try{
                Message msg = Message.obtain(null,ChatService.MSG_REGISTER_CLIENT);
                //msg.replyTo = mMessenger;
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
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this,ChatService.class),conn, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_pop_add_room);

        etxt_roomName = (EditText) findViewById(R.id.etxt_roomName);
        btn_newRoom2 = (TextView) findViewById(R.id.btn_newRoom2);
        context =this;
        btn_newRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = etxt_roomName.getText().toString();

                //방제목 입력 되었으면
                if (!title.equals("")) {

                    try{
                    Message msg =Message.obtain(null,ChatService.MSG_CREATE_ROOM);
                    Bundle bundle = msg.getData();
                    bundle.putString("send",title);
                    //msg.replyTo = mMessenger;

                    Log.e("TAG", "msg :"+msg);

                    mService.send(msg);

                    finish();//방만들고 팝업창 닫기
                     }catch (Exception e){
                    e.printStackTrace();

                    }

                    //서버에 방생성

                    //connect();

                } else {
                    Toast.makeText(v.getContext(), "방제목을 입력해 주세요", Toast.LENGTH_SHORT).show();

                }


                //DB에 방저장

                //스레드 및 정리

            }
        });


    }

    public void connect() {//(소켓)서버연결 요청

        if (nickName == null) {
            nickName = "방생성자";
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


                    String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기
                    nickName =userEmail;
                    sendMsg("150|" + nickName);//대화명 전달
                    //out: 메시지 보내기, 쓰기객체    클라이언트-----msg----->서버
                    sendMsg("160|" + title); //방제목을 서버에게 전달


                    while (true) {


                        String msg = in.readLine();//msg: 서버가 보낸 메시지
                        Log.v("", msg);

                        //msg==> "300|안녕하세요"  "160|자바방--1,오라클방--1,JDBC방--1"

                        String msgs[] = msg.split("\\|");

                        String protocol = msgs[0];
                        String memlist = msgs[1];

                        switch (protocol) {
                            //방만들때 200,175 받는다

                            case "175"://(대화방에서) 대화방 인원정보
                                //방생성완료

                                String myRoomInwons[] = msgs[1].split(",");
                                String Inwon = Integer.toString(myRoomInwons.length) ;


                                //대화방 퇴장
                                sendMsg("400|");

                               // finish();
                                break;

                            case "200"://대화방 입장

                                sendMsg("175|"); //(대화방에서)대화방 인원정보 요청


                                break;

                            case "400"://대화방 퇴장


                                break;

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

    public void sendMsg(String msg) {//서버에게 메시지 보내기
        // final String msgs = nickName + ":" + msg + "\n";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.write((msg + "\n").getBytes());

                    //   clientOut.writeUTF(msgs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }//sendMsg

    private void saveRoom(String room_nm, String member, String mem_count) {
        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    boolean success = jsonObject.getBoolean("success");
                    if (success) { // 운동기록에 성공
                      //  Toast.makeText(getApplicationContext(), "채팅방이 만들어졌습니다.", Toast.LENGTH_SHORT).show();
                        finish();//액티비티 종료
                    } else { // 운동삭제에 실패했습니다.
                      //  Toast.makeText(getApplicationContext(), "채팅방 만드는데 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();

                        return;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        // String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기


        addRoomRequest addRoomRequest = new addRoomRequest(room_nm, member, mem_count, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(addRoomRequest);
    }
}