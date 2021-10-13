package com.example.volumn.addWorkout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class addWorkoutActivity extends AppCompatActivity {


    RecyclerView rv_partMenu;
    RecyclerView rv_workout;
    String part;
    ArrayList<Model> workout_List;

    public ArrayList<Model> choice_List; //선택한 리스트 저장

    Button btn_addWorkout;
    Button btn_addWorkout_action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        set_workoutMenu_gird();

        //
       // choice_List = (ArrayList<Model>)getIntent().getSerializableExtra("choice_list");
       // if(choice_List == null)
      //  {

       // }

        choice_List = new ArrayList<>();

        Intent intent = getIntent();//인텐트 받아오기


        btn_addWorkout = (Button) findViewById(R.id.btn_addWorkout);

        btn_addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //운동추가화면
                Intent intent = new Intent(getApplicationContext(), action_addWorkoutActivity.class);

                startActivity(intent);

            }
        });

        btn_addWorkout_action = (Button) findViewById(R.id.btn_addWorkout_action);

        btn_addWorkout_action.setVisibility(View.GONE);

        btn_addWorkout_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //세트추가화면
                //Intent intent = new Intent(getApplicationContext(), addSetActivity.class);
               // intent.putExtra("choice_list",choice_List);


              //  startActivity(intent);

                //리스트로 넘기지 않고 db에 저장한다.

                //세트,카운트 ,무게 제외하고 ,날짜 포함 추가
              String date =  intent.getStringExtra("date");

                        for(int i =0; i<choice_List.size(); i++)
                        {

                            Response.Listener<String> responseListner = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);


                                        boolean success = jsonObject.getBoolean("success");
                                        if (success) { // 운동기록에 성공
                                            Toast.makeText(getApplicationContext(),"운동이 추가 되었습니다.",Toast.LENGTH_SHORT).show();

                                            finish();//액티비티 종료
                                        } else { // 운동삭제에 실패했습니다.
                                            Toast.makeText(getApplicationContext(),"중복된 운동 입니다.",Toast.LENGTH_SHORT).show();

                                            return;
                                        }




                                    }catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            };
                            String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기
                            String Workout_ID = choice_List.get(i).getWorkout_ID();


                            addWorkoutRequest addWorkoutRequest = new addWorkoutRequest(Workout_ID,date  ,  responseListner);
                            RequestQueue queue = Volley.newRequestQueue(addWorkoutActivity.this);
                            queue.add(addWorkoutRequest);

                        }





                finish();
            }
        });

    }


    private void set_workoutMenu_gird()//부위메뉴 출력
    {
        ArrayList workoutMenu_oData;

        workoutMenu_oData = new ArrayList();


        workoutMenu_oData.add("전체");
        workoutMenu_oData.add("가슴");
        workoutMenu_oData.add("어깨");
        workoutMenu_oData.add("등");

        workoutMenu_oData.add("하체");
        workoutMenu_oData.add("이두");
        workoutMenu_oData.add("삼두");
        workoutMenu_oData.add("엉덩이");

        workoutMenu_oData.add("승모근");
        workoutMenu_oData.add("복근");




        workoutMenuAdapter adapter = new workoutMenuAdapter(workoutMenu_oData);

        rv_partMenu = (RecyclerView) findViewById(R.id.rv_partMenu);
        rv_partMenu.setAdapter(adapter);
        rv_partMenu.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new workoutMenuAdapter.OnItemClickListener() {//부위(등,어깨...)를 선택했을때
            @Override
            public void onItemClick(View v, int position) {

                workout_List = new ArrayList(); //운동리스트 어레이리스트
                String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기
                String TAG_NAME = "workout_Name";

                part =workoutMenu_oData.get(position).toString();//선택한 부위


                //Toast.makeText(getApplicationContext(),part,Toast.LENGTH_SHORT).show();


               //리스트 요청

                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("workout");

                            for(int i= 0; i<jsonArray.length(); i++){
                                JSONObject item = jsonArray.getJSONObject(i);

                                String name = item.getString(TAG_NAME);
                                String workout_Part = item.getString("workout_Part");
                                String workout_ID = item.getString("workout_ID");

                                Model model = new Model(name,workout_Part,workout_ID,"");// 모델 객체 생성


                                if(choice_List.size() != 0)//선택 리스트있으면
                                {
                                    for (int j = 0; j<choice_List.size(); j++)//선택리스트 수 만큼 확인
                                    {

                                        if(choice_List.get(j).getWorkout_Name().toString().equals(name) )//선택리스트 이름과 운동이름(모델객체) 같으면
                                        {

                                         //선택된 리스트에 있는것과 출력되는 리스트가 같은 이름이면
                                         //그 리스트는 선택된것

                                            model.setSelected(!model.isSelected());//모델객체 선택된 상태로 변경

                                            workout_List.add(model);// 선택된 모델 운동목록리스트에 추가

                                            j = choice_List.size()+1;

                                        }
                                        if(j== choice_List.size()-1)
                                        {
                                            workout_List.add(model);//이름만 있는 모델넣기

                                        }

                                    }

                                }else
                                {

                                    workout_List.add(model);//이름만 있는 모델넣기

                                }

                            }

                            set_workout_grid();//운동목록 출력


                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                };
                WorkoutMenuRequest workoutMenuRequest = new WorkoutMenuRequest(userEmail, part, responseListner);
                RequestQueue queue = Volley.newRequestQueue(addWorkoutActivity.this);
                queue.add(workoutMenuRequest);

            }
        });
    }

    private void set_workout_grid()//운동목록 출력
    {
        workoutListAdapter adapterWorkout = new workoutListAdapter(workout_List);

        rv_workout = (RecyclerView) findViewById(R.id.rv_workout);
        rv_workout.setAdapter(adapterWorkout);
        rv_workout.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        adapterWorkout.notifyDataSetChanged();

        adapterWorkout.setOnItemClickListener(new workoutListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                final  Model model = workout_List.get(position);

                model.setSelected(!model.isSelected());


               //다중선택 클릭
                v.setBackgroundColor(model.isSelected()? Color.GRAY :Color.WHITE );

                if(model.isSelected())
                {
                    choice_List.add(model);

                    //리스트증가시 운동추가 버튼 증가 개수증가
                    btn_addWorkout_action.setVisibility(View.VISIBLE);
                    int count  = choice_List.size();
                    btn_addWorkout_action.setText(count+"개의 운동을 추가합니다.");
                }else
                {
                    String name = model.getWorkout_Name();//삭제해야 하는 운동 이름



                    for (int i=0; i<choice_List.size(); i++)
                    {  //선택해제한 대상을 선택 리스트에서 빼기

                        //choice_List.get(i).getWorkout_Name();
                        if(choice_List.get(i).getWorkout_Name().equals(name) )
                        {
                            choice_List.remove(i);
                        }

                    }

                    int count  = choice_List.size();
                    btn_addWorkout_action.setText(count+"개의 운동을 추가합니다.");

                    if(count ==0)
                    {
                        btn_addWorkout_action.setVisibility(View.GONE);

                    }
                }


            }
        });

        adapterWorkout.setOnItemLongClickListener(new workoutListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                String workout_Name = workout_List.get(position).getWorkout_Name().toString();
                String workout_Part = workout_List.get(position).getWorkout_Part().toString();
                String workout_ID = workout_List.get(position).getWorkout_ID().toString();
                //운동추가화면
                Intent intent = new Intent(getApplicationContext(), action_addWorkoutActivity.class);
                intent.putExtra("workout_Name",workout_Name );
                intent.putExtra("workout_Part",workout_Part);
                intent.putExtra("workout_ID",workout_ID);

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //
        workout_List = new ArrayList(); //운동리스트 어레이리스트
        String userEmail = PreferenceManager.getString(getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기
        String TAG_NAME = "workout_Name";




       // Toast.makeText(getApplicationContext(),part,Toast.LENGTH_SHORT).show();


        //리스트 요청

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("workout");

                    for(int i= 0; i<jsonArray.length(); i++){
                        JSONObject item = jsonArray.getJSONObject(i);

                        String name = item.getString(TAG_NAME);
                        String workout_Part = item.getString("workout_Part");
                        String workout_ID = item.getString("workout_ID");

                        Model model = new Model(name,workout_Part,workout_ID,"");// 모델 객체 생성


                        if(choice_List.size() != 0)//선택 리스트있으면
                        {
                            for (int j = 0; j<choice_List.size(); j++)//선택리스트 수 만큼 확인
                            {

                                if(choice_List.get(j).getWorkout_Name().toString().equals(name) )//선택리스트 이름과 운동이름(모델객체) 같으면
                                {

                                    //선택된 리스트에 있는것과 출력되는 리스트가 같은 이름이면
                                    //그 리스트는 선택된것

                                    model.setSelected(!model.isSelected());//모델객체 선택된 상태로 변경

                                    workout_List.add(model);// 선택된 모델 운동목록리스트에 추가

                                    j = choice_List.size()+1;

                                }
                                if(j== choice_List.size()-1)
                                {
                                    workout_List.add(model);//이름만 있는 모델넣기

                                }

                            }

                        }else
                        {

                            workout_List.add(model);//이름만 있는 모델넣기

                        }

                    }

                    set_workout_grid();//운동목록 출력


                }catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        };
        WorkoutMenuRequest workoutMenuRequest = new WorkoutMenuRequest(userEmail, part, responseListner);
        RequestQueue queue = Volley.newRequestQueue(addWorkoutActivity.this);
        queue.add(workoutMenuRequest);




    }
}