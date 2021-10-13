package com.example.volumn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.include.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;


public class loginActivity extends AppCompatActivity {

    // Google Sign In API와 호출할 구글 로그인 클라이언트
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;
    private static final String TAG = "loginActivity";

    SignInButton btn_google;


    TextView txt_join;
    Button btn_login;
    TextView etxt_Email;
    TextView etxt_Pw;
    TextView txt_findPw;//비밀번호 찾기
    PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_google = findViewById(R.id.btn_google);

        txt_join = (TextView) this.findViewById(R.id.txt_join);

        //btn_login_gg = (Button) this.findViewById(R.id.btn_login_gg);
        etxt_Email = (EditText) this.findViewById(R.id.etxt_Email);
        etxt_Pw = (EditText) this.findViewById(R.id.etxt_Pw);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        txt_findPw = (TextView) this.findViewById(R.id.txt_findPw); //비밀번호 찾기

        preferenceManager = new PreferenceManager();


        //구글 로그인
        TextView textView = (TextView) btn_google.getChildAt(0);
        String sign = "Google계정으로 로그인";
        textView.setText(sign);

        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(loginActivity.this, gso);

        // 기존에 로그인 했던 계정을 확인한다.
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(loginActivity.this);

        // 로그인 되있는 경우 (토큰으로 로그인 처리)
        if (gsa != null && gsa.getId() != null) {

        }

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        //구글 로그인







        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                String Email = etxt_Email.getText().toString();
                String Pw = etxt_Pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success)
                            {
                                String userEmail = jsonObject.getString("userEmail");
                                Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(loginActivity.this, MainActivity.class);

                                intent.putExtra("userEmail",userEmail);


                                preferenceManager.setString(loginActivity.this,"userEmail",userEmail);

                                startActivity(intent);


                            }else
                            {
                                Toast.makeText(getApplicationContext(),"로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                com.example.volumn.LoginRequest loginRequest = new com.example.volumn.LoginRequest(Email, Pw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(loginActivity.this);
                queue.add(loginRequest);
            }

        });

        txt_findPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, com.example.volumn.findPwActivity.class);//명시적 인텐트
                startActivity(intent);
            }
        });


        txt_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, RegisterActivity.class);//명시적 인텐트
                startActivity(intent);
            }
        });

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
                Log.d(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success)
                            {
                                String userEmail = jsonObject.getString("userEmail");
                                Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(loginActivity.this, MainActivity.class);

                                //intent.putExtra("userEmail",userEmail);


                                //preferenceManager.setString(loginActivity.this,"userEmail",userEmail);

                               // startActivity(intent);


                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                intent.putExtra("userEmail",personEmail);
                                preferenceManager.setString(loginActivity.this,"userEmail",personEmail);//로그인 정보 쉐어드 저장

                                startActivity(intent);


                            }else
                            {   //최초 구글로그인시 회원가입
                                google_Register(personEmail,personName);

                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                intent.putExtra("userEmail",personEmail);
                                preferenceManager.setString(loginActivity.this,"userEmail",personEmail);//로그인 정보 쉐어드 저장

                                startActivity(intent);
                               // return;
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                googleLoginRequest loginRequest = new googleLoginRequest(personEmail,personName,responseListener);
                RequestQueue queue = Volley.newRequestQueue(loginActivity.this);
                queue.add(loginRequest);








            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_google:
//                signIn();
//                break;
//            case R.id.logoutBt:
//                mGoogleSignInClient.signOut()
//                        .addOnCompleteListener(this, task -> {
//                            Log.d(TAG, "onClick:logout success ");
//                            mGoogleSignInClient.revokeAccess()
//                                    .addOnCompleteListener(this, task1 -> Log.d(TAG, "onClick:revokeAccess success "));
//
//                        });
//                break;

//        }
//    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void google_Register(String userEmail,String userName)
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) { // 회원등록에 성공한 경우
                        Toast.makeText(getApplicationContext(),"회원 등록에 성공하였습니다.",Toast.LENGTH_SHORT).show();
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
        com.example.volumn.googleRegisterRequest registerRequest = new com.example.volumn.googleRegisterRequest(userEmail,userName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(loginActivity.this);
        queue.add(registerRequest);


    }
}