package com.example.volumn;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.calendar.CalendarActivity;
import com.example.volumn.chat.ChatRoomActivity;
import com.example.volumn.chat.ChatService;
import com.example.volumn.chat.MainChatActivity;
import com.example.volumn.chat.RestartService;
import com.example.volumn.chat.chatActivity;
import com.example.volumn.home.MainRequest;
import com.example.volumn.include.PreferenceManager;
import com.example.volumn.login.loginActivity;
import com.example.volumn.rank.RankActivity;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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


    TextView txt_calChest;
    TextView txt_calBack;
    TextView txt_calShoulder;
    TextView txt_calLeg;
    TextView txt_calBiceps;
    TextView txt_calTriceps;
    TextView txt_calHip;
    TextView txt_calTrapezius;
    TextView txt_calAbs;
    TextView txt_calVolumn;


    int year_;
    int month_;
    int dayofMonth_;

    RestartService restartService;
    private DatePickerDialog.OnDateSetListener callbackMethod1;
    private DatePickerDialog.OnDateSetListener callbackMethod2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        //채팅서비스
      //  Intent intent_service = new Intent(context, ChatService.class);
       // startService(intent_service);

        initData();
        //채팅서비스


        this.settingSideNavBar();

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


        txt_calChest = (TextView) findViewById(R.id.txt_calChest);
        txt_calBack = (TextView) findViewById(R.id.txt_calBack);
        txt_calShoulder = (TextView) findViewById(R.id.txt_calShoulder);
        txt_calLeg = (TextView) findViewById(R.id.txt_calLeg);
        txt_calBiceps = (TextView) findViewById(R.id.txt_calBiceps);
        txt_calTriceps = (TextView) findViewById(R.id.txt_calTriceps);
        txt_calHip = (TextView) findViewById(R.id.txt_calHip);
        txt_calTrapezius = (TextView) findViewById(R.id.txt_calTrapezius);
        txt_calAbs = (TextView) findViewById(R.id.txt_calAbs);
        txt_calVolumn = (TextView) findViewById(R.id.txt_calVolumn);


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

        setDate();
        // 첫번째 날짜 오늘부터 1주일전

        etxt_Date.setText(year_ + "-" + (month_ + 1) + "-" + (dayofMonth_ - 7));
        etxt_Date2.setText(year_ + "-" + (month_ + 1) + "-" + dayofMonth_);

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

        // 시작시 두번째 날짜 오늘날짜


    }


    /***
     *  -> 사이드 네브바 세팅
     *   - 클릭 아이콘 설정
     *   - 아이템 클릭 이벤트 설정
     */
    public void settingSideNavBar() {
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 사이드 메뉴를 오픈하기위한 아이콘 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_48);

        // 사이드 네브바 구현
        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );


        // -> 사이드 네브바 아이템 클릭 이벤트 설정
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.menu_item1) {
                    Intent intent = new Intent(MainActivity.this, RankActivity.class);//명시적 인텐트
                    startActivity(intent);

                } else if (id == R.id.menu_item2) {
                   // Toast.makeText(getApplicationContext(), "채팅", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);//명시적 인텐트
                    startActivity(intent);
                } else if (id == R.id.menu_item3) {
                    Toast.makeText(getApplicationContext(), "프로필설정", Toast.LENGTH_SHORT).show();
                }


                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }


    /***
     *  -> 뒤로가기시, 사이드 네브바 닫는 기능
     */


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        search_Volumn();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("MainActivity","onDestroy");
        //브로드 캐스트 해제
        unregisterReceiver(restartService);


    }

    public void InitializeListener() {
        callbackMethod1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear + 1;

                etxt_Date.setText(year + "-" + monthOfYear + "-" + dayOfMonth + "");
            }
        };

        callbackMethod2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear + 1;

                etxt_Date2.setText(year + "-" + monthOfYear + "-" + dayOfMonth + "");

            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void search_Volumn() {
        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int CalVolumn = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("workout");
                    JSONArray Ex_jsonArray = jsonObject.getJSONArray("cal");

                    String workout_Part = "";
                    String volumn = "0";
                    refreshText();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int cal = 0;//지난 날짜와 차이
                        Boolean check = false;

                        JSONObject item = jsonArray.getJSONObject(i);

                        workout_Part = item.getString("workout_Part");
                        volumn = item.getString("volumn");
                        for (int j = 0; j < Ex_jsonArray.length(); j++) {
                            JSONObject ex_item = Ex_jsonArray.getJSONObject(j);
                            String ex_volumn = ex_item.getString("volumn");

                            if (workout_Part.equals(ex_item.getString("workout_Part"))) {
                                cal = Integer.parseInt(volumn) - Integer.parseInt(ex_volumn);
                                check = true;
                            }


                        }


                        switch (workout_Part) {
                            case "가슴":


                                changeTextColor(volumn, cal, txt_chest, txt_calChest);

                                CalVolumn += cal;
                                break;
                            case "등":

                                changeTextColor(volumn, cal, txt_back, txt_calBack);

                                CalVolumn += cal;

                                break;
                            case "하체":

                                changeTextColor(volumn, cal, txt_leg, txt_calLeg);

                                CalVolumn += cal;

                                break;
                            case "어깨":

                                changeTextColor(volumn, cal, txt_shoulder, txt_calShoulder);

                                CalVolumn += cal;

                                break;
                            case "이두":

                                changeTextColor(volumn, cal, txt_biceps, txt_calBiceps);

                                CalVolumn += cal;

                                break;
                            case "삼두":

                                changeTextColor(volumn, cal, txt_triceps, txt_calTriceps);

                                CalVolumn += cal;

                                break;
                            case "엉덩이":

                                changeTextColor(volumn, cal, txt_hip, txt_calHip);

                                CalVolumn += cal;

                                break;
                            case "승모근":

                                changeTextColor(volumn, cal, txt_trapezius, txt_calTrapezius);

                                CalVolumn += cal;

                                break;
                            case "복근":

                                changeTextColor(volumn, cal, txt_abs, txt_calAbs);

                                CalVolumn += cal;

                                break;


                        }


                    }
                    // txt_calVolumn.setText(String.valueOf(CalVolumn));


                    changeTextColor(String.valueOf(sumVolumn()), CalVolumn, txt_sum, txt_calVolumn);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기
        String calday1 = calFirstDay();
        String calday2 = calSecondDay();
        MainRequest mainRequest = new MainRequest(userEmail, etxt_Date.getText().toString().trim(), etxt_Date2.getText().toString().trim(), calday1, calday2, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(mainRequest);
    }

    public void setDate() {
        //오늘날짜구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");

        SimpleDateFormat dayofMonth = new SimpleDateFormat("dd");

        year_ = Integer.parseInt(year.format(date));
        month_ = Integer.parseInt(month.format(date));
        month_ = month_ - 1;
        dayofMonth_ = Integer.parseInt(dayofMonth.format(date));
    }

    private void refreshText() {
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

    private int sumVolumn() {
        int chest = Integer.parseInt(txt_chest.getText().toString().replace("KG", "").trim());
        int back = Integer.parseInt(txt_back.getText().toString().replace("KG", "").trim());

        int hip = Integer.parseInt(txt_hip.getText().toString().replace("KG", "").trim());

        int leg = Integer.parseInt(txt_leg.getText().toString().replace("KG", "").trim());

        int abs = Integer.parseInt(txt_abs.getText().toString().replace("KG", "").trim());

        int biceps = Integer.parseInt(txt_biceps.getText().toString().replace("KG", "").trim());

        int shoulder = Integer.parseInt(txt_shoulder.getText().toString().replace("KG", "").trim());
        int trapezius = Integer.parseInt(txt_trapezius.getText().toString().replace("KG", "").trim());
        int triceps = Integer.parseInt(txt_triceps.getText().toString().replace("KG", "").trim());


        int volumn = chest + back + hip + leg + abs + biceps + shoulder + trapezius + triceps;

        // txt_sum.setText(volumn+" KG");

        return volumn;

    }

    private int calDays() {
        int Days = 0;
        try {
            // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.

            Date FirstDate = format.parse(etxt_Date.getText().toString().trim());
            Date SecondDate = format.parse(etxt_Date2.getText().toString().trim());


            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = FirstDate.getTime() - SecondDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / (24 * 60 * 60 * 1000);

            // calDateDays = Math.abs(calDateDays);//차일 일수
            Days = (int) Math.abs(calDateDays);//차일 일수


        } catch (Exception ex) {

        }
        return Days;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String calFirstDay() {
        //특정날짜 처음 날짜 두번째 날짜 차이전의 날
        Calendar currentCalendar = Calendar.getInstance();
        String Date = "";


        if (!etxt_Date.getText().toString().equals("") && !etxt_Date2.getText().toString().equals("")) {
            try {

                // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.

                Date FirstDate = format.parse(etxt_Date.getText().toString().trim());


                //캘린더에 특정날짜 넣기

                Date date = new Date(String.valueOf(FirstDate));

                SimpleDateFormat year = new SimpleDateFormat("yyyy");
                SimpleDateFormat month = new SimpleDateFormat("MM");

                SimpleDateFormat dayofMonth = new SimpleDateFormat("dd");


                int year_ = Integer.parseInt(year.format(date));
                int month_ = Integer.parseInt(month.format(date));

                int dayofMonth_ = Integer.parseInt(dayofMonth.format(date));

                currentCalendar.set(year_, month_ - 1, dayofMonth_);


                currentCalendar.add(currentCalendar.DATE, -calDays());

                java.util.Date weekago = currentCalendar.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",

                        Locale.getDefault());

                Date = formatter.format(weekago);


            } catch (ParseException e) {
                // 예외 처리
            }

        }
        return Date;


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String calSecondDay() {
        //특정날짜 처음 날짜 두번째 날짜 차이전의 날
        Calendar currentCalendar = Calendar.getInstance();
        String Date = "";


        if (!etxt_Date.getText().toString().equals("") && !etxt_Date2.getText().toString().equals("")) {
            try {

                // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.

                Date FirstDate = format.parse(calFirstDay());


                //캘린더에 특정날짜 넣기

                //Date date = new Date(String.valueOf(SecondDate));

                SimpleDateFormat year = new SimpleDateFormat("yyyy");
                SimpleDateFormat month = new SimpleDateFormat("MM");

                SimpleDateFormat dayofMonth = new SimpleDateFormat("dd");


                int year_ = Integer.parseInt(year.format(FirstDate));
                int month_ = Integer.parseInt(month.format(FirstDate));

                int dayofMonth_ = Integer.parseInt(dayofMonth.format(FirstDate));

                currentCalendar.set(year_, month_ - 1, dayofMonth_);


                currentCalendar.add(currentCalendar.DATE, (calDays() - 1));

                java.util.Date weekago = currentCalendar.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",

                        Locale.getDefault());

                Date = formatter.format(weekago);


            } catch (ParseException e) {
                // 예외 처리
            }

        }
        return Date;


    }

    private void changeTextColor(String volumn, int cal, TextView volumn_, TextView cal_) {

        volumn_.setText(volumn + " KG");

        cal_.setText(String.valueOf(cal));

        if (cal >= 0) {
            cal_.setTextColor(Color.parseColor("#3F51B5"));

        } else {
            cal_.setTextColor(Color.parseColor("#F44336"));

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initData(){
        //리스타트 서비스 생성
         restartService = new RestartService();

        Intent intent =new Intent(MainActivity.this,ChatService.class);

        IntentFilter intentFilter = new IntentFilter("com.example.volumn.chat.ChatService");

        //브로드 캐스트에 등록
        registerReceiver(restartService,intentFilter);

        //서비스 시작
        startForegroundService(intent);
    }

}