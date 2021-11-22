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




}