package com.udacity.vehicles.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.domain.manufacturer.ManufacturerRepository;
import com.udacity.vehicles.service.CarNotFoundException;
import com.udacity.vehicles.service.CarService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


/**
 * Implements testing of the CarController class.
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(CarController.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@MockBean(JpaMetamodelMappingContext.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    @MockBean
    private CarResourceAssembler carResourceAssembler;

    @MockBean
    private ManufacturerRepository manufacturerRepository;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @BeforeEach
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any(Car.class))).willReturn(car);
        given(carService.findById(1L)).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));

        given(priceClient.getPrice(1L)).willReturn("USD 45678.9");
        given(mapsClient.getAddress(car.getLocation(), car.getId())).willReturn(car.getLocation());
        given(mapsClient.updateAddress(car.getLocation(), car.getId())).willReturn(car.getLocation());
        mvc = MockMvcBuilders.standaloneSetup(new CarController(carService, carResourceAssembler)).build();

    }

    @Test
    public void testCarDeserialization() throws JsonProcessingException {
        String json = "{\"location\":{\"lat\":40.730610,\"lon\":-73.935242}," +
                "\"details\":{\"manufacturer\":{\"code\":101,\"name\":\"Chevrolet\"}," +
                "\"model\":\"Impala\",\"mileage\":32280," +
                "\"externalColor\":\"white\",\"body\":\"sedan\"," +
                "\"engine\":\"3.6L V6\",\"fuelType\":\"Gasoline\"," +
                "\"modelYear\":2018,\"productionYear\":2018," +
                "\"numberOfDoors\":4}," +
                "\"condition\":\"USED\"}";

        ObjectMapper objectMapper = new ObjectMapper();
        Car car = objectMapper.readValue(json, Car.class);

        assertNotNull(car);
        assertEquals("Impala", car.getDetails().getModel());
        assertEquals(101, car.getDetails().getManufacturer().getCode());
        assertEquals("USED", car.getCondition().toString());
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        car.setId(2L);

        given(carService.save(any(Car.class))).willReturn(car);
        given(carResourceAssembler.toModel(car)).willReturn(EntityModel.of(car));

        mvc.perform(post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2));

        verify(carService, times(1)).save(any(Car.class));

    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */

    @Test
    public void listCars() throws Exception {

        Car car = getCar();
        car.setId(2L);

        given(carService.list()).willReturn(Collections.singletonList(car));

        given(carResourceAssembler.toModel(car)).willReturn(EntityModel.of(car));

        mvc.perform(get(new URI("/cars")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.links[0].href").value("http://localhost/cars"));


        verify(carService, times(1)).list();
    }


    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {

        Car car = getCar();
        car.setId(3L);

        given(carService.findById(car.getId())).willReturn(car);
        given(carResourceAssembler.toModel(car)).willReturn(EntityModel.of(car));

        mvc.perform(get(new URI("/cars/"+car.getId())))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                         .andExpect(jsonPath("$.id").value(car.getId()));

        verify(carService, times(1)).findById(car.getId());


    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {

        Car car = getCar();
        car.setId(4L);
        doNothing().when(carService).delete(car.getId());

        mvc.perform(delete(new URI("/cars/"+car.getId())))
                .andExpect(status().isNoContent());

    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
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