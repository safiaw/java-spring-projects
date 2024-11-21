package com.udacity.pricing;


import com.netflix.discovery.DiscoveryClient;
import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.domain.price.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;


	@Test
	public void contextLoads() {
	}


	@Test
	public void testGetPrice(){

		Long testId=1L;
		String url = UriComponentsBuilder.fromHttpUrl("http://localhost:"+port +"/services/price")
				.queryParam("vehicleId", testId)
				.toUriString();
		ResponseEntity<Price> response = this.restTemplate.exchange(url,
				HttpMethod.GET,
				null,
				Price.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}

	@Test
	public void testGetPriceNotFound(){

		Long testId=21L;
		String url = UriComponentsBuilder.fromHttpUrl("http://localhost:"+port+"/services/price")
				.queryParam("vehicleId", testId)
				.toUriString();
		ResponseEntity<Price> response = this.restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				Price.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}


}
