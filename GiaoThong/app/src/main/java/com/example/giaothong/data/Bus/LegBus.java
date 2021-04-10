package com.example.giaothong.data.Bus;

public class LegBus {
    private String arrival_time;
    private String departure_time;
    private String duration;
    private String distance;
    private String start_address;
    private String end_address;
    private StepBus[] stepBuses;

    public StepBus[] getStepBuses() {
        return stepBuses;
    }

    public void setStepBuses(StepBus[] stepBuses) {
        this.stepBuses = stepBuses;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStart_address() {
        return start_address;
    }

    public void setStart_address(String start_address) {
        this.start_address = start_address;
    }

    public String getEnd_address() {
        return end_address;
    }

    public void setEnd_address(String end_address) {
        this.end_address = end_address;
    }


}
