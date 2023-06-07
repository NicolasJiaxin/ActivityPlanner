package com.nicolas.activityplanner.algorithms;

import com.nicolas.activityplanner.Place;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    private long travelTimeCost;
    private long distanceCost;
    private int visitTimeCost;
    private List<Place> itinerary;

    public Plan(long travelTimeCost, long distanceCost, int visitTimeCost, List<Place> itinerary) {
        this.travelTimeCost = travelTimeCost;
        this.distanceCost = distanceCost;
        this.visitTimeCost = visitTimeCost;
        this.itinerary = itinerary;
    }

    public Plan() {
        this.travelTimeCost = 0;
        this.distanceCost = 0;
        this.visitTimeCost = 0;
        this.itinerary = new ArrayList<>();
    }

    public void addPlaceToItinerary(Place place) {
        itinerary.add(place);
    }

    public Place getPlaceByIndex(int i) {
        return itinerary.get(i);
    }

    public long getTravelTimeCost() {
        return travelTimeCost;
    }

    public void setTravelTimeCost(long travelTimeCost) {
        this.travelTimeCost = travelTimeCost;
    }

    public int getVisitTimeCost() {
        return visitTimeCost;
    }

    public void setVisitTimeCost(int visitTimeCost) {
        this.visitTimeCost = visitTimeCost;
    }

    public long getDistanceCost() {
        return distanceCost;
    }

    public void setDistanceCost(long distanceCost) {
        this.distanceCost = distanceCost;
    }

    public List<Place> getItinerary() {
        return itinerary;
    }

    public void setItinerary(List<Place> itinerary) {
        this.itinerary = itinerary;
    }

    @Override
    public String toString() {
        StringBuffer stringBuf = new StringBuffer("" +
                "Plan{travelTimeCost=" + travelTimeCost +
                ", visitTimeCost=" + visitTimeCost +
                ", distanceCost=" + distanceCost +
                ", itinerary=[");
        for (Place place : itinerary) {
            stringBuf.append(place.toString());
            stringBuf.append(", ");
        }

        stringBuf.delete(stringBuf.length() - 2, stringBuf.length() - 1);
        stringBuf.append("]}");

        return stringBuf.toString();
    }
}
