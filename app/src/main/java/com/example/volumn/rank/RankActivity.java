package com.example.volumn.rank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volumn.R;

public class RankActivity extends AppCompatActivity implements View.OnClickListener {


    Toolbar toolbar;
    ViewPager2 pager2;
    FragmentAdapter adapter;

    TextView tab1, tab2, tab3, select;
    ImageView img_back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);


        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);
        select = findViewById(R.id.textSelected);
        img_back = findViewById(R.id.img_back);

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
            select.setText("일별랭킹");

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

}