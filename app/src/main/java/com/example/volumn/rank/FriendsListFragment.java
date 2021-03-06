package com.example.volumn.rank;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.volumn.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsListFragment extends Fragment {

    Context context;
    RecyclerView rv_list_f ;

    TextView txt_addfriend;
    public FriendsListFragment() {
        // Required empty public constructor
    }


    public static FriendsListFragment newInstance(String param1, String param2) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_friends_list, container, false);


    }

    @Override
    public void onResume() {
        super.onResume();

        txt_addfriend = (TextView) getView().findViewById(R.id.txt_addfriend);
        rv_list_f = (RecyclerView) getView().findViewById(R.id.rv_list_f);

        txt_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().startActivity(new Intent(getActivity(),addFriendActivity.class));
            }
        });
        //??????????????? ??????
        ArrayList<dModel_Rank> Rank_list = new ArrayList<>();

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("Rank");

                    int Rank = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String userName = item.getString("userName");
                        String friendName = item.getString("friendName");


                        String volumn = item.getString("volumn");


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
                                state = "??????";

                            }

                        }
                        if(state.equals("??????") && user_ID.equals(meID))
                        {
                            Rank +=1 ;

                            //??????,??????,??????
                            dModel_Rank model = new dModel_Rank(Rank, friendName,volumn);// ?????? ?????? ??????

                            Rank_list.add(model);
                        }

                    }



                    Rank_FriendAdapter rank_friendAdapter = new Rank_FriendAdapter(context, Rank_list);

                    rv_list_f.setAdapter(rank_friendAdapter);
                    rv_list_f.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        String userEmail = PreferenceManager.getString(context, "userEmail");//??????????????? ???????????? ????????? ????????????

        Rank_List_Request rank_list_request = new Rank_List_Request(userEmail,  responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(rank_list_request);

    }

}