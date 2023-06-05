package com.nicolas.activityplanner;

import com.nicolas.activityplanner.algorithms.Plan;
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
    public Plan[] processPlaces(@RequestParam("days") int days, @RequestBody List<Place> places) {
        for (int i = 0; i < places.size(); i++) {
            System.out.println(places.get(i));
        }
        placeService.setPlacesList(places);
        Plan[] plans = placeService.getPlan(days);

        return plans;
    }
}
