package com.example.volumn.chat;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.volumn.R;
import com.example.volumn.include.myRoom_PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private ArrayList<chatRoom_Model> mData = null;
    private RoomAdapter.OnItemClickListener mListener = null;
    private RoomAdapter.OnItemClickListener mLongListener = null;
    public static Context context;


    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(RoomAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }
    public void setOnItemLongClickListener(RoomAdapter.OnItemClickListener listener) {
        this.mLongListener = listener;
    }

    public RoomAdapter(ArrayList<chatRoom_Model> data) {
        mData = data;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.roomcell, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);
        //holder.btn_joinRoom.setVisibility(View.GONE);
//        holder.btn_joinRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String roomName = mData.get(position).getRoom_nm();
//                String room_ID = mData.get(position).getRoom_ID();
//
//
//                Intent intent = new Intent((ChatRoomActivity)context, MainChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("roomName", roomName);
//                intent.putExtra("room_ID", room_ID);
//
//                ((ChatRoomActivity)context).startActivity(intent);
//            }
//        });
        //기존에 입장되어있는 방에 입장
//        String json = myRoom_PreferenceManager.getString(context, "ROOM");
//        Log.v("TAG", "json : " + json);
//        if (json != null) {
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = new JSONArray(json);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject item = jsonArray.getJSONObject(i);
//                    String room_name = item.getString("value");
//                    if(room_name.equals(mData.get(position).getRoom_nm()) ){
//                    }else
//                    {
//                    }
//
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//
//        }
        if(!mData.get(position).getLast_msg().equals(""))
        {
            holder.txt_Check.setVisibility(View.VISIBLE);

        }else {
            holder.txt_Check.setVisibility(View.INVISIBLE);

        }

        holder.txt_roomName.setText(mData.get(position).getRoom_nm());
        holder.txt_roomCount.setText(mData.get(position).getMem_count());
        holder.txt_roomInwon.setText(mData.get(position).getMem_count());

        holder.txt_lastTime.setText(mData.get(position).getLastTime());


        String msg = mData.get(position).getLast_msg();
        if(!msg.equals("")){
            String msgs[] = msg.split("▶");
            msgs[0] = msgs[0].replace("[","").replace("]","");

            holder.txt_lastMsg.setText(""+msgs[1]);
        }

        if(mData.get(position).getMsg_count()> 0){
            String  msgCount = Integer.toString(mData.get(position).getMsg_count());
            holder.txt_msgCount.setText(msgCount);
            //테스트
            //holder.txt_msgCount.setVisibility(View.GONE);
//            if( mData.get(position).getRoom_nm().equals(((ChatRoomActivity)ChatRoomActivity.context).inRoom_name))//들어간방과 같으면
//            {
//                holder.txt_msgCount.setVisibility(View.GONE);
//
//            }


        }else
        {
            holder.txt_msgCount.setVisibility(View.GONE);
        }

    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_roomName;
        TextView txt_roomCount;
        TextView txt_msgCount;
        TextView txt_lastMsg;
        TextView txt_Check;

        TextView txt_roomInwon;
        TextView txt_lastTime;
        ViewHolder(View itemView) {
            super(itemView);



            txt_msgCount = (TextView) itemView.findViewById(R.id.txt_msgCount);
            txt_roomName = (TextView) itemView.findViewById(R.id.txt_roomName);
            txt_roomCount = (TextView) itemView.findViewById(R.id.txt_roomInwon);
            txt_lastMsg = (TextView) itemView.findViewById(R.id.txt_lastMsg);
            txt_Check = (TextView) itemView.findViewById(R.id.txt_Check);
            txt_roomInwon = (TextView) itemView.findViewById(R.id.txt_roomInwon);
            txt_lastTime = (TextView) itemView.findViewById(R.id.txt_lastTime);

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
