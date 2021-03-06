package com.example.volumn.addWorkout;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volumn.R;

import java.util.ArrayList;

public class workoutListAdapter extends RecyclerView.Adapter<workoutListAdapter.ViewHolder>  {

    private ArrayList<Model> mData = null;
    private OnItemClickListener mListener = null;
    private OnItemClickListener mLongListener = null;

    private ArrayList<Model> mChoicelList ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    public void setOnItemLongClickListener(OnItemClickListener listener) {
        this.mLongListener = listener;
    }

    public workoutListAdapter(ArrayList<Model> data) {
        mData = data;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.workoutlist_cell, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);

        holder.workout_Name.setText(mData.get(position).getWorkout_Name().toString());

        if(mData.get(position).isSelected()){
            holder.itemView.setBackgroundColor(Color.GRAY  );

        }else
        {
            holder.itemView.setBackgroundColor(Color.WHITE  );

        }


        //final  Model model = mData.get(position);

//        holder.workout_Name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                model.setSelected(!model.isSelected());
//
//
//                //???????????? ??????
//                holder.itemView.setBackgroundColor(model.isSelected()?Color.GRAY :Color.WHITE );
//
//            }
//        });



    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView workout_Name;



        ViewHolder(View itemView) {
            super(itemView);

            workout_Name = itemView.findViewById(R.id.txt_info);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {


                        //????????? ????????? ????????? ???????????? ?????? ???????????? ??????

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


                        //????????? ????????? ????????? ???????????? ?????? ???????????? ??????

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
