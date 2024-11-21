package com.udacity.boogle.maps;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class MapsService {

    private static final Map<Long, Address> locations = new HashMap<>();

    public  static Address getAddress(Long vehicleId){
        if (locations.containsKey(vehicleId)){
            return locations.get(vehicleId);
        }
        else{
            return locations.put(vehicleId, MockAddressRepository.getRandom());
        }
    }

    public static Address updateAddress(Long vehicleId){
        return locations.put(vehicleId, MockAddressRepository.getRandom());
    }
}
