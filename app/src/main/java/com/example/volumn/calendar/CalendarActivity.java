package com.example.volumn.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.addSet.addSetActivity;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private ArrayList<String> check;
    Context context ;


    RecyclerView rv;
    TextView txt_add;//운동추가하기 텍스트

    ArrayList<workoutRecord_itemData> oData;//운동기록 출력 리스트
    workoutRecord_itemData oItem;//운동기록 변수클래스
    TextView txt_date;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initWidgets();
        this.context = this;
//        selectedDate = LocalDate.now();
//        setMonthView();
//
//        oData = new ArrayList<workoutRecord_itemData>();//리스트 객체생성
//        oItem = new workoutRecord_itemData();//변수 객체생성
//
//        long now = System.currentTimeMillis();
//        Date date = new Date(now);
//
//        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String time = mFormat.format(date);
//
//
//        txt_date.setText(time);
//
//        Set_bot(getDay(selectedDate));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        selectedDate = LocalDate.now();
        setMonthView();

        oData = new ArrayList<workoutRecord_itemData>();//리스트 객체생성
        oItem = new workoutRecord_itemData();//변수 객체생성

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = mFormat.format(date);


        txt_date.setText(time);

        Set_bot(getDay(selectedDate));

    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        txt_date = findViewById(R.id.txt_date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

       //check 데이터 채우기

        check = new ArrayList<>();


        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("workout");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String DATE = item.getString("DATE");


                        check.add(DATE);

                    }

                    set(daysInMonth);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        String userEmail = PreferenceManager.getString(this, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        CalendarCheckRequest calendarCheckRequest = new CalendarCheckRequest(userEmail, monthYearFromDate_m(selectedDate), responseListner);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(calendarCheckRequest);





    }
    private void set(ArrayList<String> daysInMonth )
    {
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, selectedDate, check, this,this );
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();//년도와 달

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMMM");
        return date.format(formatter);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate_m(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String SET_Date(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDay(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view) {//이전달 보기
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view) {//다음달 보기
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, String dayText) {//캘린더 클릭
        if (!dayText.equals("")) {

            oData = new ArrayList<workoutRecord_itemData>();//리스트 객체생성

            Set_bot(dayText);


        }
    }

    private void set_workoutRecord_grid() {


        rv = (RecyclerView) findViewById(R.id.rv_record);
        txt_add = (TextView) findViewById(R.id.txt_add);


        if (oData.size() < 1) {
            rv.setVisibility(View.GONE);
            txt_add.setVisibility(View.VISIBLE);

            txt_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //운동추가(세트추가화면)
                    Intent intent = new Intent(getApplicationContext(), addSetActivity.class);//명시적 인텐트
                    intent.putExtra("date", txt_date.getText());


                    startActivity(intent);
                }
            });


        } else {
            rv.setVisibility(View.VISIBLE);
            txt_add.setVisibility(View.GONE);

            WorkoutAdapter adapter = new WorkoutAdapter(oData);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


            adapter.notifyDataSetChanged();

            adapter.setOnItemClickListener(new WorkoutAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {


                    //운동추가(세트추가화면)
                    Intent intent = new Intent(getApplicationContext(), addSetActivity.class);//명시적 인텐트

                    intent.putExtra("date", txt_date.getText());
                    startActivity(intent);


                }
            });
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void Set_bot(String dayText) {

        String date = SET_Date(selectedDate) + "-" + dayText;
        txt_date.setText(date);

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("workout");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject item = jsonArray.getJSONObject(i);

                        String workout_Name = item.getString("workout_Name");//운동이름
                        String workout_Part = item.getString("workout_Part");//운동부위
                        String set = item.getString("Perform_set") + "세트";//세트
                        String volumn = item.getString("VOLUMN") + "kg";//볼륨

                        workoutRecord_itemData itemData = new workoutRecord_itemData();// 모델 객체 생성
                        itemData.setWorkout_name(workout_Name);
                        itemData.setWorkout_set(set);
                        itemData.setWorkout_part(workout_Part);
                        itemData.setWorkout_volumn(volumn);

                        oData.add(itemData);


                    }
                    //운동 수행 리스트 출력

                    set_workoutRecord_grid();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        CalendarRequest calendarRequest = new CalendarRequest(userEmail, date, responseListner);
        RequestQueue queue = Volley.newRequestQueue(CalendarActivity.this);
        queue.add(calendarRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void Set_Check() {

    }

}