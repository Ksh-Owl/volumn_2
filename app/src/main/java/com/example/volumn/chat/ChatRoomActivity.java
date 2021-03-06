package com.example.volumn.chat;

import com.example.volumn.include.ChatCount_PreferenceManager;

import org.json.JSONArray;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.include.myRoom_PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ChatRoomActivity extends AppCompatActivity implements LifecycleOwner {

    public static Context context;

    public BufferedReader in;
    public DataOutputStream out;
    public Socket s;
    Button btn_newRoom;
    RecyclerView rv_room;
    private ChatRoom_list_model chatRoom_list;
    public ArrayList<chatRoom_Model> room_list;
    public ArrayList<chatRoom_Model> room_list_new;//새로온 메시지
    public ChatRoom_list_model chatRoomList;
    public static String inRoom_name = "";

    ImageView img_back4;
    //서비스
    private Messenger mService;
    private final Messenger mMessenger = new Messenger(new IncomingHandelr());


    //라이브 데이터
    //private MutableLiveData<ArrayList<chatRoom_Model>> liveData = new MutableLiveData<>();
    public ChatRoom_list_model model;

    private class IncomingHandelr extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {

            //메시지 값에 따라
            //서비스에서 메시지 오면 반응
            switch (msg.what) {
                case ChatService.MSG_RESPONSE:


                    Bundle bundle = msg.getData();
                    String response = bundle.getString("response");
                    String roomInwon = bundle.getString("roomInwon");

                    Log.e("TAG", "response:" + response);
                    if (response.equals("160")) {
                        // setRoom("","","");

                        Log.e("TAG", "방업데이트");
                        Log.e("인원 정보", "방업데이트");

                        String inwon[] = roomInwon.split(",");

                        room_list_new = new ArrayList<>();
                        //room_list = new ArrayList<>();

                        for (int i = 0; i < inwon.length; i++) {
                            String room_inwon[] = inwon[i].split("--");

                            String room = room_inwon[0];
                            String _inwon = room_inwon[1];
                            if (room_list.size() <= i) {

                                //inwon.length 보다 room_list가 작으면 반환
                                return;
                            }
                            String room_nm = room_list.get(i).getRoom_nm();

                            if (room.equals(room_nm)) {
                                String room_ID = room_list.get(i).getRoom_ID();
                                String member = room_list.get(i).getMember();

                                String lastTime = room_list.get(i).getLastTime();
                                int msg_count = room_list.get(i).getMsg_count();
                                String msgs = room_list.get(i).getLast_msg();
                                String mem_count = "";
                                if (!msgs.equals("")) {
                                    mem_count = _inwon;


                                    room_list.get(i).setMem_count(_inwon);
                                }


                                chatRoom_Model chatRoom_Model = new chatRoom_Model(room_ID, room_nm, member, mem_count, lastTime, msg_count, msgs);// 모델 객체 생성
                             //   room_list_new.add(chatRoom_Model);
                               // roo.add(chatRoom_Model);

                            }

                        }

//                        RoomAdapter adapter = new RoomAdapter(room_list_new);
//
//
//                        adapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(View v, int position) {
//
//
//                                String roomName = room_list_new.get(position).getRoom_nm();
//                                String room_ID = room_list_new.get(position).getRoom_ID();
//
//
//                                Intent intent = new Intent(context, MainChatActivity.class);
//                                intent.putExtra("roomName", roomName);
//                                intent.putExtra("room_ID", room_ID);
//
//                                startActivity(intent);
//
//
//                            }
//                        });
//
//                        rv_room.setAdapter(adapter);
//
//                        rv_room.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
//                        adapter.notifyDataSetChanged();


                    }
                    try {
                        Message msg2 = Message.obtain(null, ChatService.MSG_REQUEST_NO_READ_COUNT);
                        Bundle bundle2 = msg2.getData();
                        bundle2.putString("send", "");
                        if (mService != null) {
                            //방업데이트 요청

                            mService.send(msg2);
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
//                case ChatService.MSG_SENDMSG:
//                    Log.e("TAG","방업데이트");
//
//                    Bundle bundle1 = msg.getData();
//                    String response1 = bundle1.getString("response");
//                    String title = bundle1.getString("title");
//                    String json = ChatCount_PreferenceManager.getChatCount(context,title);
//
//                    //방 메시지 증가
//                    setRoom();
//
//
//                    break;
                case ChatService.MSG_NO_READ_COUNT:
                    Log.e("TAG", "방업데이트");

                    Bundle bundle_NO_READ = msg.getData();
                    String NO_READ_Data = bundle_NO_READ.getString("NO_READ_Data");


                    //방인원 정보


                    //방 메시지 증가
                    setRoom(NO_READ_Data);
                    //여기서 방순서 변경할것


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
            try {
                Message msg = Message.obtain(null, ChatService.MSG_CHATROOM_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);


                Message msg2 = Message.obtain(null, ChatService.MSG_REQUEST_NO_READ_COUNT);
                Bundle bundle = msg2.getData();
                bundle.putString("send", "");
                mService.send(msg2);


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        chatRoomList = new ChatRoom_list_model();
        //데이터 바인딩
        //ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

       // setContentView(binding.getRoot());

        model = new ViewModelProvider(this).get(ChatRoom_list_model.class);

        final androidx.lifecycle.Observer<ArrayList<chatRoom_Model>> roomObserver = new androidx.lifecycle.Observer<ArrayList<chatRoom_Model>>() {
            @Override
            public void onChanged(ArrayList<chatRoom_Model> chatRoom) {

                Log.d("LiveData","방리스트 변경");

                RoomAdapter adapter = new RoomAdapter(chatRoom);


                adapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {


                        String roomName = chatRoom.get(position).getRoom_nm();
                        String room_ID = chatRoom.get(position).getRoom_ID();
                        inRoom_name = roomName;//들어간 방 이름

                        Intent intent = new Intent(context, MainChatActivity.class);
                        intent.putExtra("roomName", roomName);
                        intent.putExtra("room_ID", room_ID);

                        startActivity(intent);


                    }
                });

                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);

                rv_room.setLayoutManager(mLayoutManager);


                rv_room.setAdapter(adapter);
                // rv_room.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, true));

                adapter.notifyDataSetChanged();
            }
        };

        model.getChatRoom().observe(this,roomObserver);



        getLifecycle().addObserver(new Observer());
        rv_room = (RecyclerView) findViewById(R.id.rv_room);
        rv_room.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));


        btn_newRoom = (Button) findViewById(R.id.btn_newRoom);
        context = this;
        img_back4 = (ImageView) findViewById(R.id.img_back4);
        img_back4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        //LifeCycle aware

    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(this, ChatService.class), conn, Context.BIND_AUTO_CREATE);


