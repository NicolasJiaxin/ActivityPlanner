package com.nicolas.activityplanner;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaceService {
    private static List<Place> placesList = new ArrayList<>();
    private static int count = 0;

    public List<Place> getPlacesList() {
        return placesList;
    }

    public void addPlace(Place place) {
        place.setId(++count);
        placesList.add(place);
    }
}
