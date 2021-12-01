package com.example.volumn.chat;

import org.json.JSONArray;

import java.util.ArrayList;

public class msg_Model {



    private String msg;
    private String time ;
    private String img_id ;
    private String read_count;
    private JSONArray read_mem = new JSONArray() ;


    public msg_Model(String msg, String time, String img_id){//,String read_count,JSONArray read_mem
        this.msg = msg;
        this.time = time;
        this.img_id = img_id;
       // this.read_count = read_count;
      //  this.read_mem = read_mem ;

    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setRead_count(String read_count) {
        this.read_count = read_count;
    }

    public String getRead_count() {
        return read_count;
    }

    public void addRead_mem(String read_mem) {
        this.read_mem.put(read_mem) ;
    }

    public JSONArray getRead_mem() {
        return read_mem;
    }
}
