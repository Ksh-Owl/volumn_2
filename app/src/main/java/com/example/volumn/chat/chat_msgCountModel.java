package com.example.volumn.chat;

public class chat_msgCountModel {

   private String title;
   private String msg;



    public chat_msgCountModel(String title,String msg){
        this.title = title;
        this.msg = msg;


    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
