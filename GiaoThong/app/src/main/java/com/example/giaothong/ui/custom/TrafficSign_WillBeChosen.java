package com.example.giaothong.ui.custom;

public class TrafficSign_WillBeChosen {
    private int imagecode;

    public TrafficSign_WillBeChosen(int code){
        this.imagecode = code;
    }

    public int getCode(){
        return this.imagecode;
    }

    public void setImagecode(int code){
        this.imagecode = code;
    }
}
