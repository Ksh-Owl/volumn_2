package com.example.volumn.chat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.include.ChatCount_PreferenceManager;
import com.example.volumn.include.PreferenceManager;
import com.example.volumn.include.Chat_PreferenceManager;
import com.example.volumn.include.myRoom_PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ChatService extends Service {

    //채팅 클라이언트 세팅
    private String nickName;
 //   ArrayList<String> clientMsg_list;
   // ArrayList<chat_msgCountModel> chat_msgCount_list;


    String room = null;//채팅방 이름
    String room_ID = null;//채팅방 ID
    String userEmail ;

    MsgAdapter adapter;

    //소켓 입출력객체
    BufferedReader in;
    private DataOutputStream out;
    TextView txt_nowRoom;//현재 방이름

    //


    private ArrayList<Messenger> clientList = new ArrayList<>();
    static final int MSG_REGISTER_CLIENT = 44; //클라이언트 추가
    static final int MSG_UNREGISTER_CLIENT = 56;
    static final int MSG_CREATE_ROOM = 60;//방만들기
    static final int MSG_IN_ROOM = 61;//방들어오기
    static final int MSG_OUT_ROOM = 62;//방나가기

    static final int MSG_SET_VALUE = 77;


    public static final int MSG_MSG = 100;//채팅 메시지 받기

    static final int SEND = 11;
    private String send = null;



    //클라이언트에 보내는 Message의 what에 해당하는 값
    public static final int MSG_RESPONSE = 12;

    public static final int MSG_SENDMSG = 16;//채팅 메시지 보내기

    int mValue = 0;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    clientList.add(msg.replyTo);

                    break;


                case MSG_UNREGISTER_CLIENT:
                    clientList.remove(msg.replyTo);
                    break;
                case SEND:
                    Bundle bundle = msg.getData();
                    send = bundle.getString("send");
                    Log.e("TAG", "ServicewithMessenger/send:" + bundle.getString("send"));

                    //recive();
                    break;
                case MSG_CREATE_ROOM:
                    Bundle bundle_CREAT_ROOM = msg.getData();
                    send = bundle_CREAT_ROOM.getString("send");
                    Log.e("TAG", "MSG_CREATE_ROOM/send:" + bundle_CREAT_ROOM.getString("send"));
                    sendMsg("160|" + send); //방제목을 서버에게 전달
                    //db에 저장
                    saveRoom(send, ""+userEmail, "1");
                    break;
                case MSG_IN_ROOM:
                    Bundle bundle_IN_ROOM = msg.getData();
                    send = bundle_IN_ROOM.getString("send");
                    Log.e("TAG", "MSG_IN_ROOM/send:" + bundle_IN_ROOM.getString("send"));
                    sendMsg("200|" + send); //방제목을 서버에게 전달
                    //db에 저장
                    //saveRoom(send, ""+userEmail, "1");
                    break;
                case MSG_MSG:
                    Bundle bundle_MSG = msg.getData();
                    send = bundle_MSG.getString("send");
                    Log.e("TAG", "MSG_MSG/send:" + bundle_MSG.getString("send"));
                    sendMsg("300|" + send); //채팅메시지를 서버에게 전달
                    //db에 저장
                    //saveRoom(send, ""+userEmail, "1");
                    break;

                default:

                    super.handleMessage(msg);

                    break;
            }

        }
    }


    private Messenger messenger = new Messenger(new IncomingHandler());

    public ChatService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오
        connect();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();

    }

    public void connect() {//(소켓)서버연결 요청

        if (nickName == null) {
            nickName = "클라이언트";
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                  //  chat_msgCount_list = new ArrayList<>(); //새로온 메시지 리스트에 넣고 쉐어드에 저장


                    //Socket s = new Socket(String host<서버ip>, int port<서비스번호>);

                    Socket s = new Socket("13.209.66.177", 5000);//연결시도
                    Log.v("", "클라이언트 : 서버 연결됨.");

                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //in: 서버메시지 읽기객체    서버-----msg------>클라이언트

                    out = new DataOutputStream(s.getOutputStream());
                    //out: 메시지 보내기, 쓰기객체    클라이언트-----msg----->서버


                    nickName = userEmail;

                    sendMsg("100|" + nickName);//(대기실)접속 알림

                    sendMsg("150|");//대화명 전달
                    Log.v("", "클라이언트 : 메시지 전송완료");



                    //기존에 입장되어있는 방에 입장
                    String json =  myRoom_PreferenceManager.getString(getApplicationContext(),"ROOM");
                    if(json !=null)
                    {
                        JSONArray jsonArray = new JSONArray(json);
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject item = jsonArray.getJSONObject(i);
                            String room_name = item.getString("value");

                            sendMsg("200|" + room_name); //방제목을 서버에게 전달


                        }


                    }

                    //  sendMsg("200|" + room);
                    //txt_nowRoom.setText(room);


                    //데이터 베이스 룸 업데이트
                    //inRoom(room_ID,userEmail);


                    //clientMsg_list = new ArrayList<>();
                    //adapter = new MsgAdapter(clientMsg_list);

                    //rv_msgList.setAdapter(adapter);
                    //rv_msgList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

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



                                try {
                                    //클라이언트에게 전송
                                    if (clientList.size() > 0) {

                                        boolean save_check = false;

                                        for (int i = 0; i < clientList.size(); i++){
                                            Message message = Message.obtain(null, MSG_SENDMSG);
                                            Bundle bundle = message.getData();
                                            bundle.putString("response", ""+msgs[3]);

                                            bundle.putString("time", ""+msgs[2]);
                                            bundle.putString("title", ""+msgs[1]);
                                            clientList.get(i).send(message);

                                            if(!save_check){


                                                ChatCount_PreferenceManager.setChatCount(getApplicationContext(),msgs[1],msgs[3]);
                                                Chat_PreferenceManager.setChatArrayPref(getApplicationContext(),msgs[1],msgs[3],msgs[2]);
                                                save_check = true;

                                                //노티피케이션
                                                //createNotification(getApplicationContext());

                                            }
                                            //chat_msgCountModel msgCountModel = new chat_msgCountModel(msgs[1],msgs[2]);




                                        }

                                    }


                                } catch (Exception e) {
                                    //  clientList.remove(i);
                                    e.printStackTrace();
                                }
                                break;


                            case "160"://방만들기
                                try {
                                    //클라이언트에게 전송
                                    if (clientList.size() > 0) {
                                        Message message = Message.obtain(null, MSG_RESPONSE);
                                        Bundle bundle = message.getData();
                                        bundle.putString("response", "160");
                                        clientList.get(0).send(message);
                                    }


                                } catch (Exception e) {
                                    //  clientList.remove(i);
                                    e.printStackTrace();
                                }

                                break;

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

                } catch (JSONException e) {
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

    private void saveRoom(String room_nm, String member, String mem_count) {
        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    boolean success = jsonObject.getBoolean("success");
                    if (success) { // 운동기록에 성공
                        //  Toast.makeText(getApplicationContext(), "채팅방이 만들어졌습니다.", Toast.LENGTH_SHORT).show();
                        //finish();//액티비티 종료
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
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(addRoomRequest);
    }

    private void createNotification(Context context ){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("알림 제목");
        builder.setContentText("알람 세부 텍스트");

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }
}
