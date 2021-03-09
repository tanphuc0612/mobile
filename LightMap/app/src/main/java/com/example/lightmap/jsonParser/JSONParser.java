package com.example.lightmap.jsonParser;

import com.example.lightmap.model.Bus.LegBus;
import com.example.lightmap.model.Leg;
import com.example.lightmap.model.Step;
import com.example.lightmap.model.Bus.StepBus;
import com.example.lightmap.model.TransitDetail;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
    private String origin, dest;

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Leg parseToLeg(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString("status").equals("OK")) {
            JSONObject routes = jsonObject.getJSONArray("routes").getJSONObject(0);
            JSONObject jLeg = routes.getJSONArray("legs").getJSONObject(0);
            JSONArray jStep = jLeg.getJSONArray("steps");

            String distance_text = jLeg.getJSONObject("distance").getString("text");
            String duration_text = jLeg.getJSONObject("duration").getString("text");

            Step[] steps = new Step[jStep.length()];
            for (int i = 0; i < jStep.length(); i++) {
                steps[i] = new Step();
                JSONObject step = jStep.getJSONObject(i);
                steps[i].setDistance(step.getJSONObject("distance").getString("text"));
                String html_instructions_formatted = step.getString("html_instructions")
//                        .replace("<b>", "")
//                        .replace("</b", "")
                        .replace("<div style=\"font-size:0.9em\">", "<br/>")
                        .replace("</div>", "");
                steps[i].setHtml_instructions(html_instructions_formatted);
                if (step.has("maneuver"))
                    steps[i].setManeuver(step.getString("maneuver"));

            }
//            steps[0] = new Step();
//            steps[0].setHtml_instructions(origin);
//            steps[jStep.length() + 1] = new Step();
//            steps[jStep.length() + 1].setHtml_instructions(dest);

            Leg leg = new Leg();
            leg.setDistance(distance_text);
            leg.setDuration(duration_text);
            leg.setSteps(steps);
            return leg;

        }
        return null;
    }

    public LegBus parseToLegBus(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString("status").equals("OK")) {
            JSONObject routes = jsonObject.getJSONArray("routes").getJSONObject(0);
            JSONObject jLeg = routes.getJSONArray("legs").getJSONObject(0);
            JSONArray jStep = jLeg.getJSONArray("steps");

            String distance_text = jLeg.getJSONObject("distance").getString("text");
            String duration_text = jLeg.getJSONObject("duration").getString("text");

            StepBus[] steps = new StepBus[jStep.length()];
            for (int i = 0; i < jStep.length(); i++) {
                steps[i] = new StepBus();
                JSONObject step = jStep.getJSONObject(i);
                String travel_mode = step.getString("travel_mode");
                if (travel_mode.equals("WALKING")) {
                    steps[i].setTravel_mode(TravelMode.WALKING);
                    steps[i].setDistance(step.getJSONObject("distance").getString("text")
                            + " (" + step.getJSONObject("duration").getString("text") + ")");
                    String html_instructions_formatted = step.getString("html_instructions")
                            .replace("<div style=\"font-size:0.9em\">", "<br/>")
                            .replace("</div>", "");
                    steps[i].setHtml_instructions(html_instructions_formatted);
                } else {
                    steps[i].setTravel_mode(TravelMode.TRANSIT);
                    steps[i].setDuration(step.getJSONObject("duration").getString("text"));
                    JSONObject transit_details = step.getJSONObject("transit_details");
                    TransitDetail transitDetail = new TransitDetail();

                    transitDetail.setDeparture_stop_name(transit_details.getJSONObject("departure_stop").getString("name"));
                    transitDetail.setBusName(transit_details.getJSONObject("line").getString("name"));
                    transitDetail.setHeadsign(transit_details.getString("headsign"));
                    transitDetail.setNum_stop(transit_details.getString("num_stops"));
                    transitDetail.setArrival_stop(transit_details.getJSONObject("arrival_stop").getString("name"));
                    steps[i].setTransitDetail(transitDetail);

                }
            }

            LegBus legBus = new LegBus();
            legBus.setDistance(distance_text);
            legBus.setDuration(duration_text);
            legBus.setStepBuses(steps);

            return legBus;
        }
        return null;
    }


}
