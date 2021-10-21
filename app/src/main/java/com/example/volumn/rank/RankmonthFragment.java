package com.example.volumn.rank;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.volumn.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankmonthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankmonthFragment extends Fragment {

    Context context;
    RecyclerView rv_list_month;




    public RankmonthFragment() {
        // Required empty public constructor
    }


    public static RankmonthFragment newInstance(String param1, String param2) {
        RankmonthFragment fragment = new RankmonthFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rankmonth, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();

        rv_list_month = (RecyclerView) getView().findViewById(R.id.rv_list_month);

        ArrayList<dModel_Rank> Rank_list = new ArrayList<>();

        Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Rank");

                            JSONArray jsonArray_me = jsonObject.getJSONArray("me");//내 점수 받아오기


                            JSONObject item_me = jsonArray_me.getJSONObject(0);
                            boolean check_me = false;//점수 추가 체크
                            int Rank = 0;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);

                                String userName = item.getString("userName");
                                String friendName = item.getString("friendName");


                                String volumn = item.getString("volumn");

                                //나보다 볼륨이 작으면 내가먼저 들어감
                                if(!check_me){

                                    try{
                                        int vol = Integer.parseInt(volumn);
                                        int vol_me = Integer.parseInt(item_me.getString("volumn"));

                                        if(vol_me >=vol){

                                            Rank +=1 ;//랭크 추가되었으니 랭크 추가

                                            //랭크,이름,볼륨
                                            dModel_Rank model = new dModel_Rank(Rank, "나",Integer.toString(vol_me));// 모델 객체 생성

                                            Rank_list.add(model);
                                            check_me = true;//추가끝
                                        }
                                    }catch (Exception e){

                                    }


                                }


                                String ID = item.getString("ID");
                                String user_ID = item.getString("user_ID");

                                String friend_ID = item.getString("friend_ID");
                                String meID = item.getString("meID");

                                //   String friend_name = item.getString("friend_name");

                                String state = "";
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject item_2 = jsonArray.getJSONObject(j);
                                    String user_ID_2 = item_2.getString("user_ID");
                                    String friend_ID_2 = item_2.getString("friend_ID");

                                    if (user_ID.equals(friend_ID_2) && friend_ID.equals(user_ID_2)) {
                                        state = "친구";

                                    }

                                }
                                if(state.equals("친구") && user_ID.equals(meID))
                                {
                                    Rank +=1 ;

                                    //랭크,이름,볼륨
                                    dModel_Rank model = new dModel_Rank(Rank, friendName,volumn);// 모델 객체 생성

                                    Rank_list.add(model);
                                }



                            }
                            Rank_FriendAdapter rank_friendAdapter = new Rank_FriendAdapter(context, Rank_list);

                            rv_list_month.setAdapter(rank_friendAdapter);
                            rv_list_month.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                //이번달 시작과 끝구하기

                String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                Rank_month_Request request = new Rank_month_Request(userEmail, responseListner);//DATE1,DATE2
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
    }
}