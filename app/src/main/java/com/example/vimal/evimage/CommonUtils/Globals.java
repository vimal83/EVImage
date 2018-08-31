package com.example.vimal.evimage.CommonUtils;

public class Globals {
    private static Globals instance;

    // Global variable
    private String AuthKey;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setData(String d){
        this.AuthKey=d;
    }
    public String getData(){
        return this.AuthKey;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
