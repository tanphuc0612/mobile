package com.example.giaothong.data.Bus;

import com.example.giaothong.data.TransitDetail;
import com.google.maps.model.TravelMode;

public class StepBus {
    private TravelMode travel_mode;
    private String distance;
    private String html_instructions;
    private TransitDetail transitDetail;
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public TransitDetail getTransitDetail() {
        return transitDetail;
    }

    public void setTransitDetail(TransitDetail transitDetail) {
        this.transitDetail = transitDetail;
    }

    public TravelMode getTravel_mode() {
        return travel_mode;
    }

    public void setTravel_mode(TravelMode travel_mode) {
        this.travel_mode = travel_mode;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

}
