package com.example.volumn.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.volumn.R;

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
    //소켓 입출력객체
    BufferedReader in;
    private DataOutputStream out;
    private DataOutputStream clientOut;
    private DataInputStream clientIn;


    //클라세팅
    ArrayList<String> clientMsg_list;
    private String clientMsg;
    private String nickName;
    ImageView img_send;
    EditText etxt_msgBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        img_send = findViewById(R.id.img_send);
        etxt_msgBox = findViewById(R.id.etxt_msgBox);

        //clientMsg_list = new cl
        connect();
        //
        //sendMsg("안녕");//(대기실)접속 알림

        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = etxt_msgBox.getText().toString();
             //   sendMsg("300|"+msg);
                sendMsg(msg);

                etxt_msgBox.setText("");
            }
        });


    }
    public void protocol(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    while(true){
                        String msg = in.readLine();//msg: 서버가 보낸 메시지

                        //msg==> "300|안녕하세요"  "160|자바방--1,오라클방--1,JDBC방--1"

                        String msgs[] = msg.split("\\|");

                        String protocol = msgs[0];

                        switch(protocol){

                            case "300":

                                 etxt_msgBox.setText(msgs[1]);

//                                cc.ta.append(msgs[1]+"\n");
//
//                                cc.ta.setCaretPosition(cc.ta.getText().length());

                                break;



//                            case "160"://방만들기
//
//                                //방정보를 List에 뿌리기
//
//                                if(msgs.length > 1){
//
//                                    //개설된 방이 한개 이상이었을때 실행
//
//                                    //개설된 방없음 ---->  msg="160|" 였을때 에러
//
//                                    String roomNames[] = msgs[1].split(",");
//
//                                    //"자바방--1,오라클방--1,JDBC방--1"
//
//                                    roomInfo.setListData(roomNames);
//
//                                }
//
//                                break;
//
//
//
//                            case "170"://(대기실에서) 대화방 인원정보
//
//                                String roomInwons[] = msgs[1].split(",");
//
//                                roomInwon.setListData(roomInwons);
//
//                                break;
//
//
//
//                            case "175"://(대화방에서) 대화방 인원정보
//
//                                String myRoomInwons[] = msgs[1].split(",");
//
//                                cc.li_inwon.setListData(myRoomInwons);
//
//                                break;
//
//
//
//                            case "180"://대기실 인원정보
//
//                                String waitNames[] = msgs[1].split(",");
//
//                                waitInfo.setListData(waitNames);
//
//                                break;
//
//
//
//                            case "200"://대화방 입장
//
//                                cc.ta.append("=========["+msgs[1]+"]님 입장=========\n");
//
//                                cc.ta.setCaretPosition(cc.ta.getText().length());
//
//                                break;
//
//
//
//                            case "400"://대화방 퇴장
//
//                                cc.ta.append("=========["+msgs[1]+"]님 퇴장=========\n");
//
//                                cc.ta.setCaretPosition(cc.ta.getText().length());
//
//                                break;
//
//
//
//                            case "202"://개설된 방의 타이틀 제목 얻기
//
//                                cc.setTitle("채팅방-["+msgs[1]+"]");
//
//                                break;





                        }//클라이언트 switch

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void connect(){//(소켓)서버연결 요청

        if(nickName == null){
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

                    protocol();

//
//                    clientOut = new DataOutputStream(s.getOutputStream());
//                    clientIn = new DataInputStream(s.getInputStream());
//
//                    clientOut.writeUTF(nickName);

                    Log.v("", "클라이언트 : 메시지 전송완료");
                    sendMsg("100|");//(대기실)접속 알림

                    //out: 메시지 보내기, 쓰기객체    클라이언트-----msg----->서버

//                    while (in != null) {
//                        try {
//
//                            String msg = in.readLine();
//
//                            //clientMsg = clientIn.readUTF();
//
//                            etxt_msgBox.setText(msg);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                      //  handler.sendEmptyMessage(CLIENT_TEXT_UPDATE);
//                    }

                } catch (UnknownHostException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }
        }).start();



    }//connect

    public void sendMsg(String msg){//서버에게 메시지 보내기
        final String msgs = nickName + ":" + msg + "\n";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.write(  (msg+"\n").getBytes()   );

                 //   clientOut.writeUTF(msgs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }//sendMsg



}