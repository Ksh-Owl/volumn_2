package com.example.volumn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class RegisterActivity extends AppCompatActivity {
    //인증코드
    String GmailCode;
    int mailSend=0;
    static int value;
    MainHandler mainHandler;
    EditText etxt_confirm;
    EditText etxt_userEmail;

    boolean confirm_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button  btn_register;//회원가입버튼

        Button btn_confirm;//인증하기
        Button btn_sendcode;//인증번호 보내기 버튼

        EditText etxt_userPw;
        EditText etxt_userPw2;
        EditText etxt_userName;

        TextView txt_state;//인증여부 텍스트




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = (Button) this.findViewById(R.id.btn_register);
        etxt_userEmail = (EditText) this.findViewById(R.id.etxt_userEmail);
        etxt_userPw = (EditText) this.findViewById(R.id.etxt_userPw);
        etxt_userPw2 = (EditText) this.findViewById(R.id.etxt_userPw2);
        etxt_userName = (EditText) this.findViewById(R.id.etxt_userName);

        btn_sendcode = (Button) this.findViewById(R.id.btn_sendcode);//승인번호 보내기 버튼

        txt_state = (TextView) this.findViewById(R.id.txt_state);
        txt_state.setVisibility(View.GONE);

        btn_confirm = (Button) this.findViewById(R.id.btn_confirm);//승인버튼
        btn_confirm.setVisibility(View.GONE);


        etxt_confirm = (EditText) this.findViewById(R.id.etxt_confirm);//승인번호 입력텍스트
        etxt_confirm.setVisibility(View.GONE);

        //인증이메일 보내기
        btn_sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //메일을 보내주는 쓰레드
                MailTread mailTread = new MailTread();
                mailTread.start();

                if(mailSend==0){
                    value=180;
                    //쓰레드 객체 생성
                    BackgrounThread backgroundThread = new BackgrounThread();
                    //쓰레드 시작
                    backgroundThread.start();
                    mailSend+=1;
                }else{
                    value = 180;
                }

                btn_confirm.setVisibility(View.VISIBLE);
                etxt_confirm.setVisibility(View.VISIBLE);

                //핸들러 객체 생성
                mainHandler=new MainHandler();

            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etxt_confirm.getText().toString().equals(GmailCode)){
                    //인증코드 일치할 경우
                    confirm_check = true;
                    etxt_confirm.setVisibility(View.GONE);
                    btn_confirm.setVisibility(View.GONE);
                    btn_sendcode.setVisibility(View.GONE);

                    txt_state.setVisibility(View.VISIBLE);


                }else
                {
                    Toast.makeText(getApplicationContext(), "인증번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show();

                }
            }
        });



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = etxt_userEmail.getText().toString();
                String userPw = etxt_userPw.getText().toString();
                String userPw2 = etxt_userPw2.getText().toString();
                String userName = etxt_userName.getText().toString();
                //인증완료 체크
                if(!confirm_check)
                {
                    Toast.makeText(getApplicationContext(),"이메일 인증을 해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }


                //이메일 형식체크
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if(!pattern.matcher(userEmail).matches())
                {
                    Toast.makeText(getApplicationContext(),"이메일형식에 맞춰주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                //비밀번호 체크
                if(userPw.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력해 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!userPw.equals(userPw2) )
                {
                    Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }








                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 회원등록에 성공한 경우
                                Toast.makeText(getApplicationContext(),"회원 등록에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, com.example.volumn.loginActivity.class);
                                startActivity(intent);
                            } else { // 회원등록에 실패한 경우
                                Toast.makeText(getApplicationContext(),"이미 가입된 이메일입니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                // 서버로 Volley를 이용해서 요청을 함.
                RegisterRequest registerRequest = new RegisterRequest(userEmail,userPw,userName, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }


        });

    }
    class MailTread extends Thread{

        public void run(){
            GMailSender gMailSender = new GMailSender("ajsl7942@gmail.com", "ddmxajzodvakhgzn");
            //GMailSender.sendMail(제목, 본문내용, 받는사람);


            //인증코드
            GmailCode=gMailSender.getEmailCode();
            try {
                gMailSender.sendMail("볼륨 회원가입 이메일 인증", "회원가입 인증번호는 "+"["+ GmailCode +"]", etxt_userEmail.getText().toString());
            } catch (SendFailedException e) {

            } catch (MessagingException e) {
                System.out.println(e);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //시간초가 카운트 되는 쓰레드
    class BackgrounThread extends Thread{
        //180초는 3분
        //메인 쓰레드에 value를 전달하여 시간초가 카운트다운 되게 한다.

        public void run(){
            //180초 보다 밸류값이 작거나 같으면 계속 실행시켜라
            while(true){
                value-=1;
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }

                Message message = mainHandler.obtainMessage();
                //메세지는 번들의 객체 담아서 메인 핸들러에 전달한다.
                Bundle bundle = new Bundle();
                bundle.putInt("value", value);
                message.setData(bundle);

                //핸들러에 메세지 객체 보내기기

                mainHandler.sendMessage(message);

                if(value<=0){
                    GmailCode="";
                    break;
                }
            }



        }
    }

    //쓰레드로부터 메시지를 받아 처리하는 핸들러
    //메인에서 생성된 핸들러만이 Ui를 컨트롤 할 수 있다.
    class MainHandler extends Handler{
        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
            int min, sec;

            Bundle bundle = message.getData();
            int value = bundle.getInt("value");

            min = value/60;
            sec = value % 60;
            //초가 10보다 작으면 앞에 0이 더 붙어서 나오도록한다.
            if(sec<10){
                //텍스트뷰에 시간초가 카운팅
                etxt_confirm.setHint("0"+min+" : 0"+sec);
            }else {
                etxt_confirm.setHint("0"+min+" : "+sec);
            }
        }
    }
}