//        try {
//            Message msg = Message.obtain(null, ChatService.MSG_REQUEST_NO_READ_COUNT);
//            Bundle bundle = msg.getData();
//            bundle.putString("send", "");
//            if (mService != null) {
//                //방업데이트 요청
//
//                mService.send(msg);
//
//            }
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            //  noReadCount();

        } catch (Exception e) {

        }
    }

    public void noReadCount() throws JSONException {
        try {
            String json_myRoom = myRoom_PreferenceManager.getString(getApplicationContext(), "ROOM");

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
            String NO_READ_Data = room_Object.toString();
            setRoom(NO_READ_Data);
        } catch (Exception e) {

        }

    }

    private void setRoom(String NO_READ_Data) {

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("방 업데이트", "방 업데이트");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("chat");
                    chatRoom_Model chatRoom_Model;// 모델 객체 생성
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
                                Log.e("TAG", "jsonObject_NO_READ 에 이름이 없어서 오류 발생");

                            }


                        }

                        chatRoom_Model = new chatRoom_Model(room_ID, room_nm, member, mem_count, lastTime, msg_count, msg);// 모델 객체 생성
                        room_list.add(chatRoom_Model);

                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd/HH:mm:ss");

                    for (int i = 0; i < room_list.size(); i++) {
                        chatRoom_Model chatRoom_Model_swap = room_list.get(i);// 모델 객체 생성
                        String time = chatRoom_Model_swap.getLastTime();
                        Date date = null;
                        if (time.equals("")) {//시간이 공백이면 마지막으로 이동
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
                        int minindex = i; //가장 작은 시간 인덱스


                        for (int j = i + 1; j < room_list.size(); j++) {

                            chatRoom_Model chatRoom_Model_swap = room_list.get(minindex);// 모델 객체 생성
                            String time = chatRoom_Model_swap.getLastTime();
                            Date date = null;
                            if (!time.equals("")) {
                                date = dateFormat.parse(time);

                            }


                            //if (i + 1 < room_list.size()) {

                            chatRoom_Model chatRoom_Model_swap2 = room_list.get(j);// 모델 객체 생성
                            String time2 = chatRoom_Model_swap2.getLastTime();
                            Date date2 = null;
                            if (!time2.equals("")) {
                                date2 = dateFormat.parse(time2);

                            }

                            boolean afterTime = false;
                            //비교할시간.after(기준시간)
                            //비교할 시간이 기준시간을 지났을 경우 true를 반환
                            //지났지 않을 경우에는 false를 반환
                            if (date != null && date2 != null) {
                                afterTime = date.after(date2);

                            } else {
                                //날짜가 비어있는 방은 밑으로
                            }

                            //false 이면 좀더 최신 유지
                            //true 이면 기준시간이 더  최신 기준시간을 앞으로

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


                    RoomAdapter adapter = new RoomAdapter(room_list);


                    adapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {


                            String roomName = room_list.get(position).getRoom_nm();
                            String room_ID = room_list.get(position).getRoom_ID();
                            inRoom_name = roomName;//들어간 방 이름

                            Intent intent = new Intent(context, MainChatActivity.class);
                            intent.putExtra("roomName", roomName);
                            intent.putExtra("room_ID", room_ID);

                            startActivity(intent);


                        }
                    });

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                    mLayoutManager.setReverseLayout(true);
                    mLayoutManager.setStackFromEnd(true);

                    rv_room.setLayoutManager(mLayoutManager);


                    rv_room.setAdapter(adapter);
                    // rv_room.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, true));

                    adapter.notifyDataSetChanged();


                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        };
        //String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오

        getRoomRequest getRoomRequest = new getRoomRequest(responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRoomRequest);
    }


}

