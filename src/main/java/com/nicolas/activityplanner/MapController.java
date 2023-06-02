package com.nicolas.activityplanner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
