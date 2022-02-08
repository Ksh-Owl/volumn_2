package com.example.volumn.chat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.include.ChatCount_PreferenceManager;
import com.example.volumn.include.PreferenceManager;
import com.example.volumn.include.Chat_PreferenceManager;
import com.example.volumn.include.myRoom_PreferenceManager;
import com.google.gson.JsonArray;

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
    String userEmail;

    MsgAdapter adapter;

    //소켓 입출력객체
    BufferedReader in;
    private DataOutputStream out;

    //


    private ArrayList<Messenger> clientList = new ArrayList<>();
    private ArrayList<Messenger> chatRoom_clientList = new ArrayList<>();

    static final int MSG_CHATROOM_CLIENT = 43; //클라이언트 추가

    static final int MSG_REGISTER_CLIENT = 44; //클라이언트 추가
    static final int MSG_UNREGISTER_CLIENT = 56;
    static final int MSG_CREATE_ROOM = 60;//방만들기
    static final int MSG_IN_ROOM = 61;//방들어오기
    static final int MSG_OUT_ROOM = 62;//방나가기
    static final int MSG_READ_LINE = 63;//여기까지 읽었습니다.

    static final int MSG_NO_READ_COUNT = 65;//읽지않은 메시지 리스트
    static final int MSG_REQUEST_NO_READ_COUNT = 66;//읽지않은 메시지 리스트 요청
    static final int MSG_YES_READ = 67;//메시지 읽음처리

    static final int MSG_MAINCHAT = 68;//채팅 화면에서 나옴


    static final int MSG_MSGLIST = 70;//메시지 리스트
    static final int MSG_PAGEING_MSGLIST = 71;//페이징메시지 리스트


    static final int MSG_SET_VALUE = 77;


    public static final int MSG_MSG = 100;//채팅 메시지 받기

    static final int SEND = 11;
    private String send = null;
    private String MainChat = "";
    private String NowRoom_name = "";


    private int limit = 20;
    private int page = 1;


    //클라이언트에 보내는 Message의 what에 해당하는 값
    public static final int MSG_RESPONSE = 12;

    public static final int MSG_SENDMSG = 16;//채팅 메시지 보내기

    int mValue = 0;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_CHATROOM_CLIENT:
                    Log.e("TAG", "msg.replyTo:" + msg.replyTo);
                    chatRoom_clientList.clear();
                    chatRoom_clientList.add(msg.replyTo);

                    // clientList.add(msg.replyTo);

                    break;


                case MSG_REGISTER_CLIENT:

                    Log.e("TAG", "msg.replyTo:" + msg.replyTo);

                    clientList.clear();

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
                    try {
                        Bundle bundle_CREAT_ROOM = msg.getData();
                        send = bundle_CREAT_ROOM.getString("send");
                        Log.e("TAG", "MSG_CREATE_ROOM/send:" + bundle_CREAT_ROOM.getString("send"));
                        sendMsg("160|" + send); //방제목을 서버에게 전달
                        Log.e("TAG", "방만들기 방제목 서버에 보냄 :" + send);

                        //db에 저장
                        saveRoom(send, "" + userEmail, "1");

                        myRoom_PreferenceManager.setString(getApplicationContext(), "ROOM", send);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                case MSG_PAGEING_MSGLIST://채팅창에서 스크롤 하면

                    Log.e("TAG", "메시지 : MSG_PAGEING_MSGLIST 받음");

                    Bundle bundle_PAGEING_MSGLIST = msg.getData();
                    String title = bundle_PAGEING_MSGLIST.getString("title");

                    page = Integer.parseInt(bundle_PAGEING_MSGLIST.getString("page"));
                    limit = Integer.parseInt(bundle_PAGEING_MSGLIST.getString("limit"));

                    String json_PAGEING = Chat_PreferenceManager.getChatArrayPref(getApplicationContext(), title);

                    try {
                        if (clientList.size() > 0 || chatRoom_clientList.size() > 0) {

                            //   boolean save_check = false;
                            JSONArray jsonArray_send = new JSONArray();
                            Message message = Message.obtain(null, MSG_PAGEING_MSGLIST);
                            Bundle bundle_PAGEING_LIST = message.getData();
                            bundle_PAGEING_LIST.putString("title", title);


                            int k = 0;
                            //페이징 처리
                            if (json_PAGEING != null) {
                                JSONArray jsonArray = new JSONArray(json_PAGEING);

                                if (jsonArray.length() - (limit * page) < 0) {
                                    //페이징 끝
                                    Log.e("TAG", "페이징 중단 페이지 :" + page);

                                    if ((jsonArray.length() - (limit * (page - 1))) > 0) { //남아있는 데이터 있음
                                        Log.e("TAG", "남아있는 개수 :" + (jsonArray.length() - (limit * (page - 1))));

                                        int left = (jsonArray.length() - (limit * (page - 1)));//남아있다  limit -

                                        for (int L = 0; L < left; L++) {
                                            try {
                                                JSONObject item = jsonArray.getJSONObject(L);
                                                Log.e("TAG", "여기서부터 :" + 0);
                                                Log.e("TAG", "여기까지 :" + left);
                                                jsonArray_send.put(item);
                                            } catch (Exception e) {
                                                Log.e("TAG", "페이징 오류 발생 :" + L);
                                                Log.e("TAG", "jsonArray 크기 :" + jsonArray.length());


                                            }
                                        }

                                    } else {

                                        return;
                                    }


                                } else {
                                    for (int j = jsonArray.length() - (limit * page); j < jsonArray.length() - (limit * (page - 1)); j++) {
                                        k++;
                                        Log.e("TAG", "여기서부터 :" + (jsonArray.length() - (limit * page)));
                                        Log.e("TAG", "여기까지 :" + (jsonArray.length() - (limit * (page - 1))));
                                        Log.e("TAG", "현재 :" + j + " 몇개 출력하지 :" + k);

                                        try {
                                            JSONObject item = jsonArray.getJSONObject(j);

                                            jsonArray_send.put(item);
                                        } catch (Exception e) {
                                            Log.e("TAG", "페이징 오류 발생 :" + j);
                                            Log.e("TAG", "jsonArray 크기 :" + jsonArray.length());


                                        }


                                    }
                                }

                                Log.e("TAG", "메시지 :" + jsonArray_send.toString());


                                bundle_PAGEING_LIST.putString("response", jsonArray_send.toString());

                                chatRoom_clientList.get(0).send(message);

                                for (int i = 0; i < clientList.size(); i++) {
                                    try {
                                        clientList.get(i).send(message);

                                        Log.e("서비스에서 메시지리스트 보냄", "");

                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }

                            //bundle_MSGLIST.putString("time", "");


                        }
                    } catch (Exception e) {

                    }


                    break;
                case MSG_IN_ROOM:
                    Bundle bundle_IN_ROOM = msg.getData();
                    send = bundle_IN_ROOM.getString("send");

                    MainChat = bundle_IN_ROOM.getString("MainChat");
                    NowRoom_name = send;

                    page = Integer.parseInt(bundle_IN_ROOM.getString("page"));
                    limit = Integer.parseInt(bundle_IN_ROOM.getString("limit"));

                    try {


                        myRoom_PreferenceManager.setString(getApplicationContext(), "ROOM", send);
                        Log.e("TAG", "입장한 방 등록:" + send);

                        //읽음이 마지막이면 제거
                        try {

                            //메시지 리스트 전달
                            String json = Chat_PreferenceManager.getChatArrayPref(getApplicationContext(), send);
                            if(json !=null)
                            {
                                JSONArray jsonArray = new JSONArray(json);
                                Chat_PreferenceManager.deleteString(getApplicationContext(), send);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject item = jsonArray.getJSONObject(i);

                                    String msg_ = item.getString("msg");
                                    String time = item.getString("time");
                                    String img_id = "";

                                    try {
                                        img_id = item.getString("img_id");

                                    }catch (Exception e){

                                    }

                                    String data_[] = msg_.split("▶");

                                    // String msgs[] = msg.split("▶");
                                    if (!data_[0].equals("읽음")) { //읽음이 아니면
                                        Log.e("TAG", "메시지 저장 :" + data_[0]);
                                        //메시지 저장
                                        Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), send, msg_, time, img_id);

                                    }//읽음이 아닌 메시지 만 다시저장
                                    else {//읽음 ==  여기까지 읽었습니다.
                                        //읽음인데 마지막인경우
                                        if(i+1 == jsonArray.length())
                                        {
                                            //마지막일 경우

                                        }else {
                                            //마지막이 아닐경우
                                            String noti = "읽음▶=========여기까지 읽었습니다.=========";

                                            try {
                                                //if(!MainChat.equals("MainChat")){
                                                Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), send, noti, "", "");
                                                //}

                                                //여기까지 읽었습니다. 저장되는 위치
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (clientList.size() > 0) {




                            //   boolean save_check = false;
                            Message message = Message.obtain(null, MSG_MSGLIST);
                            Bundle bundle_MSGLIST = message.getData();
                            bundle_MSGLIST.putString("title", send);


                            JSONArray jsonArray_send = new JSONArray();

                            //메시지 리스트 전달
                            String json = Chat_PreferenceManager.getChatArrayPref(getApplicationContext(), send);
                            if (json != null) {
                                JSONArray jsonArray = new JSONArray(json);
                                int k = 0;
                                if (jsonArray.length() - (limit * page) < 0) {
                                    //페이징 끝
                                    Log.e("TAG", "페이징 중단 페이지 :" + page);

                                    if ((jsonArray.length() - (limit * (page - 1))) > 0) { //남아있는 데이터 있음
                                        Log.e("TAG", "남아있는 개수 :" + (jsonArray.length() - (limit * (page - 1))));

                                        int left = (jsonArray.length() - (limit * (page - 1)));//남아있다 limit -

                                        for (int L = 0; L < left; L++) {
                                            try {
                                                JSONObject item = jsonArray.getJSONObject(L);
                                                Log.e("TAG", "여기서부터 :" + 0);
                                                Log.e("TAG", "여기까지 :" + left);
                                                jsonArray_send.put(item);

                                            } catch (Exception e) {
                                                Log.e("TAG", "페이징 오류 발생 :" + L);
                                                Log.e("TAG", "jsonArray 크기 :" + jsonArray.length());


                                            }
                                        }
                                    } else {
                                        return;
                                    }

                                } else {
                                    for (int j = jsonArray.length() - (limit * page); j < jsonArray.length() - (limit * (page - 1)); j++) {
                                        k++;
                                        Log.e("TAG", "여기서부터 :" + (jsonArray.length() - (limit * page)));
                                        Log.e("TAG", "여기까지 :" + (jsonArray.length() - (limit * (page - 1))));
                                        Log.e("TAG", "현재 :" + j + " 몇개 출력하지 :" + k);

                                        JSONObject item = jsonArray.getJSONObject(j);

                                        jsonArray_send.put(item);

                                    }

                                }


                                Log.e("TAG", "메시지 :" + jsonArray_send.toString());

                                bundle_MSGLIST.putString("response", jsonArray_send.toString());

                            }
                            try {

                                // chatRoom_clientList.get(0).send(message);

                                for (int i = 0; i < clientList.size(); i++) {

                                    if (clientList.size() > 0) {
                                        clientList.get(i).send(message);
                                        Log.e("서비스에서 메시지리스트 보냄", "");

                                    }


                                }


                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("TAG", "MSG_IN_ROOM/send:" + bundle_IN_ROOM.getString("send"));

                    sendMsg("200|" + send); //방제목을 서버에게 전달
                    //db에 저장
                    //saveRoom(send, ""+userEmail, "1");
                    break;
                case MSG_OUT_ROOM:
                    // 방나가기
                    //방제목 받아오기
                    try {

                        Bundle bundle_OUT_ROOM = msg.getData();

                        String title2 = bundle_OUT_ROOM.getString("title");
                        Log.e("TAG", "방나가기 실행 방이름:" + title2);

                        userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오


                        sendMsg("400|" + title2 + "|" + userEmail); //방제목을 서버에게 전달
                        Log.e("TAG", "방나가기 서버에 요청 :" + title2 + "|" + userEmail);

                        //쉐어드에 저장된 등록된 방삭제

                        myRoom_PreferenceManager.deleteString(getApplicationContext(), "ROOM", title2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;

                case MSG_MSG:
                    Bundle bundle_MSG = msg.getData();
                    send = bundle_MSG.getString("send");

                    if (send.equals("사진을 보냈습니다.")) {
                        String lastid = bundle_MSG.getString("lastid");
                        sendMsg("301|" + "사진을 보냈습니다.|" + lastid); //이미지저장 id를 서버에게 전달
                        Log.e("TAG", "이미지 메시지 보냄:" + lastid);


                    } else {
                        Log.e("TAG", "MSG_MSG/send:" + bundle_MSG.getString("send"));
                        sendMsg("300|" + send); //채팅메시지를 서버에게 전달
                    }


                    //db에 저장
                    //saveRoom(send, ""+userEmail, "1");
                    break;
                //죽지않는 서비스 사용후 동기화 안됨
                //쉐어드에 메시지 읽지않은 기록 저장하고 메시지 저장하는데 그로인해 갱신 안됨
                //서비스 내부에서는 쉐어드 갱신되어지니 갱신할 데이터를 메시지로 주고 받는다
                case MSG_REQUEST_NO_READ_COUNT:

                    //읽지않은 메시지 리스트 전달 to ChatRoomActivity

                    //ChatRoomActivity에서 요청하면 쉐어드에서 읽지않은 메시지 리스트 받아와서


                    Bundle bundle_MSG_REQUEST_NO_READ_COUNT = msg.getData();
                    send = bundle_MSG_REQUEST_NO_READ_COUNT.getString("send");
                    Log.e("TAG", "MSG_REQUEST_NO_READ_COUNT/send:" + bundle_MSG_REQUEST_NO_READ_COUNT.getString("send"));
                    try {
                        Message msg_NO_READ = noReadCount();

                        if(chatRoom_clientList.size() >0)
                        {
                            chatRoom_clientList.get(0).send(msg_NO_READ);
                        }




//                        if (clientList.size() > 0 || chatRoom_clientList.size() > 0) {
//
//                            for (int i = 0; i < clientList.size(); i++) {
//
//                                try {
//
//
//                                 //   clientList.get(i).send(msg_NO_READ);
//
//
//                                    Log.e("서비스에서 ChatRoomActivity", "");
//
//                                } catch (RemoteException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                        }

                    } catch (Exception e) {

                    }


                    break;

                case MSG_MAINCHAT:
                    Bundle bundle_MAINCHAT_OUT = msg.getData();
                    MainChat = bundle_MAINCHAT_OUT.getString("MainChat");

                    Log.e("TAG", "MSG_MAINCHAT/MainChat:" + bundle_MAINCHAT_OUT.getString("MainChat"));

                    break;
                case MSG_YES_READ:
                    Bundle bundle_YES_READ = msg.getData();

                    send = bundle_YES_READ.getString("send");

                    String userEamil = PreferenceManager.getString(getApplicationContext(), "userEmail");

                    Log.e("TAG", "MSG_YES_READ/send:" + bundle_YES_READ.getString("send"));
                    try {
                        ChatCount_PreferenceManager.resetChatCount(getApplicationContext(), send);//메시지 읽음 처리
                        Log.e("TAG", "메시지 읽음 처리:" + bundle_YES_READ.getString("send"));

                        //서버에 메시지 읽었다 전달
                        sendMsg("303|" + send + "|" + userEamil); //채팅메시지를 서버에게 전달
                        Log.v("읽었다고 서버에 보냄 ", "MSG_YES_READ |" + "303|" + send + "|" + userEamil);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("TAG", "오류발생 | MSG_YES_READ");

                    }

                    break;
                case MSG_MSGLIST:
                    //메시지 리스트 전달 to MainChatActivity

                    Bundle bundle_MSGLIST = msg.getData();
                    send = bundle_MSGLIST.getString("send");
                    Log.e("TAG", "MSG_MSGLIST/send:" + bundle_MSGLIST.getString("send"));

//                    if (clientList.size() > 0) {
//
//                        //   boolean save_check = false;
//
//                        for (int i = 0; i < clientList.size(); i++) {
//                            Message message = Message.obtain(null, MSG_MSGLIST);
//                            Bundle bundle_toChatRoom = message.getData();
//                            bundle_toChatRoom.putString("title", "");
//
//                            bundle_toChatRoom.putString("MSG", "");
//                            bundle_toChatRoom.putString("time", "");
//
//                            try {
//                                clientList.get(i).send(message);
//                                Log.e("서비스에서 MainChatActivity", "");
//
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//
//                    }

                    break;

                case MSG_READ_LINE:

                    //채팅방 나갔음을 의미
                    Log.v("방나갔을때", "방나갔을때");

                    NowRoom_name ="";


                    //여기까지 읽었습니다. 공지사항 모드로 채팅저장


                    Bundle bundle_READ_LINE = msg.getData();
                    send = bundle_READ_LINE.getString("send");
                    Log.e("TAG", "여기까지 읽었습니다. 방제목 :" + send);

                    //기존 읽음 삭제
                    String json = Chat_PreferenceManager.getChatArrayPref(getApplicationContext(), send);
                    try {

                        if(json  != null)
                        {
                            JSONArray jsonArray = new JSONArray(json);
                            JSONArray jsonArray_newChat = new JSONArray();
                            Chat_PreferenceManager.deleteString(getApplicationContext(), send);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);

                                String msg_ = item.getString("msg");
                                String time = item.getString("time");
                                String img_id = "";

                                try {
                                    img_id = item.getString("img_id");

                                }catch (Exception e){

                                }

                                String data_[] = msg_.split("▶");

                                // String msgs[] = msg.split("▶");
                                if (!data_[0].equals("읽음")) { //읽음이 아니면
                                    Log.e("TAG", "메시지 저장 :" + data_[0]);
                                    //메시지 저장
                                    Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), send, msg_, time, img_id);

                                }//읽음이 아닌 메시지 만 다시저장
                            }
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String noti = "읽음▶=========여기까지 읽었습니다.=========";

                    try {
                        //if(!MainChat.equals("MainChat")){
                            Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), send, noti, "", "");
                        //}

                        //여기까지 읽었습니다. 저장되는 위치
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        unregisterRestartAlarm();
        userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오
        connect();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /**
         * 서비스 종료 시 알람 등록을 통해 서비스 재 실행
         */
        Log.v("서비스 종료", "서비스 종료");

        registerRestartAlarm();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("onStartCommand", "onStartCommand");

        /**
         * startForeground 를 사용하면 notification 을 보여주어야 하는데 없애기 위한 코드
         */
        NotificationChannel channel = new NotificationChannel(
                "default", "service channel", NotificationManager.IMPORTANCE_MIN
        );

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(channel);
        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Log.v("노티생성", "노티생성");

            notification = new Notification.Builder(getApplicationContext(), "default")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("")
                    .setContentText("")
                    .setAutoCancel(true)
                    .build();

        }
        startForeground(1, notification);

        nm.notify(startId, notification);
        nm.cancel(startId);

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();

    }

    private Message noReadCount() throws JSONException {
        //저장된 나의방
        String json_myRoom = myRoom_PreferenceManager.getString(getApplicationContext(), "ROOM");
        Log.e("TAG", "저장된 나의방 :" + json_myRoom);
        if (json_myRoom != null) {
            JSONArray jsonArray_myRoom = new JSONArray(json_myRoom);//저장된 나의방 jsonArray변환

            JSONObject room_Object = new JSONObject(); //각방 안읽을 메시지 넣을 오브젝트
            for (int j = 0; j < jsonArray_myRoom.length(); j++) {
                JSONObject itme_myRoom = jsonArray_myRoom.getJSONObject(j);//방이름 추출

                String myRoom = itme_myRoom.getString("value");
                Log.e("TAG", "방이름 :" + myRoom);


                String json_NO_READ = ChatCount_PreferenceManager.getChatCount(getApplicationContext(), myRoom);
                Log.e("TAG", myRoom + " 읽지안은 카운트 :" + json_NO_READ);
                if (json_NO_READ != null) {
                    JSONArray jsonArray_NO_READ = new JSONArray(json_NO_READ);

                    room_Object.put(myRoom, jsonArray_NO_READ);
                }


            }
            Log.e("TAG", "전달하는 데이터" + room_Object.toString());

            //읽지않은 메시지 리스트 보내기
            Message message_NO_READ = Message.obtain(null, MSG_NO_READ_COUNT);
            Bundle bundle_toChatRoom = message_NO_READ.getData();


            // JSONObject item_NO_READ = jsonArray.getJSONObject(0);
            // String count = item_NO_READ.getString("count");
            String NO_READ_Data = room_Object.toString();


            bundle_toChatRoom.putString("NO_READ_Data", NO_READ_Data);
            return message_NO_READ;

        } else {
            Message message_NO_READ = Message.obtain(null, MSG_NO_READ_COUNT);
            Bundle bundle_toChatRoom = message_NO_READ.getData();
            bundle_toChatRoom.putString("NO_READ_Data", "");

            return message_NO_READ;
        }


    }

    public void connect() {//(소켓)서버연결 요청

        if (nickName == null) {
            nickName = "클라이언트";
        }

        new Thread(new Runnable() {//스레드
            @Override
            public void run() {

                try {
                    //  chat_msgCount_list = new ArrayList<>(); //새로온 메시지 리스트에 넣고 쉐어드에 저장


                    //Socket s = new Socket(String host<서버ip>, int port<서비스번호>);

                    //Socket s = new Socket("192.168.0.91", 5000);//연결시도 팀노바
                    //Socket s = new Socket("192.168.219.100", 5000);//연결시도 집
                    //Socket s = new Socket("172.30.1.35", 5000);//연결시도 카페
                    //Socket s = new Socket("15.164.50.211", 5000);//연결시도 우분투
                    Socket s = new Socket("192.168.42.85", 5000);//연결시도 5G

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
                    String json = myRoom_PreferenceManager.getString(getApplicationContext(), "ROOM");
                    Log.v("TAG", "json : " + json);

                    if (json != null) {
                        JSONArray jsonArray = new JSONArray(json);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            String room_name = item.getString("value");

                            sendMsg("200|" + room_name); //방제목을 서버에게 전달


                        }


                    }


                    while (true) {

                        if (room != null) {
                            room = null;
                        }


                        String msg = in.readLine();//msg: 서버가 보낸 메시지
                        Log.v("서버가 보낸 메시지", msg);

                        //msg==> "300|안녕하세요"  "160|자바방--1,오라클방--1,JDBC방--1"

                        String msgs[] = msg.split("\\|");

                        String protocol = msgs[0];

                        switch (protocol) {

                            case "303"://메시지 읽은사람
                                //아하 읽었구나
//
//                                String Room_name = msgs[1];//읽은 발생한 방이름
//                                String user_id = msgs[2];//읽은사람
//
//                                String json_ChatData = Chat_PreferenceManager.getChatArrayPref(getApplicationContext(), Room_name);//방 채팅 데이터
//                                Log.v("방 채팅 데이터", "" + json_ChatData);
//
//                                if (json_ChatData == null) {
//                                    return;
//                                }
//
//                                JSONArray jsonArray_ChatData = new JSONArray(json_ChatData);
//                                Log.v("방 채팅 데이터 json 변환", "" + jsonArray_ChatData.toString());
//
//
//                                Chat_PreferenceManager.deleteString(getApplicationContext(), Room_name);
//
//                                for (int i = 0; i < jsonArray_ChatData.length(); i++) {
//                                    JSONObject item = jsonArray_ChatData.getJSONObject(i);
//                                    JSONArray jsonArray_read_mem;
//
//                                    String msg_Data = item.getString("msg");
//
//                                    String time = item.getString("time");
//                                    String img_id = "";
//                                    try {
//                                        img_id = item.getString("img_id");
//
//                                    } catch (Exception e) {
//
//                                    }
//
//                                    String read_Count = item.getString("read_Count");
//
//                                    String json_read_mem = item.getString("read_mem");
//
//                                    Log.v("읽음체크", "읽음 사람아이디:" + json_read_mem);
//
//                                    if (!json_read_mem.equals("") && json_read_mem != null) {//빈값이 아니면
//                                        Log.v("읽음체크", "읽었던 사람이 빈값이 아니면:" + json_read_mem);
//
//                                        jsonArray_read_mem = new JSONArray(json_read_mem);
//                                        Log.v("jsonArray_read_mem", " length() :" + jsonArray_read_mem.length());
//
//                                        boolean CHECK = false;
//                                        for (int j = 0; j < jsonArray_read_mem.length(); j++) {
//                                            String read_mem = jsonArray_read_mem.getString(j);
//                                            Log.v("read_mem", ":" + read_mem);
//
//                                            if (read_mem.equals(user_id)) {
//                                                CHECK = true;
//                                                Log.v("읽음체크", "추가해야하는 읽은사람과 중복발생:" + user_id);
//
//                                            }
//                                        }
//
//                                        if (!CHECK) {
//                                            jsonArray_read_mem.put(user_id);
//                                            //사용자 정보를 어레이에 넣음
//                                            item.put("read_mem", jsonArray_read_mem);
//                                            Log.v("읽음체크", "읽은사람 추가:" + user_id);
//                                            Log.v("읽음체크", "읽은사람 추가:" + jsonArray_read_mem.toString());
//                                            json_read_mem = jsonArray_read_mem.toString();
//
//                                        }
//                                    } else {
//                                        Log.v("읽음체크", "읽었던 사람이 빈값 이면:" + json_read_mem);
//
//                                        jsonArray_read_mem = new JSONArray();
//                                        //빈값이면
//                                        jsonArray_read_mem.put(user_id);
//                                        //사용자 정보를 어레이에 넣음
//                                        item.put("read_mem", jsonArray_read_mem);
//                                        Log.v("읽음체크", "읽은사람 추가:" + user_id);
//                                        Log.v("읽음체크", "읽은사람 추가:" + jsonArray_read_mem.toString());
//
//                                    }
//
//
//                                    Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), Room_name, msg_Data, time, img_id, read_Count, jsonArray_read_mem);
//                                    Log.v("채팅저장", "읽은멤버" + jsonArray_read_mem.toString());
//
//
//                                    //채팅화면변경
//                                    if (MainChat.equals("MainChat") && NowRoom_name.equals(msgs[1])) {
//                                        Pageing();
//                                    }
//
//                                }

                                //쉐어드에 저장된 방제목의 채팅에 mem_count 추가 user_id


                                break;

                            case "301":
                                //메시지 이미지 받기
                                ChatCount_PreferenceManager.setChatCount(getApplicationContext(), msgs[1], msgs[3], msgs[2], msgs[4]);

                                JSONArray jsonArray = new JSONArray();
                                if (MainChat.equals("MainChat") && NowRoom_name.equals(msgs[1])) {
                                    String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");
                                    jsonArray.put(userEmail);
                                }
                                Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), msgs[1], msgs[3], msgs[2], msgs[5]);//, msgs[4], jsonArray
                                Log.v("채팅저장", "읽은멤버" + jsonArray.toString());

                                Message message_img = Message.obtain(null, MSG_SENDMSG);
                                Bundle bundle_img = message_img.getData();
                                bundle_img.putString("response", "" + msgs[3]);//메시지 내용

                                bundle_img.putString("time", "" + msgs[2]);//메시지 시간
                                bundle_img.putString("title", "" + msgs[1]);//메시지

                                bundle_img.putString("img_id", "" + msgs[5]);//메시지

                                try {
                                    if (clientList.size() > 0) {
                                        clientList.get(0).send(message_img);

                                    }
                                    if (chatRoom_clientList.size() > 0) {

                                            Message message_NO_READ = noReadCount();

                                            chatRoom_clientList.get(0).send(message_NO_READ);


                                    }

                                    createNotification(getApplicationContext(), msgs[1], msgs[3]);

                                } catch (Exception e) {
                                    e.printStackTrace();

                                }

                                break;

                            case "300":
                                try {

                                    //메시지 받기
                                    //메시지를 adapter에 넣는다

                                    Log.v(" case300", "진입");


                                    //클라이언트에게 전송

//                                    String your_id[] = msgs[3].split("▶");
//                                    your_id[0] = your_id[0].replace("[", "").replace("]", "");
//
//                                    JSONArray jsonArray2 = new JSONArray();
//                                    jsonArray2.put(your_id[0]);
                                    ChatCount_PreferenceManager.setChatCount(getApplicationContext(), msgs[1], msgs[3], msgs[2], msgs[4]);
                                    try{
                                        Log.v(" 1월22일", "시작");
                                     //   if(((ChatRoomActivity)ChatRoomActivity.context).inRoom_name== null){
                                         //   Log.v(" 1월22일", "inRoom null");

                                     //   }
                                       // String inRoom =  ((ChatRoomActivity)ChatRoomActivity.context).inRoom_name; //들어간 방이름
                                        Log.v(" 1월22일", "inRoom :"+NowRoom_name);
                                        Log.v(" 1월22일", "msgs[1]:"+msgs[1]);

                                        if(NowRoom_name.equals(msgs[1]))
                                        {
                                            //들어간 방에서의 메시지
                                            //읽음처리
                                            ChatCount_PreferenceManager.resetChatCount(getApplicationContext(), NowRoom_name);//메시지 읽음 처리

                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    if (MainChat.equals("MainChat") || NowRoom_name.equals(msgs[1])) {//현재 채팅 화면에 있으면
                                        String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");
                                        //jsonArray2.put(""+userEmail);

                                        //상대방에게 읽어다 보내기

                                        // sendMsg("303|" + NowRoom_name + "|" + userEmail); //방제목을 서버에게 전달

                                        //Log.v("읽었다고 서버에 보냄 case300", "303|" + NowRoom_name + "|" + userEmail);

                                    }

                                    Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), msgs[1], msgs[3], msgs[2], "");//, msgs[4], jsonArray2
                                    //Log.v("채팅저장", "읽은멤버" + jsonArray2.toString());

                                    Log.v("메시지 카운트저장", "카운트저장");
                                    if (clientList.size() > 0 || chatRoom_clientList.size() > 0) {

                                        //   boolean save_check = false;


                                        // for (int i = 0; i < clientList.size(); i++) {

                                        Message message = Message.obtain(null, MSG_SENDMSG);
                                        Bundle bundle = message.getData();
                                        bundle.putString("response", "" + msgs[3]);//메시지 내용

                                        bundle.putString("time", "" + msgs[2]);//메시지 시간
                                        bundle.putString("title", "" + msgs[1]);//메시지 제목

//                                        bundle.putString("read_mem", jsonArray2.toString());//읽은 멤버
//                                        bundle.putString("read_Count", "" + msgs[4]);//읽어야하는 카운트

                                        // clientList.get(i).send(message);
                                        Log.e("서비스에서 메시지 보냄", "" + clientList.size());


                                        Log.e("TAG", "message_NO_READ 생성");

                                        //clientList.get(i).send(message_NO_READ);

                                        if (clientList.size() > 0) {
                                            clientList.get(0).send(message);

                                        }


                                        // }
                                        Message message_NO_READ = noReadCount();

                                        chatRoom_clientList.get(0).send(message_NO_READ);

                                        Log.e("TAG", "ChatService to ChatRoomActivity");
                                        //createNotification(getApplicationContext());
                                        createNotification(getApplicationContext(), msgs[1], msgs[3]);
                                    }


                                } catch (Exception e) {
                                    //  clientList.remove(i);
                                    e.printStackTrace();
                                    Log.e("TAG", "오류발생");
                                    Log.e("TAG", "" + e.toString());

                                }
                                break;


                            case "160"://방만들기
                                Log.e("TAG", "방정보 변경 감지");
                                try {
                                    //서버에서 보내는 방정보
                                    String get = msgs[1];

                                    Message message = Message.obtain(null, MSG_RESPONSE);//ChatRoomActivity에서 받아가기 아직
                                    Bundle bundle = message.getData();
                                    bundle.putString("response", "160");
                                    bundle.putString("roomInwon", get);

                                    if (chatRoom_clientList.size() > 0) {
                                        for (int i = 0; i < chatRoom_clientList.size(); i++) {


                                            chatRoom_clientList.get(i).send(message);
                                        }

                                    }
                                    if (clientList.size() > 0) {
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
                                Log.e("대화방인원 : ", "" + msgs[1]);

//                                cc.li_inwon.setListData(myRoomInwons);

                                break;

                            case "180"://대기실 인원정보

                                String get = msgs[1];

                                Message message_inwonName = Message.obtain(null, MSG_RESPONSE);//ChatRoomActivity에서 받아가기 아직
                                Bundle bundle_inwonName = message_inwonName.getData();
                                bundle_inwonName.putString("response", "180");
                                bundle_inwonName.putString("roomInwonName", get);
                                try{
                                    if (chatRoom_clientList.size() > 0) {
                                        for (int i = 0; i < chatRoom_clientList.size(); i++) {


                                            chatRoom_clientList.get(i).send(message_inwonName);
                                        }

                                    }
                                    if (clientList.size() > 0) {
                                        clientList.get(0).send(message_inwonName);

                                    }
                                }catch (Exception e){

                                }


                                break;

                            case "200"://대화방 입장
                                try {

                                    if (clientList.size() > 0) {

                                        Log.e("대화방 입장", "방이름 : " + msgs[1]);
                                        String nickname2 = msgs[2];

                                        Message message2 = Message.obtain(null, MSG_SENDMSG);
                                        Bundle bundle2 = message2.getData();

                                        String noti_IN = "공지사항▶=========[" + nickname2 + "]님이 들어왔습니다.=========";
                                        bundle2.putString("response", "" + noti_IN);//이메일

                                        bundle2.putString("nickName", "" + msgs[2]);//이메일
                                        bundle2.putString("title", "" + msgs[1]);//방이름
                                        Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), msgs[1], noti_IN, "", "");

//
                                        for (int i = 0; i < clientList.size(); i++) {

                                            clientList.get(i).send(message2);


                                        }
                                    }
                                } catch (Exception ex) {

                                }


                                break;
//
//
//
                            case "400"://대화방 퇴장

                                //400|퇴장한이메밀
                                //MainChatActivity에 전달 MSG_SENDMSG
                                Log.e("대화방 퇴장 확인", "방이름 " + msgs[1]);

                                try {
                                    Log.e("공지사항", "클라이언트 리스트 사이즈 :" + clientList.size());

                                    if (clientList.size() > 0) {
                                        Log.e("공지사항", "clientList.size() > 0 ");

                                        String nickname = msgs[2];
                                        Message message = Message.obtain(null, MSG_SENDMSG);
                                        Bundle bundle = message.getData();

                                        String noti = "공지사항▶=========[" + nickname + "]님이 나갔습니다.=========";
                                        bundle.putString("response", "공지사항▶=========[" + nickname + "]님 퇴장=========");//메시지 내용

                                        bundle.putString("nickName", "" + msgs[2]);//이메일
                                        bundle.putString("title", "" + msgs[1]);//방이름

                                        //   boolean save_check = false;
                                        Chat_PreferenceManager.setChatArrayPref(getApplicationContext(), msgs[1], noti, "", "");

                                        for (int i = 0; i < clientList.size(); i++) {

                                            clientList.get(i).send(message);


                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("공지사항", "오류발생 ");

                                }

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

    /**
     * 알람 매니져에 서비스 등록
     */
    private void registerRestartAlarm() {

        Log.i("000 ChatService", "registerRestartAlarm");
        Intent intent = new Intent(ChatService.this, RestartService.class);
        intent.setAction("ACTION.RESTART.PersistentService");
        PendingIntent sender = PendingIntent.getBroadcast(ChatService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1 * 1000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /**
         * 알람 등록
         */
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1 * 1000, sender);

    }

    /**
     * 알람 매니져에 서비스 해제
     */
    private void unregisterRestartAlarm() {

        Log.i("000 ChatService", "unregisterRestartAlarm");

        Intent intent = new Intent(ChatService.this, RestartService.class);
        intent.setAction("ACTION.RESTART.PersistentService");
        PendingIntent sender = PendingIntent.getBroadcast(ChatService.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);


    }


    private void createNotification(Context context, String title, String msg) {

        Log.e("TAG", "채팅화면 여부:" + MainChat);

        if (!MainChat.equals("MainChat")) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default1");

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("" + title);
            builder.setContentText("" + msg);

            builder.setColor(Color.RED);
            // 사용자가 탭을 클릭하면 자동 제거
            //builder.setAutoCancel(true);

            // 알림 표시
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(new NotificationChannel("default1", "기본 채널", NotificationManager.IMPORTANCE_HIGH));
            }
            Log.e("노티 출력", "노티 출력");

            // id값은
            // 정의해야하는 각 알림의 고유한 int값
            notificationManager.notify(2, builder.build());
        }

    }
}
