package com.example.volumn.rank;

public class dModel_Rank {

    private int Rank;
    private String Name;
    private String Volumn;

    public dModel_Rank(int Rank,String Name,String Volumn ){
        this.Rank = Rank;
        this.Name = Name;
        this.Volumn = Volumn;

    }

    public int getRank(){
        return Rank;
    }


    public String getName(){return  Name;}

    public String getVolumn(){return  Volumn;}


}
