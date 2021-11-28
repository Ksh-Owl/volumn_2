package com.example.volumn.chat;

public class msg_Model {



    private String msg;
    private String time ;
    private String img_id ;


    public msg_Model(String msg, String time, String img_id){
        this.msg = msg;
        this.time = time;
        this.img_id = img_id;


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
}
