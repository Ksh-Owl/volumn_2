package com.example.volumn.calendar;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volumn.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final ArrayList<String> check;


    private final OnItemListener onItemListener;
    private LocalDate selectedDate;
    private Context context;

    public CalendarAdapter(ArrayList<String> daysOfMonth, LocalDate selectedDate, ArrayList<String> Check, Context context, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.selectedDate = selectedDate;
        this.context = context;
        this.check = Check;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //context = parent.getContext();
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        //layoutParams.height = (int) (parent.getHeight() * 0.166666666); * 0.099999
        layoutParams.height = (int) (parent.getHeight() * 0.167);

        return new CalendarViewHolder(view, onItemListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        String day = daysOfMonth.get(position);

        if (!day.equals("")) {
            int day_int = Integer.parseInt(day);
            if (day_int < 10) {
                day = "0" + day;
            }
        }


        holder.dayOfMonth.setText(day);

        String monthYear = monthYearFromDate(selectedDate);
        String date = monthYear + "-" + day;
        //쉐어드 저장ID 와 monthYear(날짜) 로 검색

        if (!day.equals("")) {
            for (int i = 0; i < check.size(); i++) {
                if (check.get(i).equals(date)) { //검색된 데이터 있으면 보이게
                    holder.img_show.setVisibility(View.VISIBLE);

                }

            }
        }


    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return date.format(formatter);
    }
}
