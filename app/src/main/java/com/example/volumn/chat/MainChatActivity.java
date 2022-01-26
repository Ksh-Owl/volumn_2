package com.example.volumn.chat;


import com.example.volumn.addSet.addSetRequest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.volumn.include.myRoom_PreferenceManager;
import com.example.volumn.setting.get_ProfileIMG_Request;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainChatActivity extends AppCompatActivity {

    public static Context context;
    //소켓 입출력객체
    BufferedReader in;
    private DataOutputStream out;

    public String in_name[];
    public Map<String, String> userIMG = new HashMap<>();
    boolean CheckScroll = false;

    //서비스
    private Messenger mService;
    private final Messenger mMessenger = new Messenger(new IncomingHandelr());

    int position = 0;

    String img;

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

                    if (response.equals("160")) {
                        String roomInwon = bundle.getString("roomInwon");

                        String room_i[] = roomInwon.split(",");

                        for (int i = 0; i < room_i.length; i++) {
                            String room_[] = room_i[i].split("--");
                            String room_name = room_[0];
                            String inwonCount = room_[1];

                            if (room.equals(room_name)) {
                                //같은방
                                txt_memCount.setText(inwonCount);
                            }

                        }

                    }
                    if (response.equals("180")) {//방인원 이름
                        String roomInwonName = bundle.getString("roomInwonName");

                        in_name = roomInwonName.split(",");
                        //in_name = new String[room_i.length];

                        for (int i = 0; i < in_name.length; i++) {

                            //프로필 이미지 데이터베이스에서 받아오기

                            int finalI = i;
                            Response.Listener<String> responseListner = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        boolean success = jsonObject.getBoolean("success");
                                        if (success) { // 운동기록에 성공
                                            //Toast.makeText(getApplicationContext(),"운동저장이 완료되었습니다.",Toast.LENGTH_SHORT).show();

                                            String img = jsonObject.getString("img");



                                            userIMG.put(in_name[finalI], img);
                                            //Bitmap bitmap = ImageUtil.convert(data);
                                            adapter.notifyDataSetChanged();
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

                            get_ProfileIMG_Request get_profileIMG_request = new get_ProfileIMG_Request(in_name[i], responseListner);
                            RequestQueue queue = Volley.newRequestQueue(context);
                            queue.add(get_profileIMG_request);
                        }


                    }

                    //editText2.setText(response);

                    //테스트
//                    try {
//                        Message msg_yes_read = Message.obtain(null, ChatService.MSG_YES_READ);
//                        Bundle bundle_yes_read = msg_yes_read.getData();
//                        bundle_yes_read.putString("send", "" + room);
//                        // bundle_yes_read.putString("send", "" + room);
//
//
//                        mService.send(msg_yes_read);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                    Log.e("TAG", "안읽음 메시지 읽음처리");
                    //테스트

                    break;
                case ChatService.MSG_SENDMSG:
                    Bundle bundle2 = msg.getData();
                    String title = bundle2.getString("title");
                    String response2 = bundle2.getString("response");
                    String time2 = bundle2.getString("time");

//                    String read_Count = bundle2.getString("read_Count");
//
//                       String read_mem = bundle2.getString("read_mem");
//                   JSONArray jsonArray_read_mem = new JSONArray();
//                    try {
//
//                        jsonArray_read_mem = new JSONArray(read_mem);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    try {
                        String img_id = bundle2.getString("img_id");
                        if (img_id != null || !img_id.equals("")) {
                            //사진을 보냈습니다.

                            msg_Model msg_model = new msg_Model(response2, time2, img_id);//, read_Count, jsonArray_read_mem
                            clientMsg_list.add(msg_model);
                            adapter.notifyDataSetChanged();

                            //db에서 사진 데이터 받아오기

                            return;


                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    Log.e("TAG", "MainChatActivity Response::" + response2);
                    //ArrayList<String> Msg_list = new ArrayList<>();
                    if (title.equals(txt_nowRoom.getText().toString())) {

                        msg_Model msg_model = new msg_Model(response2, time2, "");//, read_Count, jsonArray_read_mem
                        clientMsg_list.add(msg_model);


                        // adapter = new MsgAdapter(clientMsg_list);
                        adapter.notifyDataSetChanged();
                        //Toast.makeText(context, "메시지", Toast.LENGTH_SHORT).show();
                        rv_msgList.scrollToPosition(clientMsg_list.size() - 1);
                    }


                    break;
                case ChatService.MSG_PAGEING_MSGLIST:

                    //로딩 시작
                    Bundle bundle4 = msg.getData();
                    String title4 = bundle4.getString("title");
                    String response4 = bundle4.getString("response");

                    try {
                        if (response4 == null) {
                            return;
                        }
                        JSONArray jsonArray = new JSONArray(response4);

                        int a = jsonArray.length();

                        if (title4.equals(txt_nowRoom.getText().toString())) {
                            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                JSONObject item = jsonArray.getJSONObject(i);

                                String msg_ = item.getString("msg");
                                String time_ = item.getString("time");
                                String img_id_ = "";
                                try {
                                    img_id_ = item.getString("img_id");

                                } catch (Exception e) {

                                }
//                                String read_Count_ = item.getString("read_Count");
//
//                                String read_mem_ = item.getString("read_mem");
//
//                                JSONArray jsonArray_read_mem_ = new JSONArray(read_mem_);


                                msg_Model msg_model = new msg_Model(msg_, time_, img_id_);//, read_Count_, jsonArray_read_mem_
                                clientMsg_list.add(0, msg_model);


                            }

                            // adapter = new MsgAdapter(clientMsg_list);
                            adapter = new MsgAdapter(clientMsg_list);


                            rv_msgList.setAdapter(adapter);
                            rv_msgList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

                            //adapter.notifyDataSetChanged();
                            rv_msgList.scrollToPosition(17);
                            CheckScroll = false;
                            //Toast.makeText(context, "메시지", Toast.LENGTH_SHORT).show();
                            //rv_msgList.scrollToPosition(rv_msgList.getAdapter().getItemCount() - 1);
                        }
                        if (!MainChatActivity.this.isFinishing()) {
//                            ProgressDialog asyncDialog = new ProgressDialog(
//                                    MainChatActivity.this);
//                            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                            asyncDialog.setMessage("로딩중입니다..");
//                            asyncDialog.show();

//                            //로딩 끝
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    asyncDialog.dismiss();
//
//
//                                }
//                            }, 500); //딜레이 타임 조절
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                case ChatService.MSG_MSGLIST:
                    Bundle bundle3 = msg.getData();
                    String title3 = bundle3.getString("title");
                    String response3 = bundle3.getString("response");


                    try {
                        if (title3 == null || response3 == null) {
                            return;
                        }
                        JSONArray jsonArray = new JSONArray(response3);


                        if (title3.equals(txt_nowRoom.getText().toString())) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);
                                String img_id_ = "";

                                String msg_ = item.getString("msg");
//                                String msgs[] = msg_.split("▶");
//                                msgs[0] = msgs[0].replace("[", "").replace("]", "");
                                //split으로 아이디 받아오기
                                //in_name.add(msgs[0]);
                                //아이디 배열에 중복값 없으면 넣기및 이미지 받아오기
                                //
                                String time_ = item.getString("time");
                                try {
                                    img_id_ = item.getString("img_id");

                                } catch (Exception e) {

                                }
//                                String read_Count2 = item.getString("read_Count");
//                                String read_mem2 = item.getString("read_mem");
//                                JSONArray jsonArray_read_mem2 = new JSONArray();
//                                try {
//
//                                    jsonArray_read_mem2 = new JSONArray(read_mem2);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }


                                msg_Model msg_model = new msg_Model(msg_, time_, img_id_);//, read_Count2, jsonArray_read_mem2
                                clientMsg_list.add(msg_model);


                            }

                            // adapter = new MsgAdapter(clientMsg_list);
                            try {
                                adapter.notifyDataSetChanged();


                                rv_msgList.smoothScrollToPosition(clientMsg_list.size() - 1);


                                for (int i = 0; i < clientMsg_list.size(); i++) {
                                    //rv_msgList.smoothScrollToPosition(clientMsg_list.size());

                                    if (clientMsg_list.get(i).getMsg().equals("읽음▶=========여기까지 읽었습니다.=========")) {
                                        rv_msgList.scrollToPosition(i - 1);
                                        position = i;
                                    }

                                }
                            } catch (Exception e) {

                            }


                            //Toast.makeText(context, "메시지", Toast.LENGTH_SHORT).show();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("TAG", "MainChatActivity ::" + title3);
                    //ArrayList<String> Msg_list = new ArrayList<>();

                    //editText2.setText(response);
                    //Chat_PreferenceManager.setChatArrayPref(getApplicationContext(),msgs[1],msgs[2]);


                    break;

                default:
                    super.handleMessage(msg);

            }

        }
    }

    //클라세팅
    ArrayList<msg_Model> clientMsg_list;

    TextView txt_nowRoom;//현재 방이름
    TextView txt_memCount; //현재방인원

    private String clientMsg;
    private String nickName;
    ImageView img_send;
    ImageView img_back3;

    ImageView img_addImage; //이미지 보내기

    EditText etxt_msgBox;
    RecyclerView rv_msgList;
    MsgAdapter adapter;
    Button btn_exitRoom;


    String room = null;//채팅방 이름
    String room_ID = null;//채팅방 ID
    String userEmail;

    String encodeImageString;
    Thread th;

    //페이징 변수
    int limit = 20;
    int page = 1;


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


    int scrollCount = 0;

    public void rvPosition() {
        if (scrollCount > 2) {
            return;

        }

        rv_msgList.scrollToPosition(clientMsg_list.size() - 1);


        for (int i = 0; i < clientMsg_list.size(); i++) {
            //rv_msgList.smoothScrollToPosition(clientMsg_list.size());

            if (clientMsg_list.get(i).getMsg().equals("읽음▶=========여기까지 읽었습니다.=========")) {
                rv_msgList.scrollToPosition(i - 1);
                position = i;
            }

        }
        scrollCount++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        context = this;
        bindService(new Intent(this, ChatService.class), conn, Context.BIND_AUTO_CREATE);

        btn_exitRoom = (Button) findViewById(R.id.btn_exitRoom);

        img_addImage = findViewById(R.id.img_addImg);//이미지 보내기
        img_send = findViewById(R.id.img_send);
        etxt_msgBox = findViewById(R.id.etxt_msgBox);
        rv_msgList = findViewById(R.id.rv_msgList);
        txt_nowRoom = findViewById(R.id.txt_nowRoom);
        img_back3 = (ImageView) findViewById(R.id.img_back3);
        Intent intent = getIntent();//인텐트 받아오기
        room = intent.getStringExtra("roomName");
        txt_nowRoom.setText(room);
        //읽지않은 메시지 카운트 삭제
        //ChatCount_PreferenceManager.deleteString(context,room);
        txt_memCount = (TextView) findViewById(R.id.txt_memCount);//현재 방 인원수

        room_ID = intent.getStringExtra("room_ID");

        userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        //  connect();
//        String json = Chat_PreferenceManager.getChatArrayPref(context, room);
//
        clientMsg_list = new ArrayList<>(); //메시지 리스트 객체생성
//
//        if (json != null) {
//            try {
//                JSONArray jsonArray = new JSONArray(json);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//                    JSONObject item = jsonArray.getJSONObject(i);
//
//                    String msg = item.getString("msg");
//                    String time = item.getString("time");
//                    String img_id = item.getString("img_id");
//                    msg_Model msg_model = new msg_Model(msg,time,img_id,"",null);
//
//                    clientMsg_list.add(msg_model);
//                }
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
        img_back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리 or 사진 앱 실행하여 사진을 선택하도록
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 10);

            }
        });
        btn_exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //테스트

