package com.example.currencyexchangeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBreakerController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@GetMapping("/sample-api-default")
	@Retry(name = "default")  // It will hit the below endpoint ("http://localhost:1234/unavailable") 3 times(default value) at max if it fails.
	public String sampleApiDefault() {
		log.info("Sample api default called");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:1234/unavailable",
				String.class);
		return forEntity.getBody();
	}
	
	
//	For custom configuration, The number to retries can be configured in application.properties file with property
//	resilience4j.retry.instances.sample-api-name.max-attempts=5
//	In above property the "sample-api-name" should be taken from below @Retry annotation. 
//	-------------------------------------
//	For fallbackMethod, we will have to create a method with the name that is provided in below @Retry annotation
//	currently "hardcodedResponse" is used, so create a method with "hardcodedResponse" and in parameters 
//	it should value of Throwable type 
//	Here, 5 retries will be done and it will be still unsuccessful, then fallback response will be returned.
	@GetMapping("/sample-api")
	@Retry(name = "sample-api-name", fallbackMethod = "hardcodedResponse") 
	public String sampleApi() {
		log.info("Sample api called");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:1234/unavailable",
				String.class);
		return forEntity.getBody();
	}
	
	public String hardcodedResponse(Exception ex) {
		return "fallback-response";
	}
}
