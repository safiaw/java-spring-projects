package com.udacity.vehicles.api;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping("/cars")
class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    /**
     * Creates a list to store any vehicles.
     * @return list of vehicles
     */
    @Operation(summary = "Get a list of all cars", description = "Returns a list of all cars with all its details and URI links")
    @ApiResponse(responseCode = "201", description = "Successfully return a response containing a list of all cars")
    @GetMapping
    ResponseEntity<CollectionModel<EntityModel<Car>>> list()
    {
        List<EntityModel<Car>> resources = carService.list()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Car>> collectionModel = CollectionModel.of(resources);
        collectionModel.add(linkTo(methodOn(CarController.class).list()).withSelfRel());
        URI uri = linkTo(methodOn(CarController.class).list()).withSelfRel().toUri();

        return ResponseEntity.created(uri).body(collectionModel);

    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @Operation(summary = "Get a car by ID", description = "Returns a response containing a car with the given ID")
    @ApiResponse(responseCode = "201", description = "Successfully return a response containing a car with the given ID")
    @ApiResponse(responseCode = "404", description = "Car not found exception when the given ID does not exist in the repository")
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<Car>> get(@PathVariable Long id) {

        Car car = carService.findById(id);
        EntityModel<Car> resource = assembler.toModel(car);
        URI uri = linkTo(methodOn(CarController.class).get(car.getId())).toUri();
        return ResponseEntity.created(uri).body(resource);
    }

    /**
     * Posts information to create a new vehicle in the system.
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @Operation(summary = "Create a new car", description = "Create a new car and add the new car object into repository")
    @ApiResponse(responseCode = "201", description = "Successfully return a response containing the newly created car")
    @ApiResponse(responseCode = "404", description = "Car not found exception when the new car ID does not exist in the repository")
    @PostMapping
    ResponseEntity<EntityModel<Car>> post(@Valid @RequestBody Car car) throws URISyntaxException{

        Car newCar = carService.save(car);
        EntityModel<Car> resource = assembler.toModel(newCar);
        URI uri = linkTo(methodOn(CarController.class).get(newCar.getId())).toUri();
        return ResponseEntity.created(uri).body(resource);

    }

    /**
     * Updates the information of a vehicle in the system.
     * @param existingId The ID number for which to update vehicle information.
     * @param newCar The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @Operation(summary = "Update a new car ID with the existing ID", description = "Takes a new car object and the existing ID of an existing Car and update the new car ID with the existing ID. This replaces the existing car details with the new car details in the repository")
    @ApiResponse(responseCode = "201", description = "Successfully return a response containing the updated car which is a new car with the existing ID")
    @ApiResponse(responseCode = "404", description = "Car not found exception when the existing ID does not exist in the repository")
    @PutMapping("/{existingId}")
    ResponseEntity<EntityModel<Car>> put(@PathVariable Long existingId, @Valid @RequestBody Car newCar) {

        Car exisitingCar = carService.findById(existingId);
        newCar.setId(exisitingCar.getId());
        Car updatedCar = carService.save(newCar);
        EntityModel<Car> resource = assembler.toModel(updatedCar);
        return ResponseEntity.ok(resource);
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @Operation(summary = "Delete a car given its ID", description = "Find the car object having the given ID and deletes it")
    @ApiResponse(responseCode = "204", description = "Successfully return a response with no content suggesting successful operation")
    @ApiResponse(responseCode = "404", description = "Car not found exception when the given ID does not exist in the repository")
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {

        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
