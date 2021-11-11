package com.example.volumn.chat;

public class chatRoom_Model {



    private String room_ID;
    private String room_nm ;
    private String member ;
    private String mem_count ;
    private String CREATE_DATE;

    public chatRoom_Model(String room_ID,String room_nm,String member, String mem_count,String CREATE_DATE ){
        this.room_ID = room_ID;
        this.room_nm = room_nm;
        this.member = member;
        this.mem_count = mem_count;
        this.CREATE_DATE = CREATE_DATE;


    }

    public void setRoom_ID(String room_ID) {
        this.room_ID = room_ID;
    }

    public String getRoom_ID() {
        return room_ID;
    }

    public String getRoom_nm() {
        return room_nm;
    }

    public void setRoom_nm(String room_nm) {
        this.room_nm = room_nm;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getMem_count() {
        return mem_count;
    }

    public void setMem_count(String mem_count) {
        this.mem_count = mem_count;
    }

    public String getCREATE_DATE() {
        return CREATE_DATE;
    }

    public void setCREATE_DATE(String CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }
}
