package com.nicolas.activityplanner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MapController {

    public PlaceService placeService;

    public MapController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String display(ModelMap model) {
        return "map";
    }

    @RequestMapping(value="/compute", method = RequestMethod.POST)
    @ResponseBody
    public List<Place> processPlaces(@RequestParam("days") int days, @RequestBody List<Place> places) {
        for (int i = 0; i < places.size(); i++) {
            System.out.println(places.get(i));
        }
        placeService.setPlacesList(places);
        placeService.computeDistance();

        return places;
    }
}
