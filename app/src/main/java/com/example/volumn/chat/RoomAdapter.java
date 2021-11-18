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

        holder.txt_roomName.setText(mData.get(position).getRoom_nm());
        holder.txt_roomCount.setText(mData.get(position).getMem_count());
        if(mData.get(position).getMsg_count()> 0){
            String  msgCount = Integer.toString(mData.get(position).getMsg_count());
            holder.txt_msgCount.setText(msgCount);

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


        ViewHolder(View itemView) {
            super(itemView);



            txt_msgCount = (TextView) itemView.findViewById(R.id.txt_msgCount);
            txt_roomName = (TextView) itemView.findViewById(R.id.txt_roomName);
            txt_roomCount = (TextView) itemView.findViewById(R.id.txt_roomCount);

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
