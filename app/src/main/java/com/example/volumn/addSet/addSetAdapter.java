package com.example.volumn.addSet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.volumn.R;
import com.example.volumn.addWorkout.Model;
import com.example.volumn.addWorkout.workoutListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class addSetAdapter extends RecyclerView.Adapter<addSetAdapter.ViewHolder> {


    private ArrayList<Model> mData = null;
    private workoutListAdapter.OnItemClickListener mListener = null;
    private workoutListAdapter.OnItemClickListener mLongListener = null;
    private Context context;
    private String Date;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(workoutListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(workoutListAdapter.OnItemClickListener listener) {
        this.mLongListener = listener;
    }

    public addSetAdapter(Context context, ArrayList<Model> data, String Date) {
        mData = data;
        this.context = context;
        this.Date = Date;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.addset_cell, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData == null) {
            return;
        }

        Log.v("item", " item ::  " + position);

        String name = mData.get(position).getWorkout_Name().trim();
        String part = mData.get(position).getWorkout_Part().trim();
        String SET_ID = mData.get(position).getSET_ID().trim();
        String workout_ID = mData.get(position).getWorkout_ID().trim();


        holder.workout_Name.setText(name);
        holder.workout_Part.setText(part);

        //ArrayList<setDataModel> set_list = new ArrayList<>();
        //setDataModel model = new setDataModel("1set","100kg","10???");

        //set_list.add(model);


        //
        //????????? ???????????? ???????????? ????????? ????????? ??????

        ArrayList<setDataModel> set_List = new ArrayList<>();

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int sum_kg = 0;

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("workout");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);


                        String Perform_ID = item.getString("Perform_ID");

                        String Perform_set = item.getString("Perform_set");
                        String Perform_count = item.getString("Perform_count");
                        String Perform_weight = item.getString("Perform_weight");
                        //String SET_ID = item.getString("SET_ID");

                        setDataModel model = new setDataModel(Perform_ID,Perform_set, Perform_count, Perform_weight);// ?????? ?????? ??????

                        set_List.add(model);//
                        sum_kg = sum_kg + (Integer.parseInt(Perform_weight) * Integer.parseInt(Perform_count));


                    }

                    holder.txt_sumSet.setText(set_List.size() + "??????");
                    holder.txt_sumKg.setText(sum_kg + "KG");

                    SetAdapter adapter = new SetAdapter(set_List);
                    mData.get(position).setSET_size(Integer.toString(set_List.size() + 1));


                    holder.rv_set.setAdapter(adapter);


                    holder.rv_set.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));





                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        //String userEmail = PreferenceManager.getString(context, "userEmail");//??????????????? ???????????? ????????? ????????????

        addSetInnerRequest addSetInnerRequest = new addSetInnerRequest(SET_ID, responseListner);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(addSetInnerRequest);


        ///

        holder.img_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ???????????? ????????????
                // workout_ID ??? workoutList_Record ??????
                // SET_ID??? set_list ??????
                // SET_ID??? perform_Record ??????

                //????????? ?????? ?????? ???????????? ??????

                Intent intent = new Intent(context, pop_addSet_Del_Activity.class);//????????? ?????????

                intent.putExtra("name", name);//????????????
                intent.putExtra("SET_ID", SET_ID);//??????ID

                context.startActivity(intent);


            }
        });


        holder.btn_addSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //set = Integer.toString(set_List.size()+1) ;
                //String set = mData.get(position).getSET_size().trim() ;

                //?????? ID,?????? ID,??????
                try {
                    Intent intent = new Intent(context, pop_addSetActivity.class);//????????? ?????????

                    intent.putExtra("name", name);//??????

                    intent.putExtra("set", mData.get(position).getSET_size().trim());//??????

                    intent.putExtra("Date", Date);//??????
                    intent.putExtra("workout_ID", mData.get(position).getWorkout_ID().trim());

                    intent.putExtra("SET_ID", SET_ID);//??????ID


                    context.startActivity(intent);
                }catch (Exception ex)
                {

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView workout_Name;
        TextView workout_Part;
        TextView txt_sumSet;
        TextView txt_sumKg;

        ImageView img_clear;

        RecyclerView rv_set;
        Button btn_addSet;


        ViewHolder(View itemView) {
            super(itemView);

            workout_Name = itemView.findViewById(R.id.txt_info);
            workout_Part = itemView.findViewById(R.id.txt_workoutPart);
            txt_sumSet = itemView.findViewById(R.id.txt_sumSet);
            txt_sumKg = itemView.findViewById(R.id.txt_sumKg);

            rv_set = itemView.findViewById(R.id.rv_set);
            btn_addSet = itemView.findViewById(R.id.btn_addSet);
            img_clear = itemView.findViewById(R.id.img_reject);


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
