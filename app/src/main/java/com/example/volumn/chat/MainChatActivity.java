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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainChatActivity extends AppCompatActivity {

    public static Context context;
    //?????? ???????????????
    BufferedReader in;
    private DataOutputStream out;

    public String in_name[];
    public Map<String, String> userIMG = new HashMap<>();
    boolean CheckScroll = false;

    //?????????
    private Messenger mService;
    private final Messenger mMessenger = new Messenger(new IncomingHandelr());

    int position = 0;

    String img;

    private class IncomingHandelr extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {

            //????????? ?????? ??????
            //??????????????? ????????? ?????? ??????
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
                                //?????????
                                txt_memCount.setText(inwonCount);
                                txt_memCount.setVisibility(View.VISIBLE);
                            }

                        }

                    }
                    if (response.equals("180")) {//????????? ??????
                        String roomInwonName = bundle.getString("roomInwonName");

                        in_name = roomInwonName.split(",");
                        //in_name = new String[room_i.length];

                        for (int i = 0; i < in_name.length; i++) {

                            //????????? ????????? ???????????????????????? ????????????

                            int finalI = i;
                            Response.Listener<String> responseListner = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        boolean success = jsonObject.getBoolean("success");
                                        if (success) { // ??????????????? ??????
                                            //Toast.makeText(getApplicationContext(),"??????????????? ?????????????????????.",Toast.LENGTH_SHORT).show();

                                            String img = jsonObject.getString("img");



                                            userIMG.put(in_name[finalI], img);
                                            //Bitmap bitmap = ImageUtil.convert(data);
                                            adapter.notifyDataSetChanged();
                                        } else { // ??????????????? ??????????????????.
                                            //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                                            return;
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            };
                            //String userEmail = PreferenceManager.getString(context, "userEmail");//??????????????? ???????????? ????????? ????????????

                            get_ProfileIMG_Request get_profileIMG_request = new get_ProfileIMG_Request(in_name[i], responseListner);
                            RequestQueue queue = Volley.newRequestQueue(context);
                            queue.add(get_profileIMG_request);
                        }


                    }

                    //editText2.setText(response);

                    //?????????
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
//                    Log.e("TAG", "????????? ????????? ????????????");
                    //?????????

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
                            //????????? ???????????????.

                            msg_Model msg_model = new msg_Model(response2, time2, img_id);//, read_Count, jsonArray_read_mem
                            clientMsg_list.add(msg_model);
                            adapter.notifyDataSetChanged();

                            //db?????? ?????? ????????? ????????????

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
                        //Toast.makeText(context, "?????????", Toast.LENGTH_SHORT).show();
                        rv_msgList.scrollToPosition(clientMsg_list.size() - 1);
                    }


                    break;
                case ChatService.MSG_PAGEING_MSGLIST:

                    //?????? ??????
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
                            //Toast.makeText(context, "?????????", Toast.LENGTH_SHORT).show();
                            //rv_msgList.scrollToPosition(rv_msgList.getAdapter().getItemCount() - 1);
                        }
                        if (!MainChatActivity.this.isFinishing()) {
//                            ProgressDialog asyncDialog = new ProgressDialog(
//                                    MainChatActivity.this);
//                            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                            asyncDialog.setMessage("??????????????????..");
//                            asyncDialog.show();

//                            //?????? ???
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    asyncDialog.dismiss();
//
//
//                                }
//                            }, 500); //????????? ?????? ??????
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
//                                String msgs[] = msg_.split("???");
//                                msgs[0] = msgs[0].replace("[", "").replace("]", "");
                                //split?????? ????????? ????????????
                                //in_name.add(msgs[0]);
                                //????????? ????????? ????????? ????????? ????????? ????????? ????????????
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

                                    if (clientMsg_list.get(i).getMsg().equals("?????????=========???????????? ???????????????.=========")) {
                                        rv_msgList.scrollToPosition(i - 1);
                                        position = i;
                                    }

                                }
                            } catch (Exception e) {

                            }


                            //Toast.makeText(context, "?????????", Toast.LENGTH_SHORT).show();

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

    //????????????
    ArrayList<msg_Model> clientMsg_list;

    TextView txt_nowRoom;//?????? ?????????
    TextView txt_memCount; //???????????????

    private String clientMsg;
    private String nickName;
    ImageView img_send;
    ImageView img_back3;

    ImageView img_addImage; //????????? ?????????

    EditText etxt_msgBox;
    RecyclerView rv_msgList;
    MsgAdapter adapter;
    Button btn_exitRoom;


    String room = null;//????????? ??????
    String room_ID = null;//????????? ID
    String userEmail;

    String encodeImageString;
    Thread th;

    //????????? ??????
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

            if (clientMsg_list.get(i).getMsg().equals("?????????=========???????????? ???????????????.=========")) {
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

        img_addImage = findViewById(R.id.img_addImg);//????????? ?????????
        img_send = findViewById(R.id.img_send);
        etxt_msgBox = findViewById(R.id.etxt_msgBox);
        rv_msgList = findViewById(R.id.rv_msgList);
        txt_nowRoom = findViewById(R.id.txt_nowRoom);
        img_back3 = (ImageView) findViewById(R.id.img_back3);
        Intent intent = getIntent();//????????? ????????????
        room = intent.getStringExtra("roomName");
        txt_nowRoom.setText(room);
        //???????????? ????????? ????????? ??????
        //ChatCount_PreferenceManager.deleteString(context,room);
        txt_memCount = (TextView) findViewById(R.id.txt_memCount);//?????? ??? ?????????

        room_ID = intent.getStringExtra("room_ID");

        userEmail = PreferenceManager.getString(context, "userEmail");//??????????????? ???????????? ????????? ????????????

        //  connect();
//        String json = Chat_PreferenceManager.getChatArrayPref(context, room);
//
        clientMsg_list = new ArrayList<>(); //????????? ????????? ????????????
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
                //????????? or ?????? ??? ???????????? ????????? ???????????????
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

                    //?????????

//                    Message msg_yes_read = Message.obtain(null, ChatService.MSG_YES_READ);
//                    Bundle bundle_yes_read = msg_yes_read.getData();
//                    bundle_yes_read.putString("send", "" + room);
//                    mService.send(msg_yes_read);

                    // bundle_yes_read.putString("send", "" + room);




                    //?????????(????????? ??? ?????????) ????????? ?????????
                    Message msg = Message.obtain(null, ChatService.MSG_OUT_ROOM);
                    Bundle bundle = msg.getData();
                    bundle.putString("title", "" + txt_nowRoom.getText().toString());

                    mService.send(msg);






                    Log.e("TAG", "????????? ????????? ????????????");
                    //?????????

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                //????????? ???????????? db?????? ??? ?????????

                //???????????????
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

                //?????? ????????? ??? ??? ????????? ???????????? ????????? +1
                int lastVisibleItemPosition = ((LinearLayoutManager) rv_msgList.getLayoutManager()).findFirstVisibleItemPosition() - 1;
                //?????? ????????? ??????
                int itemTotalCount = rv_msgList.getAdapter().getItemCount();
                if (!CheckScroll && itemTotalCount >= 20 && lastVisibleItemPosition == -1) {
                    Log.e("?????? ????????? ??????", String.valueOf(itemTotalCount));
                    Log.e("??? ??????????????? ", String.valueOf(lastVisibleItemPosition));

                    page++;

                    //
                    //???????????? page,limit ??????

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


                //????????? ????????? ???????????? ??????
                try {
                    if (msg_txt.equals("")) {
                        msg_txt = " ";
                    }

                    Message msg = Message.obtain(null, ChatService.MSG_MSG);
                    Bundle bundle = msg.getData();
                    bundle.putString("send", "" + msg_txt);
                    mService.send(msg);
                    Log.e("TAG", "??????????????? ???????????? ?????? :" + "" + msg_txt);
                    //?????????

//                    Message msg_yes_read = Message.obtain(null, ChatService.MSG_YES_READ);
//                    Bundle bundle_yes_read = msg_yes_read.getData();
//                    bundle_yes_read.putString("send", "" + room);
                    // bundle_yes_read.putString("send", "" + room);


                    //mService.send(msg_yes_read);

                    //Log.e("TAG", "????????? ????????? ????????????");
                    //?????????


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
        //??? ???????????? ???????????? ??????
        try {
            clientMsg_list.clear();
            Message msg = Message.obtain(null, ChatService.MSG_IN_ROOM);
            Bundle bundle = msg.getData();
            bundle.putString("send", "" + room);
            bundle.putString("MainChat", "MainChat");

            bundle.putString("page", "" + page);
            bundle.putString("limit", "" + limit);

            Log.e("TAG", "?????????????????? ??????");

            mService.send(msg);
            Log.e("TAG", "????????? ?????? :" + "" + userEmail);
            //??????????????? ????????? ????????? ??? ??????


            Message msg_yes_read = Message.obtain(null, ChatService.MSG_YES_READ);
            Bundle bundle_yes_read = msg_yes_read.getData();
            bundle_yes_read.putString("send", "" + room);
            // bundle_yes_read.putString("send", "" + room);

            mService.send(msg_yes_read);
            Log.e("TAG", "????????? ????????? ????????????");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }



    @Override
    protected void onResume() {
        super.onResume();


        if (mService != null) {
            try {
                Message msg_MAIN_CHAT = Message.obtain(null, ChatService.MSG_MAINCHAT);
                Bundle bundle_MAIN_CHAT = msg_MAIN_CHAT.getData();
                bundle_MAIN_CHAT.putString("MainChat", "MainChat");//?????????????????? ??????????????? ??????
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

        //????????? ????????? ????????? ?????????

        //????????? ?????? ????????? ?????????
        //?????????,??? id

//        try {
//            if(mService == null)
//            {
//                return;
//            }
//            Message msg = Message.obtain(null, ChatService.MSG_YES_READ);
//            Bundle bundle = msg.getData();
//            bundle.putString("send", "" + room);//?????????
//            bundle.putString("userEmail", "" + userEmail);//?????????
//
//            mService.send(msg);
//            Log.e("TAG", "????????? ????????? ????????????");
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {


//            Message msg = Message.obtain(null, ChatService.MSG_YES_READ);
//            Bundle bundle = msg.getData();
//            bundle.putString("send", "" + room);//?????????
//            //bundle.putString("userEmail", "" + userEmail);
//            mService.send(msg);
            Log.e("TAG", "????????? ????????? ????????????");

            ChatCount_PreferenceManager.resetChatCount(getApplicationContext(), room);//????????? ?????? ??????

            noReadCount();

            Message msg_MAIN_CHAT = Message.obtain(null, ChatService.MSG_MAINCHAT);
            Bundle bundle_MAIN_CHAT = msg_MAIN_CHAT.getData();//????????? ???????????? ???????????? ??????
            bundle_MAIN_CHAT.putString("MainChat", "");

            mService.send(msg_MAIN_CHAT);

            Log.e("TAG", "?????????????????? ?????? ??????");


            //???????????? ???????????????
            Message msg_MSG_READ_LINE = Message.obtain(null, ChatService.MSG_READ_LINE);
            Bundle bundle_MSG_READ_LINET = msg_MSG_READ_LINE.getData();
            bundle_MSG_READ_LINET.putString("send", "" + room);

            mService.send(msg_MSG_READ_LINE);

            Log.e("TAG", "?????????????????? ?????? ??????");





        } catch (RemoteException | JSONException e) {
            e.printStackTrace();
        }


    }
    public void noReadCount() throws JSONException {
        try {
            String json_myRoom = myRoom_PreferenceManager.getString(getApplicationContext(), "ROOM");

            JSONArray jsonArray_myRoom = new JSONArray(json_myRoom);//????????? ????????? jsonArray??????

            JSONObject room_Object = new JSONObject(); //?????? ????????? ????????? ?????? ????????????
            for (int j = 0; j < jsonArray_myRoom.length(); j++) {
                JSONObject itme_myRoom = jsonArray_myRoom.getJSONObject(j);//????????? ??????

                String myRoom = itme_myRoom.getString("value");
                Log.e("TAG", "????????? :" + myRoom);


                String json_NO_READ = ChatCount_PreferenceManager.getChatCount(getApplicationContext(), myRoom);
                Log.e("TAG", myRoom + " ???????????? ????????? :" + json_NO_READ);
                if (json_NO_READ != null) {
                    JSONArray jsonArray_NO_READ = new JSONArray(json_NO_READ);

                    room_Object.put(myRoom, jsonArray_NO_READ);
                }


            }
            String NO_READ_Data = room_Object.toString();
            setRoom(NO_READ_Data);
        } catch (Exception e) {

        }

    }

    private void setRoom(String NO_READ_Data) {

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<chatRoom_Model> room_list;

                try {
                    Log.e("??? ????????????", "??? ????????????");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("chat");
                    chatRoom_Model chatRoom_Model;// ?????? ?????? ??????
                    room_list = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject item = jsonArray.getJSONObject(i);

                        String room_ID = item.getString("room_ID");
                        String room_nm = item.getString("room_nm");
                        String member = item.getString("member");
                        //item.getString("mem_count");
                        //String CREATE_DATE = item.getString("CREATE_DATE");
                        int msg_count = 0;
                        String msg = "";
                        String lastTime = "";
                        String mem_count = "";


                        if (NO_READ_Data != null && !NO_READ_Data.equals("")) {
                            JSONObject jsonObject_NO_READ = new JSONObject(NO_READ_Data);


                            try {
                                JSONArray jsonArray1 = jsonObject_NO_READ.getJSONArray(room_nm);
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                                msg_count = Integer.parseInt(jsonObject1.getString("count"));
                                msg = jsonObject1.getString("msg");
                                lastTime = jsonObject1.getString("lastTime");
                                mem_count = jsonObject1.getString("mem_count");


                            } catch (Exception e) {
                                Log.e("TAG", "jsonObject_NO_READ ??? ????????? ????????? ?????? ??????");

                            }


                        }

                        chatRoom_Model = new chatRoom_Model(room_ID, room_nm, member, mem_count, lastTime, msg_count, msg);// ?????? ?????? ??????
                        room_list.add(chatRoom_Model);

                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd/HH:mm:ss");

                    for (int i = 0; i < room_list.size(); i++) {
                        chatRoom_Model chatRoom_Model_swap = room_list.get(i);// ?????? ?????? ??????
                        String time = chatRoom_Model_swap.getLastTime();
                        Date date = null;
                        if (time.equals("")) {//????????? ???????????? ??????????????? ??????
                            for (int j = 0; j < room_list.size(); j++) {
                                String index_0 = room_list.get(j).getLastTime();
                                if (!index_0.equals("")) {
                                    Collections.swap(room_list, i, j);
                                    j = room_list.size() + 1;
                                }
                            }


                        }
                    }

                    for (int i = 0; i < room_list.size(); i++) {
                        int minindex = i; //?????? ?????? ?????? ?????????


                        for (int j = i + 1; j < room_list.size(); j++) {

                            chatRoom_Model chatRoom_Model_swap = room_list.get(minindex);// ?????? ?????? ??????
                            String time = chatRoom_Model_swap.getLastTime();
                            Date date = null;
                            if (!time.equals("")) {
                                date = dateFormat.parse(time);

                            }


                            //if (i + 1 < room_list.size()) {

                            chatRoom_Model chatRoom_Model_swap2 = room_list.get(j);// ?????? ?????? ??????
                            String time2 = chatRoom_Model_swap2.getLastTime();
                            Date date2 = null;
                            if (!time2.equals("")) {
                                date2 = dateFormat.parse(time2);

                            }

                            boolean afterTime = false;
                            //???????????????.after(????????????)
                            //????????? ????????? ??????????????? ????????? ?????? true??? ??????
                            //????????? ?????? ???????????? false??? ??????
                            if (date != null && date2 != null) {
                                afterTime = date.after(date2);

                            } else {
                                //????????? ???????????? ?????? ?????????
                            }

                            //false ?????? ?????? ?????? ??????
                            //true ?????? ??????????????? ???  ?????? ??????????????? ?????????

                            if (afterTime) {
                                try {
                                    minindex = j;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //  }
                        }
                        Collections.swap(room_list, i, minindex);

                    }
                    ((ChatRoomActivity)ChatRoomActivity.context).model.getChatRoom().setValue(room_list);//viewModel ????????????




                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        };
        //String userEmail = PreferenceManager.getString(context, "userEmail");//??????????????? ???????????? ????????? ?????????

        getRoomRequest getRoomRequest = new getRoomRequest(responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRoomRequest);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {


                    //????????? ????????? ??????(Uri)?????? ????????????
                    // Uri uri = data.getData();

                    if (data != null) {
                        if (data.getClipData() == null) {  // ???????????? ????????? ????????? ??????
                            Log.e("single choice: ", String.valueOf(data.getData()));
                            Uri imageUri = data.getData();

                            //bitmap?????? ??????

                            try {
                                encodeUriToBitmap(imageUri);//?????? ?????? encodeImageString ?????? ????????? String ??? ?????????


                                Upload_img(encodeImageString);//????????? DB??? ??????

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            String imgPath = getRealPathFromUri(imageUri);

                            //????????? ????????? ?????????


                            //clientMsg_list.add( "????????????"+imageUri);
                            // adapter.notifyDataSetChanged();


                            //  rv_msgList.scrollToPosition(clientMsg_list.size()-1);
                            Log.d("????????? ??????", "" + imageUri.toString() + "\n" + imgPath);

                        } else {
                            //???????????? ????????? ????????? ??????
                            ClipData clipData = data.getClipData();
                            Log.e("clipData", String.valueOf(clipData.getItemCount()));

                            if (clipData.getItemCount() > 10) {// ????????? ???????????? 11??? ????????? ??????
                                Toast.makeText(getApplicationContext(), "????????? 10????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();

                            } else {//????????? ???????????? 1??? ?????? 10???????????? ??????
                                Log.e("TAG", "multiple choice");
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                                    try {
                                        // clientMsg_list.add("????????????"+imageUri);  //uri??? list??? ?????????.

                                        //????????? ????????? ?????????
                                        encodeUriToBitmap(imageUri);//?????? ?????? encodeImageString ?????? ????????? String ??? ?????????


                                        Upload_img(encodeImageString);//????????? DB??? ??????

                                    } catch (Exception e) {
                                        Log.e("TAG", "File select error", e);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                rv_msgList.scrollToPosition(clientMsg_list.size());

                                //???????????? ??????????????? ?????? ??????????????? ??????

                            }

                        }

                        //??????????????? ????????? . setImageURI(uri);

                        //?????????????????? ???????????? DB????????? ????????? , ????????? ????????? [?????? ?????? ????????? ??????]
                        //????????? Uri??? Gallery?????? DB?????????
                        //???????????? ????????? ???????????? ????????????(????????????: file:// -----/aaa.png)?????????
                        //Uri -->???????????? (String)??????


                        // adapter = new MsgAdapter(clientMsg_list);
                        //Toast.makeText(context, "?????????", Toast.LENGTH_SHORT).show();


                        //????????? ?????? uri ???????????????
                    } else {
                        Toast.makeText(context, "???????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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

                    if (success) { // ????????? ???????????? ??????

                        //????????? ????????? ???????????? ??????
                        try {
                            String lastid = jsonObject.getString("lastid");

                            // Toast.makeText(getApplicationContext(), "?????? ID" + lastid, Toast.LENGTH_SHORT).show();

                            Message msg = Message.obtain(null, ChatService.MSG_MSG);
                            Bundle bundle = msg.getData();
                            bundle.putString("send", "????????? ???????????????.");
                            bundle.putString("lastid", lastid);

                            mService.send(msg);
                            Log.e("TAG", "????????? ???????????? ???????????? ??????");


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // ????????? ????????? ??????
                        //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                        return;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        //String userEmail = PreferenceManager.getString(context, "userEmail");//??????????????? ???????????? ????????? ????????????

        Upload_img_Request upload_img_request = new Upload_img_Request(room, room_ID, userEmail, encodeImageString, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(upload_img_request);
    }

    private void encodeUriToBitmap(Uri uri) throws FileNotFoundException {//?????? ?????? encodeImageString ?????? ????????? String ??? ?????????

        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        //encodeBitmapImage(bitmap);

        encodeImageString = ImageUtil.convert(bitmap);


    }


}