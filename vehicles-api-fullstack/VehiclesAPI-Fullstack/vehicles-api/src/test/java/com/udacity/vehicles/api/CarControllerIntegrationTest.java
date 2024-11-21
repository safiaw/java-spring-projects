package com.udacity.vehicles.api;

import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CarControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarRepository carRepository;


    @BeforeEach
    void setUp() {

        Car car = getCar();
        car.setId(1L);
        // Save the car to the database
        this.carRepository.save(car);
    }

    @Test
    public void testGetAllCars() {

        ResponseEntity<CollectionModel<EntityModel<Car>>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CollectionModel<EntityModel<Car>>>() {}
        );

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    @Test
    public void testGetCarById() {

        ResponseEntity<EntityModel<Car>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/cars/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    @Test
    public void testCreateCar(){

        Car car = getCar();
        HttpEntity<Car> requestEntity = new HttpEntity<>(car);
        ResponseEntity<EntityModel<Car>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/cars",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.hasBody(),is(true));
    }
    @Test
    public void testUpdateCar(){
        Car existingCar = getCar();
        this.carRepository.save(existingCar);
        Long existingId = existingCar.getId();
        Car newCar = getCar();
        HttpEntity<Car> requestEntity = new HttpEntity<>(newCar);
        ResponseEntity<EntityModel<Car>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/cars/{existingId}",
                HttpMethod.PUT,
                requestEntity,
                new ParameterizedTypeReference<>() {}, existingId);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody().getContent().getId(),is(existingId));


    }
    @Test
    public void testDeleteCar(){

        Car car = getCar();
        this.carRepository.save(car);
        Long carId = car.getId();
        ResponseEntity<EntityModel<Car>> response = this.restTemplate.exchange(
                "http://localhost:" + port + "/cars/"+carId,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

        ResponseEntity<EntityModel<Car>> response2 =  this.restTemplate.exchange(
                        "http://localhost:" + port + "/cars/"+carId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {});

        assertThat(response2.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));

    }

    private Car getCar() {
        Car car = new Car();
        Location location = new Location(40.730610, -73.935242);
        location.setAddress("1470 S Washington St");
        location.setState("MA");
        location.setCity("North Attleboro");
        location.setZip("2760");
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setLocation(location);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        car.setPrice("USD 23489.97");
        car.setCreatedAt(LocalDateTime.now());
        car.setModifiedAt(LocalDateTime.now());
        return car;
    }

}
