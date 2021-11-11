package com.example.volumn.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volumn.R;
import com.example.volumn.include.PreferenceManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private ArrayList<String> mData = null;
    private MsgAdapter.OnItemClickListener mListener = null;
    private MsgAdapter.OnItemClickListener mLongListener = null;
    public static Context context;
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(MsgAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }
    public void setOnItemLongClickListener(MsgAdapter.OnItemClickListener listener) {
        this.mLongListener = listener;
    }

    public MsgAdapter(ArrayList<String> data) {
        mData = data;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.msg_cell, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);
       String msg = mData.get(position);
       String msgs[] = msg.split("▶");


       msgs[0] = msgs[0].replace("[","").replace("]","");
       String userEmail = PreferenceManager.getString(context.getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        if(userEmail.equals(msgs[0])){
            //내 메시지 이면

            holder.sentMessage2.setText(msgs[1]);

            holder.receivedMessage2.setVisibility(View.GONE);
            holder.txt_chatName.setVisibility(View.GONE);
        }else
        {  //다른사람 메시지 이면

            holder.txt_chatName.setText(msgs[0]);
            holder.receivedMessage2.setText(msgs[1]);

            holder.sentMessage2.setVisibility(View.GONE);

        }




    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView receivedMessage2;
        TextView sentMessage2;
        TextView txt_chatName;


        ViewHolder(View itemView) {
            super(itemView);




            receivedMessage2 = (TextView) itemView.findViewById(R.id.receivedMessage2);
            sentMessage2 = (TextView) itemView.findViewById(R.id.sentMessage2);
            txt_chatName= (TextView) itemView.findViewById(R.id.txt_chatName);

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
