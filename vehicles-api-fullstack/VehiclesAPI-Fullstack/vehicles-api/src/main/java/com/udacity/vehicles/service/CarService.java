package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final PriceClient priceClient;
    private final MapsClient mapsClient;
    private final ModelMapper modelMapper;

    @Autowired
    public CarService(ModelMapper modelMapper, CarRepository repository, @Qualifier("maps") WebClient mapClient, @Qualifier("pricing") WebClient pricingClient) {

        this.repository = repository;
        this.priceClient = new PriceClient(pricingClient);
        this.mapsClient = new MapsClient(mapClient, modelMapper);
        this.modelMapper = modelMapper;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {

        return this.repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        Optional<Car> optionalCar = this.repository.findById(id);
        Car car;
        if(optionalCar.isPresent()){
            car = optionalCar.get();
        }
        else
        {
            throw new CarNotFoundException("Car not found");
        }

        car.setPrice(this.priceClient.getPrice(id));
        car.setLocation(this.mapsClient.getAddress(car.getLocation(), car.getId()));

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(this.mapsClient.updateAddress(car.getLocation(), car.getId()));
                        carToBeUpdated.setCondition(car.getCondition());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
         Optional<Car> optionalCar = this.repository.findById(id);
         if(optionalCar.isPresent()){
             this.repository.delete(optionalCar.get());
             this.priceClient.updatePrice(id);
         }
         else {
             throw new CarNotFoundException("Car not found");
         }
    }
}
