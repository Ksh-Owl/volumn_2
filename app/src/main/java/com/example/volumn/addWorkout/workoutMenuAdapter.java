package com.example.volumn.addWorkout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volumn.R;

import java.util.ArrayList;

public class workoutMenuAdapter extends RecyclerView.Adapter<workoutMenuAdapter.ViewHolder> {


    private ArrayList mData = null;
    private OnItemClickListener mListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    public workoutMenuAdapter(ArrayList data) {
        mData = data;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.partmenu_cell, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);

        holder.workout_partMenu.setText(mData.get(position).toString());

    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView workout_partMenu;



        ViewHolder(View itemView) {
            super(itemView);

            workout_partMenu = itemView.findViewById(R.id.txt_partMenu);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {


                        //선택시 선택된 데이터 전달받고 메인 액티비티 실행

                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }

                    }
                }
            });



        }
    }
}
