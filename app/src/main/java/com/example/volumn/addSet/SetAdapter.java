package com.example.volumn.addSet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.addWorkout.workoutListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {

    private ArrayList<setDataModel> mData = null;
    private workoutListAdapter.OnItemClickListener mListener = null;
    private workoutListAdapter.OnItemClickListener mLongListener = null;
    public static Context context;
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(workoutListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }
    public void setOnItemLongClickListener(workoutListAdapter.OnItemClickListener listener) {
        this.mLongListener = listener;
    }

    public SetAdapter(ArrayList<setDataModel> data) {
        mData = data;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.set_cell, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);

        holder.set.setText(mData.get(position).getSet().trim());
        holder.set_kg.setText(mData.get(position).getSet_kg().trim());
        holder.set_count.setText(mData.get(position).getSet_count().trim());

        String perform_ID = mData.get(position).getPerform_ID().trim();



       holder.img_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //perform_ID 로 perform_Record 삭제


                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 운동기록에 성공
                                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                               // holder.constraintLayout.setVisibility(View.GONE);

                                ((addSetActivity)addSetActivity.CONTEXT).onResume();

//                                holder.set.setVisibility(View.GONE);
//                                holder.set_count.setVisibility(View.GONE);
//                                holder.set_kg.setVisibility(View.GONE);
//                                holder.img_clear.setVisibility(View.GONE);
//
//                                holder.textView1.setVisibility(View.GONE);
//                                holder.textView2.setVisibility(View.GONE);
//                                holder.textView3.setVisibility(View.GONE);



                            } else { // 운동삭제에 실패했습니다.
                                Toast.makeText(context, "삭제중에 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();

                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                };
                //String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

                SetRequest_del setRequest_del = new SetRequest_del(perform_ID, responseListner);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(setRequest_del);


            }
        });





    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView set;
        TextView set_kg;
        TextView set_count;
        TextView textView1;
        TextView textView2;
        TextView textView3;
        ImageView img_clear;
        ConstraintLayout constraintLayout;


        ViewHolder(View itemView) {
            super(itemView);

            set = itemView.findViewById(R.id.txt_name_aF);
            set_kg = itemView.findViewById(R.id.txt_kg);
            set_count = itemView.findViewById(R.id.txt_count);
            img_clear = (ImageView) itemView.findViewById(R.id.img_reject);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.Con_1);

            textView1 = (TextView) itemView.findViewById(R.id.txt_email_aF);
            textView2 = (TextView) itemView.findViewById(R.id.textView2);
            textView3 = (TextView) itemView.findViewById(R.id.textView3);

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
