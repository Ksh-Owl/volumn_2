package com.example.volumn.rank;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Rank_FriendAdapter extends RecyclerView.Adapter<Rank_FriendAdapter.ViewHolder> {

    private ArrayList<dModel_Rank> mData = null;
    private Rank_FriendAdapter.OnItemClickListener mListener = null;
    private Rank_FriendAdapter.OnItemClickListener mLongListener = null;

    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(Rank_FriendAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(Rank_FriendAdapter.OnItemClickListener listener) {
        this.mLongListener = listener;
    }

    public Rank_FriendAdapter(Context context, ArrayList<dModel_Rank> data) {
        mData = data;
        this.context = context;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.friend_cell, parent, false);//출력되는 화면
        Rank_FriendAdapter.ViewHolder vh = new Rank_FriendAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull Rank_FriendAdapter.ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);

        //holder.workout_Name.setText(mData.get(position).getWorkout_Name().toString());
        String name = mData.get(position).getName().trim();
        String rank = Integer.toString(mData.get(position).getRank());
        String volumn = mData.get(position).getVolumn().trim();

        holder.txt_name_f.setText(name); ;
        holder.txt_rank.setText(rank);
        holder.txt_volumn_f.setText(volumn);



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name_f;
        TextView txt_volumn_f;
        TextView txt_rank;


        ViewHolder(View itemView) {
            super(itemView);

            txt_name_f = itemView.findViewById(R.id.txt_name_f);
            txt_volumn_f = itemView.findViewById(R.id.txt_volumn_f);
            txt_rank = itemView.findViewById(R.id.txt_rank);

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
