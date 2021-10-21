package com.example.volumn.rank;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.example.volumn.addSet.addSetActivity;
import com.example.volumn.addSet.addSetRequest;
import com.example.volumn.include.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volumn.R;
import com.example.volumn.addWorkout.Model;

import java.util.ArrayList;

public class addFriendAdapter extends RecyclerView.Adapter<addFriendAdapter.ViewHolder> {

    private ArrayList<dModel_Friend> mData = null;
    private addFriendAdapter.OnItemClickListener mListener = null;
    private addFriendAdapter.OnItemClickListener mLongListener = null;

    private ArrayList<dModel_Friend> mChoicelList;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(addFriendAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(addFriendAdapter.OnItemClickListener listener) {
        this.mLongListener = listener;
    }

    public addFriendAdapter(Context context, ArrayList<dModel_Friend> data) {
        mData = data;
        this.context = context;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.addfriend_cell, parent, false);//출력되는 화면
        addFriendAdapter.ViewHolder vh = new addFriendAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull addFriendAdapter.ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);

        //holder.workout_Name.setText(mData.get(position).getWorkout_Name().toString());
        String name = mData.get(position).getFriend_Name().trim();
        String state = mData.get(position).getFriend_state().trim();
        holder.txt_name_aF.setText(name);
        holder.txt_state_afcell.setText(state);


        if (state.equals("친구")) {
            holder.img_accept.setVisibility(View.GONE);
        }
        switch (state) {
            case "친구":
                holder.img_accept.setVisibility(View.GONE);

                break;
            case "수락대기":
                holder.img_accept.setVisibility(View.GONE);

                break;
            default:

                break;
        }


        holder.img_accept.setOnClickListener(new View.OnClickListener() {
            //친구 용청 승낙
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListner = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);


                                    boolean success = jsonObject.getBoolean("success");
                                                if (success) { // 친구승낙에 성공
                                                    //Toast.makeText(getApplicationContext(),"운동저장이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                                    ((addFriendActivity)addFriendActivity.context).onResume();

                                                } else { // 친구승낙에 실패했습니다.
                                                    //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                                                    return;
                                                }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
                        String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                        addFriend_accpetRequest addFriend_accpetRequest = new addFriend_accpetRequest(userEmail,mData.get(position).getFriend_ID().trim()  ,  responseListner);
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(addFriend_accpetRequest);
            }
        });

        holder.img_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 친구삭제에 성공
                                //Toast.makeText(getApplicationContext(),"운동저장이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                ((addFriendActivity)addFriendActivity.context).onResume();

                            } else { // 친구승낙에 실패했습니다.
                                //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                                return;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                addFriend_rejectRequest addFriend_rejectRequest = new addFriend_rejectRequest(mData.get(position).getID().trim()  ,  responseListner);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(addFriend_rejectRequest);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name_aF;
        TextView txt_state_afcell;
        ImageView img_reject;
        ImageView img_accept;


        ViewHolder(View itemView) {
            super(itemView);

            txt_name_aF = itemView.findViewById(R.id.txt_name_aF);
            txt_state_afcell = itemView.findViewById(R.id.txt_state_afcell);
            img_reject = itemView.findViewById(R.id.img_reject);
            img_accept = itemView.findViewById(R.id.img_accept);

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
