package com.example.volumn.addWorkout;

import java.io.Serializable;

public class Model implements Serializable {

    private String workout_Name;
    private String workout_Part;
    private String workout_ID;
    private String SET_ID;
    private String SET_count;



    private boolean isSelected = false;

    public Model(String workout_Name,String workout_Part,String workout_ID, String SET_ID){
        this.workout_Name = workout_Name;
        this.workout_Part = workout_Part;
        this.workout_ID = workout_ID;
        this.SET_ID = SET_ID;
        //this.SET_count = SET_count;

    }


    public String getWorkout_Name(){
        return workout_Name;
    }

    public String getWorkout_Part(){return  workout_Part;}

    public String getWorkout_ID(){return  workout_ID;}

    public String getSET_ID(){return  SET_ID;}

    public String getSET_size(){return SET_count;}

    public void setSET_size(String SET_count){this.SET_count = SET_count;}



    public void setSelected(boolean selected){
        isSelected = selected;
    }
    public  boolean isSelected(){

        return isSelected;
    }
}
