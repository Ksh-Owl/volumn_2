package com.example.volumn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.os.Bundle;
import com.example.volumn.calendar.CalendarActivity;
import com.example.volumn.include.PreferenceManager;



public class MainActivity extends AppCompatActivity {
    PreferenceManager preferenceManager ;

    Button btn_logout;
    Button btn_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_logout = findViewById(R.id.btn_logout);
        btn_log = findViewById(R.id.btn_log);

        preferenceManager = new PreferenceManager();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.deleteString(v.getContext(),"userEmail");

                Intent intent = new Intent(MainActivity.this, loginActivity.class);//명시적 인텐트
                startActivity(intent);
            }
        });

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);//명시적 인텐트
                startActivity(intent);

            }
        });


        //로그인 정보 받아오기
        String userEmail = preferenceManager.getString(this, "userEmail");

        Toast.makeText(getApplicationContext(),""+userEmail,Toast.LENGTH_SHORT).show();

        if(userEmail == "" || userEmail == null )
        {
            Intent intent = new Intent(MainActivity.this, loginActivity.class);//명시적 인텐트
            startActivity(intent);
        }

    }
}