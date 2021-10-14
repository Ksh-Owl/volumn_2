package com.example.volumn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.addSet.addSetActivity;
import com.example.volumn.addSet.addSetAdapter;
import com.example.volumn.addSet.addSetRequest;
import com.example.volumn.addWorkout.Model;
import com.example.volumn.calendar.CalendarActivity;
import com.example.volumn.home.MainRequest;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    PreferenceManager preferenceManager;

    Button btn_logout;
    Button btn_log;
    EditText etxt_Date;
    EditText etxt_Date2;
    Context context;

    TextView txt_chest;
    TextView txt_back;
    TextView txt_leg;
    TextView txt_hip;
    TextView txt_shoulder;
    TextView txt_abs;
    TextView txt_biceps;
    TextView txt_triceps;
    TextView txt_trapezius;
    TextView txt_sum;

    int year_  ;
    int month_  ;
    int dayofMonth_ ;


    private DatePickerDialog.OnDateSetListener callbackMethod1;
    private DatePickerDialog.OnDateSetListener callbackMethod2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        btn_logout = findViewById(R.id.btn_logout);
        btn_log = findViewById(R.id.btn_log);


        etxt_Date = (EditText) findViewById(R.id.etxt_Date);
        etxt_Date2 = (EditText) findViewById(R.id.etxt_Date2);

        txt_sum = (TextView) findViewById(R.id.txt_sum);

        txt_chest = (TextView) findViewById(R.id.txt_chest);
        txt_back = (TextView) findViewById(R.id.txt_back);
        txt_leg = (TextView) findViewById(R.id.txt_leg);
        txt_hip = (TextView) findViewById(R.id.txt_hip);
        txt_shoulder = (TextView) findViewById(R.id.txt_shoulder);
        txt_abs = (TextView) findViewById(R.id.txt_abs);
        txt_biceps = (TextView) findViewById(R.id.txt_biceps);
        txt_triceps = (TextView) findViewById(R.id.txt_triceps);
        txt_trapezius = (TextView) findViewById(R.id.txt_trapezius);


        preferenceManager = new PreferenceManager();
        this.InitializeListener();


        //로그아웃
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.deleteString(v.getContext(), "userEmail");

                Intent intent = new Intent(MainActivity.this, loginActivity.class);//명시적 인텐트
                startActivity(intent);
            }
        });
        //일지
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);//명시적 인텐트
                startActivity(intent);

            }
        });

        //첫번째 날짜
        etxt_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDate();

                DatePickerDialog dialog = new DatePickerDialog(context, callbackMethod1, year_, month_, dayofMonth_);
                dialog.show();
            }
        });
        //두번째 날짜
        etxt_Date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDate();

                DatePickerDialog dialog = new DatePickerDialog(context, callbackMethod2, year_, month_, dayofMonth_);
                dialog.show();
            }
        });


        //로그인 정보 받아오기
        String userEmail = preferenceManager.getString(this, "userEmail");

        Toast.makeText(getApplicationContext(), "" + userEmail, Toast.LENGTH_SHORT).show();

        if (userEmail == "" || userEmail == null) {
            Intent intent = new Intent(MainActivity.this, loginActivity.class);//명시적 인텐트
            startActivity(intent);
        }


        etxt_Date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_Volumn();

                //각 부위별 볼륨 합 구하고 출력

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etxt_Date2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_Volumn();

                //각 부위별 볼륨 합 구하고 출력

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        setDate();
        // 첫번째 날짜 오늘부터 1주일전

        etxt_Date.setText(year_+"-"+(month_+1)+ "-"+(dayofMonth_-7));
        // 시작시 두번째 날짜 오늘날짜

        etxt_Date2.setText(year_+"-"+(month_+1)+ "-"+dayofMonth_);

        search_Volumn();


    }


    public void InitializeListener() {
        callbackMethod1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear +1;

                etxt_Date.setText(year + "-" + monthOfYear + "-" + dayOfMonth + "");
            }
        };

        callbackMethod2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear +1;

                etxt_Date2.setText(year + "-" + monthOfYear + "-" + dayOfMonth + "");

            }
        };
    }
    public void search_Volumn(){
        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("workout");
                    String workout_Part ="";
                    String volumn ="0";
                    refreshText();
                    for (int i = 0; i <jsonArray.length(); i++) {

                            JSONObject item = jsonArray.getJSONObject(i);

                            workout_Part = item.getString("workout_Part");
                            volumn = item.getString("volumn");




                        switch (workout_Part) {
                            case "가슴":
                                txt_chest.setText(volumn + " KG");

                                break;
                            case "등":
                                txt_back.setText(volumn + " KG");

                                break;
                            case "하체":
                                txt_leg.setText(volumn + " KG");

                                break;
                            case "어깨":
                                txt_shoulder.setText(volumn + " KG");

                                break;
                            case "이두":
                                txt_biceps.setText(volumn + " KG");

                                break;
                            case "삼두":
                                txt_triceps.setText(volumn + " KG");

                                break;
                            case "엉덩이":
                                txt_hip.setText(volumn + " KG");

                                break;
                            case "승모근":
                                txt_trapezius.setText(volumn + " KG");

                                break;
                            case "복근":
                                txt_abs.setText(volumn + " KG");

                                break;


                        }


                    }

                    sumVolumn();//볼륨 합계구하기

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        MainRequest mainRequest = new MainRequest(userEmail, etxt_Date.getText().toString().trim(), etxt_Date2.getText().toString().trim(), responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(mainRequest);
    }
    public void setDate(){
        //오늘날짜구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");

        SimpleDateFormat dayofMonth = new SimpleDateFormat("dd");

         year_ = Integer.parseInt(year.format(date)) ;
         month_ =Integer.parseInt(month.format(date))  ;
         month_ = month_-1;
         dayofMonth_ = Integer.parseInt(dayofMonth.format(date)) ;
    }
    private void refreshText(){
        txt_chest.setText("0 KG");
        txt_back.setText("0 KG");
        txt_hip.setText("0 KG");
        txt_leg.setText("0 KG");

        txt_abs.setText("0 KG");
        txt_biceps.setText("0 KG");
        txt_shoulder.setText("0 KG");
        txt_trapezius.setText("0 KG");
        txt_triceps.setText("0 KG");


    }
    private void sumVolumn(){
        int chest = Integer.parseInt(txt_chest.getText().toString().replace("KG","").trim()) ;
        int back = Integer.parseInt(txt_back.getText().toString().replace("KG","").trim()) ;

        int hip = Integer.parseInt(txt_hip.getText().toString().replace("KG","").trim()) ;

        int leg = Integer.parseInt(txt_leg.getText().toString().replace("KG","").trim()) ;

        int abs = Integer.parseInt(txt_abs.getText().toString().replace("KG","").trim()) ;

        int biceps = Integer.parseInt(txt_biceps.getText().toString().replace("KG","").trim()) ;

        int shoulder = Integer.parseInt(txt_shoulder.getText().toString().replace("KG","").trim()) ;
        int trapezius = Integer.parseInt(txt_trapezius.getText().toString().replace("KG","").trim()) ;
        int triceps = Integer.parseInt(txt_triceps.getText().toString().replace("KG","").trim()) ;


        int volumn = chest+back+hip+leg+abs+biceps+shoulder+trapezius+triceps;

        txt_sum.setText(volumn+" KG");

    }


}