package com.example.volumn.addSet;

public class setDataModel {

    private String perform_ID;
    private String set;
    private String set_kg;
    private String set_count;


    public setDataModel(String perform_ID,String set,String set_kg,String set_count){
        this.perform_ID = perform_ID;
        this.set = set;
        this.set_kg = set_kg;
        this.set_count = set_count;

    }
    public String getPerform_ID(){return  perform_ID;}

    public String getSet(){
        return set;
    }

    public String getSet_kg(){return  set_kg;}

    public String getSet_count(){return  set_count;}
}
