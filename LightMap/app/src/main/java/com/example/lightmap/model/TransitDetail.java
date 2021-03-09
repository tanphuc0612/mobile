package com.example.lightmap.model;

public class TransitDetail {
    private String departure_stop_name;
    private String busName;
    private String headsign;
    private String num_stop;


    private String arrival_stop;

    public String getDeparture_stop_name() {
        return departure_stop_name;
    }

    public void setDeparture_stop_name(String departure_stop_name) {
        this.departure_stop_name = departure_stop_name;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getNum_stop() {
        return num_stop;
    }

    public void setNum_stop(String num_stop) {
        this.num_stop = num_stop;
    }

    public String getArrival_stop() {
        return arrival_stop;
    }

    public void setArrival_stop(String arrival_stop) {
        this.arrival_stop = arrival_stop;
    }
}
