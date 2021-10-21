package com.example.volumn.rank;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.example.volumn.addSet.addSetRequest;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volumn.R;

public class RankActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Toolbar toolbar;
    ViewPager2 pager2;
    FragmentAdapter adapter;

    TextView tab1, tab2, tab3, select;
    TextView txt_volumn_r;
    ImageView img_back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        this.context = this;

        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);
        select = findViewById(R.id.textSelected);
        img_back = findViewById(R.id.img_back);
        txt_volumn_r = findViewById(R.id.txt_volumn_r);

        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        img_back.setOnClickListener(this);


        pager2 = findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        pager2.setAdapter(adapter);


        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);


                switch (position) {
                    case 0:
                        onClick(tab1);
                        break;
                    case 1:
                        onClick(tab2);
                        break;
                    case 2:
                        onClick(tab3);
                        break;


                }
            }
        });


    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tab1) {
            tab1.setTextColor(getResources().getColor(R.color.black));
            tab2.setTextColor(getResources().getColor(R.color.gray));
            tab3.setTextColor(getResources().getColor(R.color.gray));
            select.animate().x(0).setDuration(60);
            select.setText("주별랭킹");

            pager2.setCurrentItem(0);

            // do anything here
            //  Toast.makeText(this, "tab 1 clicked", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.tab2) {
            tab1.setTextColor(getResources().getColor(R.color.gray));
            tab2.setTextColor(getResources().getColor(R.color.black));
            tab3.setTextColor(getResources().getColor(R.color.gray));
            int size = tab2.getWidth();
            select.animate().x(size).setDuration(60);
            select.setText("월별랭킹");
            pager2.setCurrentItem(1);

            //    Toast.makeText(this, "tab 2 clicked", Toast.LENGTH_SHORT).show();

        } else if (v.getId() == R.id.tab3) {
            tab1.setTextColor(getResources().getColor(R.color.gray));
            tab2.setTextColor(getResources().getColor(R.color.gray));
            tab3.setTextColor(getResources().getColor(R.color.black));
            int size = tab2.getWidth() * 2;
            select.animate().x(size).setDuration(60);
            select.setText("친구목록");
            pager2.setCurrentItem(2);

            //Toast.makeText(this, "tab 3 clicked", Toast.LENGTH_SHORT).show();

        }

        if(v.getId() == R.id.img_back)
        {
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Rank");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);

                                String userID = item.getString("userID");
                                String volumn = item.getString("volumn");
                                txt_volumn_r.setText(volumn);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                Rank_volumn_me_Request rank_volumn_me_request = new Rank_volumn_me_Request(userEmail,  responseListner);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(rank_volumn_me_request);
    }
}