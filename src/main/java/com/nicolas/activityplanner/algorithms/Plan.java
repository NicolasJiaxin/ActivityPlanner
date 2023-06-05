package com.nicolas.activityplanner.algorithms;

import com.nicolas.activityplanner.Place;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    private long timeCost;
    private List<Place> itinerary;

    public Plan(long timeCost, List<Place> itinerary) {
        this.timeCost = timeCost;
        this.itinerary = itinerary;
    }

    public Plan(long timeCost) {
        this.timeCost = timeCost;
        this.itinerary = new ArrayList<>();
    }

    public void addPlaceToItinerary(Place place) {
        itinerary.add(place);
    }

    public Place getPlaceByIndex(int i) {
        return itinerary.get(i);
    }

    public long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(long timeCost) {
        this.timeCost = timeCost;
    }

    public List<Place> getItinerary() {
        return itinerary;
    }

    public void setItinerary(List<Place> itinerary) {
        this.itinerary = itinerary;
    }

    @Override
    public String toString() {
        StringBuffer stringBuf = new StringBuffer("Plan{timeCost=" + timeCost + ", itinerary=[");
        for (Place place : itinerary) {
            stringBuf.append(place.toString());
            stringBuf.append(", ");
        }

        stringBuf.delete(stringBuf.length() - 2, stringBuf.length() - 1);
        stringBuf.append("]}");

        return stringBuf.toString();
    }
}
