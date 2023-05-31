package com.nicolas.activityplanner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MapController {

    public PlaceService placeService;

    public MapController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String display(ModelMap model) {
        model.put("place", new Place(0,"", 0,0, 0));
        model.put("placesList", placeService.getPlacesList());
        return "map";
    }

    @RequestMapping(value="/", method=RequestMethod.POST)
    public String display(ModelMap model, Place place) {
        placeService.addPlace(place);
        System.out.println(placeService.getPlacesList().size());
        return "redirect:/";
    }
}
