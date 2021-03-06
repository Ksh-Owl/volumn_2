package com.example.volumn.chat;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.example.volumn.include.myRoom_PreferenceManager;
import com.example.volumn.include.profileIMG_PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    String left_read;
    private ArrayList<msg_Model> mData = null;
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

    public MsgAdapter(ArrayList<msg_Model> data) {
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
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);
        String msg = mData.get(position).getMsg();

        if (msg == null) {
            return;
        }
        String time = mData.get(position).getTime();
        String img_id = mData.get(position).getImg_id();
        String msgs[] = msg.split("???");
        msgs[0] = msgs[0].replace("[", "").replace("]", "");
        String userEmail = PreferenceManager.getString(context.getApplicationContext(), "userEmail");//??????????????? ???????????? ????????? ????????????


        //?????? ??????
        // int  read_Count = Integer.parseInt(mData.get(position).getRead_count()) ;
        // JSONArray J_read_mem = mData.get(position).getRead_mem();

//         left_read = Integer.toString(read_Count -(J_read_mem.length())) ;
//         if(Integer.parseInt(left_read) <=0)
//         {
//             //left_read = "";
//         }


        if (!img_id.equals("")) {//????????? ?????????
            // holder.img_img2.setVisibility(View.VISIBLE);


            holder.receivedMessage2.setVisibility(View.GONE);
            holder.sentMessage2.setVisibility(View.GONE);
            holder.txt_notice.setVisibility(View.GONE);

            Get_img(holder, img_id, userEmail, msgs[0]);

           // holder.sentMessage2.setVisibility(View.GONE);

            // byte[] bytePlainOrg = Base64.decode();


            //    holder.img_img2.setImageURI(Uri.parse());


            return;
        }


        if (msgs[0].equals("????????????")) {
            holder.txt_notice.setVisibility(View.VISIBLE);
            holder.txt_notice.setText(msgs[1]);


            holder.receivedMessage2.setVisibility(View.GONE);
            holder.txt_chatName.setVisibility(View.GONE);
            holder.sentMessage2.setVisibility(View.GONE);
            holder.circleImageView.setVisibility(View.GONE);


            holder.txt_time.setVisibility(View.GONE);

            holder.txt_time2.setVisibility(View.GONE);
            holder.txt_time3.setVisibility(View.GONE);
            holder.txt_time4.setVisibility(View.GONE);
        } else if (msgs[0].equals("??????")) {

            holder.txt_notice.setVisibility(View.VISIBLE);
            holder.txt_notice.setText(msgs[1]);

            if (mData.size()-1 <= position){
                //?????????
                holder.txt_notice.setVisibility(View.GONE);

            }




            holder.receivedMessage2.setVisibility(View.GONE);
            holder.txt_chatName.setVisibility(View.GONE);
            holder.sentMessage2.setVisibility(View.GONE);
            holder.circleImageView.setVisibility(View.GONE);


            holder.txt_time.setVisibility(View.GONE);

            holder.txt_time2.setVisibility(View.GONE);
            holder.txt_time3.setVisibility(View.GONE);
            holder.txt_time4.setVisibility(View.GONE);

        } else {

            try{
                if (userEmail.equals(msgs[0])) {
                    //??? ????????? ??????

                    holder.sentMessage2.setText(msgs[1]);
                    // holder.txt_time2.setText(time);

                    holder.receivedMessage2.setVisibility(View.GONE);
                    holder.txt_chatName.setVisibility(View.GONE);

                    holder.txt_notice.setVisibility(View.GONE);
                    holder.circleImageView.setVisibility(View.GONE);
                    // holder.txt_readcount2.setText(left_read);

                    holder.txt_time.setVisibility(View.GONE);

                    holder.txt_time2.setVisibility(View.GONE);
                    holder.txt_time3.setVisibility(View.GONE);
                    holder.txt_time4.setVisibility(View.GONE);

                    holder.sentMessage2.setVisibility(View.VISIBLE);

                } else {  //???????????? ????????? ??????



                    try{
                        Map<String,String> user_IMG =  ((MainChatActivity)MainChatActivity.context).userIMG;

                        String img = user_IMG.get(msgs[0]);
                        if(img == null)
                        {
                            //???????????? ????????? ????????? ????????? ????????? ?????????
                            //userEmail ????????? ?????????
                            img = profileIMG_PreferenceManager.getProfileIMG(context, msgs[0]);


                        }else
                        {
                            //???????????? ????????? ??????
                            profileIMG_PreferenceManager.setProfileIMG(context,msgs[0],img);

                        }
                        Bitmap bitmap = ImageUtil.convert(img);
                        holder.circleImageView.setImageBitmap(bitmap);
                        holder.circleImageView.setVisibility(View.VISIBLE);



                        holder.txt_chatName.setText(msgs[0]);//????????? ?????????
                        holder.txt_chatName.setVisibility(View.VISIBLE);
                        holder.receivedMessage2.setVisibility(View.VISIBLE);

                        holder.receivedMessage2.setText(msgs[1]);
                    }catch (Exception e){

                    }
                    holder.sentMessage2.setVisibility(View.GONE);
                    holder.txt_notice.setVisibility(View.GONE);


                    holder.txt_time.setVisibility(View.GONE);
                    holder.txt_time2.setVisibility(View.GONE);
                    holder.txt_time3.setVisibility(View.GONE);
                    holder.txt_time4.setVisibility(View.GONE);


                }
            }catch (Exception e){
              e.printStackTrace();
            }


        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView receivedMessage2;
        TextView sentMessage2;
        TextView txt_chatName;
        TextView txt_notice;
        ImageView img_img;
        ImageView img_img2;

        CircleImageView circleImageView;

        TextView txt_time;
        TextView txt_time2;
        TextView txt_time3;
        TextView txt_time4;


        ViewHolder(View itemView) {
            super(itemView);

            img_img = (ImageView) itemView.findViewById(R.id.img_img);
            img_img2 = (ImageView) itemView.findViewById(R.id.img_img2);

            receivedMessage2 = (TextView) itemView.findViewById(R.id.receivedMessage2);
            sentMessage2 = (TextView) itemView.findViewById(R.id.sentMessage2);
            txt_chatName = (TextView) itemView.findViewById(R.id.txt_chatName);
            txt_notice = (TextView) itemView.findViewById(R.id.txt_notice);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.circleImageView);

            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_time2 = (TextView) itemView.findViewById(R.id.txt_time2);
            txt_time3 = (TextView) itemView.findViewById(R.id.txt_time3);
            txt_time4 = (TextView) itemView.findViewById(R.id.txt_time4);

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

    private void Get_img(ViewHolder holder, String img_id, String userEmail, String msg_id) {


        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    boolean success = jsonObject.getBoolean("success");

                    if (success) { // ????????? ???????????? ??????

                        //????????? ????????? ???????????? ??????
                        try {
                            String img = jsonObject.getString("img");


                            String data = img; //bitmap ??????

                            Bitmap bitmap = ImageUtil.convert(data);

                            if (userEmail.equals(msg_id)) {
                                //?????? ?????? ??????
                                holder.img_img2.setVisibility(View.VISIBLE);
                                holder.img_img2.setImageBitmap(bitmap);
                                holder.txt_chatName.setVisibility(View.GONE);

                                holder.circleImageView.setVisibility(View.GONE);

                                holder.txt_time.setVisibility(View.GONE);
                                holder.txt_time2.setVisibility(View.GONE);
                                holder.txt_time3.setVisibility(View.GONE);
                                holder.txt_time4.setVisibility(View.GONE);
                                //holder.txt_readcount4.setText(left_read);


                            } else {
                                //??????????????? ?????? ??????

                                try{
                                    holder.img_img.setImageBitmap(bitmap);
                                    holder.img_img.setVisibility(View.VISIBLE);

                                    Map<String,String> user_IMG =  ((MainChatActivity)MainChatActivity.context).userIMG;

                                    String imgA = user_IMG.get(msg_id);
                                    Bitmap bitmapA = ImageUtil.convert(imgA);
                                    holder.circleImageView.setImageBitmap(bitmapA);
                                    holder.circleImageView.setVisibility(View.VISIBLE);


                                    holder.txt_chatName.setText(msg_id);
                                }catch (Exception e){

                                }
                                holder.txt_chatName.setVisibility(View.VISIBLE);


                                holder.txt_time.setVisibility(View.GONE);
                                holder.txt_time2.setVisibility(View.GONE);
                                holder.txt_time3.setVisibility(View.GONE);
                                holder.txt_time4.setVisibility(View.GONE);
                            }
                            //????????? ??????
                            try{

                                    ((MainChatActivity)MainChatActivity.context).rvPosition();



                            }catch (Exception e){

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // ????????? ????????? ??????
                        //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                        return;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        //String userEmail = PreferenceManager.getString(context, "userEmail");//??????????????? ???????????? ????????? ????????????

        Get_img_Request get_img_request = new Get_img_Request(img_id, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(get_img_request);

    }
}
