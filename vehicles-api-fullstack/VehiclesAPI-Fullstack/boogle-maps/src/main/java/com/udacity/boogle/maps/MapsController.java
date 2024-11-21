package com.udacity.boogle.maps;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maps")
public class MapsController {

    @GetMapping
    public Address get(@RequestParam Double lat, @RequestParam Double lon, @RequestParam Long vehicleId)
    {
        return MapsService.getAddress(vehicleId);
    }

    @PutMapping
    public Address put(@RequestParam Double lat, @RequestParam Double lon, @RequestParam Long vehicleId){
        return MapsService.updateAddress(vehicleId);
    }
}
