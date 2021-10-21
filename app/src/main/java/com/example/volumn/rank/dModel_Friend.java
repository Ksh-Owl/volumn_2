package com.example.volumn.rank;

public class dModel_Friend {

    private String friend_Name;
   // private String friend_Email;
    private String friend_state;
    private String ID;
    private String friend_ID;

    public dModel_Friend(String friend_Name,String friend_state,String friend_ID ,String ID){
        this.friend_Name = friend_Name;
        this.friend_state = friend_state;
        this.friend_ID = friend_ID;
        this.ID = ID;

    }

    public String getFriend_Name(){
        return friend_Name;
    }


    public String getFriend_state(){return  friend_state;}

    public String getFriend_ID(){return  friend_ID;}
    public String getID(){return  ID;}


}
