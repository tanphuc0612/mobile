package com.example.giaothong.data;

public class Leg {
    private String distance;
    private String duration;
    private com.example.giaothong.data.Step[] steps;

    public com.example.giaothong.data.Step[] getSteps() {
        return steps;
    }

    public void setSteps(com.example.giaothong.data.Step[] steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


}