//                    Message msg_yes_read = Message.obtain(null, ChatService.MSG_YES_READ);
//                    Bundle bundle_yes_read = msg_yes_read.getData();
//                    bundle_yes_read.putString("send", "" + room);
//                    mService.send(msg_yes_read);

                    // bundle_yes_read.putString("send", "" + room);




                    //서비스(서버에 방 나가기) 메시지 보내기
                    Message msg = Message.obtain(null, ChatService.MSG_OUT_ROOM);
                    Bundle bundle = msg.getData();
                    bundle.putString("title", "" + txt_nowRoom.getText().toString());

                    mService.send(msg);






                    Log.e("TAG", "안읽음 메시지 읽음처리");
                    //테스트

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                //마지막 사람이면 db에서 방 지우기

                //방나가기기
                finish();
            }
        });

        adapter = new MsgAdapter(clientMsg_list);

        rv_msgList.setAdapter(adapter);
        rv_msgList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));


        rv_msgList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //현재 보이는 것 중 마지막 아이템의 포지션 +1
                int lastVisibleItemPosition = ((LinearLayoutManager) rv_msgList.getLayoutManager()).findFirstVisibleItemPosition() - 1;
                //전체 아이템 갯수
                int itemTotalCount = rv_msgList.getAdapter().getItemCount();
                if (!CheckScroll && itemTotalCount >= 20 && lastVisibleItemPosition == -1) {
                    Log.e("전체 아이템 갯수", String.valueOf(itemTotalCount));
                    Log.e("홈 프레그먼트 ", String.valueOf(lastVisibleItemPosition));

                    page++;

                    //
                    //서비스에 page,limit 전달

                    try {
                        Message msg = Message.obtain(null, ChatService.MSG_PAGEING_MSGLIST);
                        Bundle bundle = msg.getData();
                        bundle.putString("title", "" + txt_nowRoom.getText().toString());
                        bundle.putString("page", "" + page);
                        bundle.putString("limit", "" + limit);
                        mService.send(msg);
                        CheckScroll = true;

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg_txt = etxt_msgBox.getText().toString();


                //메시지 보내기 서비스에 전달
                try {
                    if (msg_txt.equals("")) {
                        msg_txt = " ";
                    }

                    Message msg = Message.obtain(null, ChatService.MSG_MSG);
                    Bundle bundle = msg.getData();
                    bundle.putString("send", "" + msg_txt);
                    mService.send(msg);
                    Log.e("TAG", "채팅메시지 서비스에 전달 :" + "" + msg_txt);
                    //테스트

//                    Message msg_yes_read = Message.obtain(null, ChatService.MSG_YES_READ);
//                    Bundle bundle_yes_read = msg_yes_read.getData();
//                    bundle_yes_read.putString("send", "" + room);
                    // bundle_yes_read.putString("send", "" + room);


                    //mService.send(msg_yes_read);

                    //Log.e("TAG", "안읽음 메시지 읽음처리");
                    //테스트


                } catch (Exception e) {
                    e.printStackTrace();
                }
                //sendMsg("300|" + msg);
                //sendMsg(msg);

                etxt_msgBox.setText("");
            }
        });


    }

    private void inRoom() {
        //방 들어왔다 서비스에 전달
        try {
            clientMsg_list.clear();
            Message msg = Message.obtain(null, ChatService.MSG_IN_ROOM);
            Bundle bundle = msg.getData();
            bundle.putString("send", "" + room);
            bundle.putString("MainChat", "MainChat");

            bundle.putString("page", "" + page);
            bundle.putString("limit", "" + limit);

            Log.e("TAG", "노티피케이션 불가");

            mService.send(msg);
            Log.e("TAG", "채팅방 입장 :" + "" + userEmail);
            //방들어갈때 쉐어드 들어간 방 저장


            Message msg_yes_read = Message.obtain(null, ChatService.MSG_YES_READ);
            Bundle bundle_yes_read = msg_yes_read.getData();
            bundle_yes_read.putString("send", "" + room);
            // bundle_yes_read.putString("send", "" + room);

            mService.send(msg_yes_read);
            Log.e("TAG", "안읽음 메시지 읽음처리");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();


        if (mService != null) {
            try {
                Message msg_MAIN_CHAT = Message.obtain(null, ChatService.MSG_MAINCHAT);
                Bundle bundle_MAIN_CHAT = msg_MAIN_CHAT.getData();
                bundle_MAIN_CHAT.putString("MainChat", "MainChat");//채팅화면으로 들어갔다고 알림
                mService.send(msg_MAIN_CHAT);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        if (clientMsg_list != null) {

            try {
                //rv_msgList.scrollToPosition(0);
                rv_msgList.scrollToPosition(clientMsg_list.size());

            } catch (Exception e) {

            }

        }

        //메시지 읽어다 메시지 보내기

        //보내야 하는 메지지 데이터
        //방제목,내 id

//        try {
//            if(mService == null)
//            {
//                return;
//            }
//            Message msg = Message.obtain(null, ChatService.MSG_YES_READ);
//            Bundle bundle = msg.getData();
//            bundle.putString("send", "" + room);//방제목
//            bundle.putString("userEmail", "" + userEmail);//방제목
//
//            mService.send(msg);
//            Log.e("TAG", "안읽음 메시지 읽음처리");
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {


            Message msg = Message.obtain(null, ChatService.MSG_YES_READ);
            Bundle bundle = msg.getData();
            bundle.putString("send", "" + room);//방제목
            //bundle.putString("userEmail", "" + userEmail);
            mService.send(msg);
            Log.e("TAG", "안읽음 메시지 읽음처리");

            //ChatCount_PreferenceManager.resetChatCount(getApplicationContext(), room);//메시지 읽음 처리


            Message msg_MAIN_CHAT = Message.obtain(null, ChatService.MSG_MAINCHAT);
            Bundle bundle_MAIN_CHAT = msg_MAIN_CHAT.getData();//메시지 화면에서 나갔다고 알림
            bundle_MAIN_CHAT.putString("MainChat", "");

            mService.send(msg_MAIN_CHAT);

            Log.e("TAG", "노티피케이션 출력 가능");


            //여기까지 읽었습니다
            Message msg_MSG_READ_LINE = Message.obtain(null, ChatService.MSG_READ_LINE);
            Bundle bundle_MSG_READ_LINET = msg_MSG_READ_LINE.getData();
            bundle_MSG_READ_LINET.putString("send", "" + room);

            mService.send(msg_MSG_READ_LINE);

            Log.e("TAG", "노티피케이션 출력 가능");


        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {


                    //선택한 사진의 경로(Uri)객체 가져오기
                    // Uri uri = data.getData();

                    if (data != null) {
                        if (data.getClipData() == null) {  // 이미지를 하나만 선택한 경우
                            Log.e("single choice: ", String.valueOf(data.getData()));
                            Uri imageUri = data.getData();

                            //bitmap으로 변환

                            try {
                                encodeUriToBitmap(imageUri);//최종 결과 encodeImageString 값에 이미지 String 값 전달됨


                                Upload_img(encodeImageString);//이미지 DB에 저장

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            String imgPath = getRealPathFromUri(imageUri);

                            //서버에 메시지 보내기


                            //clientMsg_list.add( "이미지▶"+imageUri);
                            // adapter.notifyDataSetChanged();


                            //  rv_msgList.scrollToPosition(clientMsg_list.size()-1);
                            Log.d("이미지 경로", "" + imageUri.toString() + "\n" + imgPath);

                        } else {
                            //이미지를 여러장 선택한 경우
                            ClipData clipData = data.getClipData();
                            Log.e("clipData", String.valueOf(clipData.getItemCount()));

                            if (clipData.getItemCount() > 10) {// 선택한 이미지가 11장 이상인 경우
                                Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();

                            } else {//선택한 이미지가 1장 이상 10장이하인 경우
                                Log.e("TAG", "multiple choice");
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                                    try {
                                        // clientMsg_list.add("이미지▶"+imageUri);  //uri를 list에 담는다.

                                        //서버에 메시지 보내기
                                        encodeUriToBitmap(imageUri);//최종 결과 encodeImageString 값에 이미지 String 값 전달됨


                                        Upload_img(encodeImageString);//이미지 DB에 저장

                                    } catch (Exception e) {
                                        Log.e("TAG", "File select error", e);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                rv_msgList.scrollToPosition(clientMsg_list.size());

                                //이미지가 어댑터에서 작업 되어지면서 그때

                            }

                        }

                        //이미지뷰의 변수명 . setImageURI(uri);

                        //갤러리앱에서 관리하는 DB정보가 있는데 , 그것이 나온다 [실제 파일 경로가 아님]
                        //얻어온 Uri는 Gallery앱의 DB번호임
                        //업로드를 하려면 이미지의 절대경로(실제경로: file:// -----/aaa.png)필요함
                        //Uri -->절대경로 (String)변환


                        // adapter = new MsgAdapter(clientMsg_list);
                        //Toast.makeText(context, "메시지", Toast.LENGTH_SHORT).show();


                        //이미지 경로 uri 확인해보기
                    } else {
                        Toast.makeText(context, "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                break;
        }

    }

    String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void Upload_img(String encodeImageString) {


        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    boolean success = jsonObject.getBoolean("success");

                    if (success) { // 이미지 업로드에 성공

                        //메시지 보내기 서비스에 전달
                        try {
                            String lastid = jsonObject.getString("lastid");

                            // Toast.makeText(getApplicationContext(), "저장 ID" + lastid, Toast.LENGTH_SHORT).show();

                            Message msg = Message.obtain(null, ChatService.MSG_MSG);
                            Bundle bundle = msg.getData();
                            bundle.putString("send", "사진을 보냈습니다.");
                            bundle.putString("lastid", lastid);

                            mService.send(msg);
                            Log.e("TAG", "이미지 보냈음을 서비스에 전달");


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // 이미지 업로드 실패
                        //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                        return;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        //String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        Upload_img_Request upload_img_request = new Upload_img_Request(room, room_ID, userEmail, encodeImageString, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(upload_img_request);
    }

    private void encodeUriToBitmap(Uri uri) throws FileNotFoundException {//최종 결과 encodeImageString 값에 이미지 String 값 전달됨

        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        //encodeBitmapImage(bitmap);

        encodeImageString = ImageUtil.convert(bitmap);


    }


}