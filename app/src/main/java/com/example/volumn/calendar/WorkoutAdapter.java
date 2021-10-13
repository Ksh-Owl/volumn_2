package com.example.volumn.calendar;

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

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder>
{
    private ArrayList<workoutRecord_itemData> mData = null;
    private OnItemClickListener mListener = null;
    private OnItemClickListener mLongListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemClickListener listener) {
        this.mLongListener = listener;
    }

    public WorkoutAdapter(ArrayList<workoutRecord_itemData> data) {
        mData = data;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.workoutrecord_cell, parent, false);
        ViewHolder vh = new ViewHolder(view);



        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }
        workoutRecord_itemData itemData = mData.get(position);
        Log.v("item", " item ::  " + position);

        holder.workout_Name.setText(itemData.getWorkout_name());
        holder.workout_Part.setText(itemData.getWorkout_part());
        holder.workout_Sets.setText(itemData.getWorkout_set());
        holder.workout_Volumn.setText(itemData.getWorkout_volumn());

    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView workout_Name;
        TextView workout_Part;
        TextView workout_Sets;
        TextView workout_Volumn;


        ViewHolder(View itemView) {
            super(itemView);

            workout_Name = itemView.findViewById(R.id.txt_workout);
            workout_Part = itemView.findViewById(R.id.txt_part);
            workout_Sets = itemView.findViewById(R.id.txt_set);
            workout_Volumn = itemView.findViewById(R.id.txt_volumn);


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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {


                        //선택시 선택된 데이터 전달받고 메인 액티비티 실행

                        if (mLongListener != null) {
                            mLongListener.onItemClick(v, pos);
                        }

                    }


                    return false;
                }
            });

        }
    }
}
