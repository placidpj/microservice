package com.example.currencyexchangeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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

	
//  Circuit breaker have 3 stages. Initially when application start it is in Closed state. If all the requests 
//	are failing then it will go to Open state and then it will occasionally hit the endpoint i.e. that is it changed to half-open state
//	to see if they are working, if yes, then status will change to Closed otherwise it will go to half-open 
//	status where it occasionally hit the endpoint to check if the endpoint started working. 
//	If circuit breaker goes to open state then the controll will  not go inside the below method i.e. will not pring
//	the log statement. 
	@GetMapping("/sample-api-circuit-breaker")
//	@Retry(name = "sample-api-name", fallbackMethod = "hardcodedResponse")
	@CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
	public String sampleApiCircuitBreaker() {
		log.info("Sample api circuit breaker called");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:1234/unavailable",
				String.class);
		return forEntity.getBody();
	}

//	Puts limit on the number of calls made per unit of time. eg.10 calls per minute
//	@RateLimiter(name="default")
	
//	Number of concurrent calls that can be allowed.
//	@Bulkhead(name="sample-api")
	
	
}
