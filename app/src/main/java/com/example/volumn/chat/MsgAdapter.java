package com.example.volumn.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Base64;

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

       if(msg == null)
       {
           return;
       }
        String time = mData.get(position).getTime();
        String img_id = mData.get(position).getImg_id();
       String msgs[] = msg.split("▶");
        msgs[0] = msgs[0].replace("[","").replace("]","");
        String userEmail = PreferenceManager.getString(context.getApplicationContext(), "userEmail");//쉐어드에서 로그인된 아이디 받아오기


        //읽음 처리
        int  read_Count = Integer.parseInt(mData.get(position).getRead_count()) ;
        JSONArray J_read_mem = mData.get(position).getRead_mem();

         left_read = Integer.toString(read_Count -(J_read_mem.length())) ;
         if(Integer.parseInt(left_read) <=0)
         {
             //left_read = "";
         }


        if(!img_id.equals("")  ){
           // holder.img_img2.setVisibility(View.VISIBLE);


            holder.receivedMessage2.setVisibility(View.GONE);
            holder.sentMessage2.setVisibility(View.GONE);
            holder.txt_notice.setVisibility(View.GONE);

            Get_img(holder,img_id,userEmail,msgs[0]);



           // byte[] bytePlainOrg = Base64.decode();


        //    holder.img_img2.setImageURI(Uri.parse());


            return;
        }


       if(msgs[0].equals("공지사항"))
       {
           holder.txt_notice.setVisibility(View.VISIBLE);
           holder.txt_notice.setText(msgs[1]);


           holder.receivedMessage2.setVisibility(View.GONE);
           holder.txt_chatName.setVisibility(View.GONE);
           holder.sentMessage2.setVisibility(View.GONE);
           holder.circleImageView.setVisibility(View.GONE);


           holder.txt_readcount.setVisibility(View.GONE);

           holder.txt_readcount2.setVisibility(View.GONE);
           holder.txt_readcount3.setVisibility(View.GONE);
           holder.txt_readcount4.setVisibility(View.GONE);
       }else
       {


           if(userEmail.equals(msgs[0])){
               //내 메시지 이면

               holder.sentMessage2.setText(msgs[1]);

               holder.receivedMessage2.setVisibility(View.GONE);
               holder.txt_chatName.setVisibility(View.GONE);

               holder.txt_notice.setVisibility(View.GONE);
               holder.circleImageView.setVisibility(View.GONE);
               holder.txt_readcount2.setVisibility(View.VISIBLE);
               holder.txt_readcount2.setText(left_read);

               holder.txt_readcount.setVisibility(View.GONE);
               holder.txt_readcount3.setVisibility(View.GONE);
               holder.txt_readcount4.setVisibility(View.GONE);


           }else
           {  //다른사람 메시지 이면

               holder.txt_chatName.setText(msgs[0]);
               holder.receivedMessage2.setText(msgs[1]);

               holder.circleImageView.setVisibility(View.VISIBLE);

               holder.sentMessage2.setVisibility(View.GONE);
               holder.txt_notice.setVisibility(View.GONE);



               holder.txt_readcount.setVisibility(View.VISIBLE);
               holder.txt_readcount.setText(left_read);

               holder.txt_readcount2.setVisibility(View.GONE);
               holder.txt_readcount3.setVisibility(View.GONE);
               holder.txt_readcount4.setVisibility(View.GONE);


           }

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
        TextView txt_notice;
        ImageView img_img;
        ImageView img_img2;

        CircleImageView circleImageView ;

        TextView txt_readcount;
        TextView txt_readcount2;
        TextView txt_readcount3;
        TextView txt_readcount4;


        ViewHolder(View itemView) {
            super(itemView);

            img_img = (ImageView) itemView.findViewById(R.id.img_img);
            img_img2 = (ImageView) itemView.findViewById(R.id.img_img2);

            receivedMessage2 = (TextView) itemView.findViewById(R.id.receivedMessage2);
            sentMessage2 = (TextView) itemView.findViewById(R.id.sentMessage2);
            txt_chatName= (TextView) itemView.findViewById(R.id.txt_chatName);
            txt_notice = (TextView) itemView.findViewById(R.id.txt_notice);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.circleImageView);

            txt_readcount = (TextView) itemView.findViewById(R.id.txt_readcount);
            txt_readcount2 = (TextView) itemView.findViewById(R.id.txt_readcount2);
            txt_readcount3 = (TextView) itemView.findViewById(R.id.txt_readcount3);
            txt_readcount4 = (TextView) itemView.findViewById(R.id.txt_readcount4);

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
    private  void Get_img(ViewHolder holder, String  img_id,String userEmail,String msg_id){


        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    boolean success = jsonObject.getBoolean("success");

                    if (success) { // 이미지 업로드에 성공

                        //메시지 보내기 서비스에 전달
                        try {
                            String  img = jsonObject.getString("img");


                            String data =  img; //bitmap 변환

                            Bitmap bitmap =  ImageUtil.convert(data);

                            if(userEmail.equals(msg_id))
                            {
                                //내가 보낸 사진
                                holder.img_img2.setVisibility(View.VISIBLE);
                                holder.img_img2.setImageBitmap(bitmap);
                                holder.txt_chatName.setVisibility(View.GONE);

                                holder.circleImageView.setVisibility(View.GONE);

                                holder.txt_readcount.setVisibility(View.GONE);

                                holder.txt_readcount2.setVisibility(View.GONE);
                                holder.txt_readcount3.setVisibility(View.GONE);
                                holder.txt_readcount4.setVisibility(View.VISIBLE);
                                holder.txt_readcount4.setText(left_read);


                            }else
                            {
                                //다른사람이 보낸 사진
                                holder.img_img.setVisibility(View.VISIBLE);

                                holder.img_img.setImageBitmap(bitmap);
                                holder.txt_chatName.setText(msg_id);
                                holder.circleImageView.setVisibility(View.VISIBLE);

                                holder.txt_readcount.setVisibility(View.GONE);

                                holder.txt_readcount2.setVisibility(View.GONE);
                                holder.txt_readcount3.setVisibility(View.VISIBLE);
                                holder.txt_readcount3.setText(left_read);

                                holder.txt_readcount4.setVisibility(View.GONE);

                            }





                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else { // 이미지 업로드 실패
                        //Toast.makeText(getApplicationContext()," ",Toast.LENGTH_SHORT).show();

                        return;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        //String userEmail = PreferenceManager.getString(context, "userEmail");//쉐어드에서 로그인된 아이디 받아오기

        Get_img_Request get_img_request = new Get_img_Request(img_id, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(get_img_request);

    }
}
