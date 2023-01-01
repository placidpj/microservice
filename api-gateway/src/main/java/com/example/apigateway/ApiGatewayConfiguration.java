package com.example.apigateway;

import java.util.function.Function;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

//	@Bean
//	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
////		Below pridicate tells, when http://localhost:8765/get is hit, 
////		then redirect the call to below uri() followed by path(). e.g> http://httpbin.org:80/get
//		Function<PredicateSpec, Buildable<Route>> routeFunction = p -> p.path("/get")
//				.uri("http://httpbin.org:80");
//		return builder.routes().route(routeFunction).build();
//	}
	
//	@Bean
//	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
////		Apart from redirect, now we also want to send header
//		Function<PredicateSpec, Buildable<Route>> routeFunction = p -> p.path("/get")
////				In filters let's say we want to add few authentication parameters. 
////				The api-gateway is adding headers in the request before calling the uri("http://httpbin.org:80")
//				.filters(f -> f.addRequestHeader("MyHeader", "MyURI")
//						.addRequestParameter("Param", "MyValue"))
//				.uri("http://httpbin.org:80"); // uri() should be the host, should not contain path
//		return builder.routes().route(routeFunction).build();
//	}
	
	
//	This URL: http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR
//	looks little complex because currency-exchange is repeating twice. So what we want to do is that, whenever
//	we get a request with /currency-exchange redirect it to the naming server.
//	So that new URL would look like this: "http://localhost:8765/currency-exchange/from/USD/to/INR"
//	For above thing to work we will comment out below properties in application.properties
//	spring.cloud.gateway.discovery.locator.enabled=true
//	spring.cloud.gateway.discovery.locator.lower-case-service-id=true
//	
	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
//		Apart from redirect, now we also want to send header
		Function<PredicateSpec, Buildable<Route>> routeFunction = p -> p.path("/get")
//				In filters let's say we want to add few authentication parameters. 
//				The api-gateway is adding headers in the request before calling the uri("http://httpbin.org:80")
				.filters(f -> f.addRequestHeader("MyHeader", "MyURI")
						.addRequestParameter("Param", "MyValue"))
				.uri("http://httpbin.org:80"); // uri() should be the host, should not contain path
		
		// In below function, the request will be redirected to the eureka server, then load balancer between 
		// the instances that are returned. 
		Function<PredicateSpec, Buildable<Route>> redirectToEurekaAndThenLoadBalance = 
				p -> p.path("/currency-exchange/**")  // Any URI starting with /currency-exchange and is followed by anything
				.uri("lb://currency-exchange");  // Here "currency-exchange is the name of the registration on the Eureka server."
		
		//Hit from browser: http://localhost:8765/currency-conversion/from/USD/to/INR/quantity/10
		Function<PredicateSpec, Buildable<Route>> redirectToEurekaAndThenLoadBalanceCurrencyConversion = 
				p -> p.path("/currency-conversion/**")  
				.uri("lb://currency-conversion-service"); 
				
		//Hit from browser: http://localhost:8765/currency-conversion-feign/from/USD/to/INR/quantity/10
		Function<PredicateSpec, Buildable<Route>> redirectToEurekaAndThenLoadBalanceCurrencyConversionFeign = 
				p -> p.path("/currency-conversion-feign/**")  
				.uri("lb://currency-conversion-service");				
				
		//Hit from browser: http://localhost:8765/currency-conversion-new/from/USD/to/INR/quantity/10
		Function<PredicateSpec, Buildable<Route>> redirectToEurekaAndThenLoadBalanceCurrencyConversionRedirect = 
				p -> p.path("/currency-conversion-new/**")
				.filters(f -> f.rewritePath(
						"/currency-conversion-new/(?<segment>.*)",  // what to replace? currency-conversion-new followed by further path in the URL, here called as  segment.
						"/currency-conversion-feign/${segment}"))  // The new string to be put in. Here ${segment} is a regular expression.
				.uri("lb://currency-conversion-service");
				
		return builder.routes()
				.route(routeFunction)
				.route(redirectToEurekaAndThenLoadBalance)
				.route(redirectToEurekaAndThenLoadBalanceCurrencyConversion)
				.route(redirectToEurekaAndThenLoadBalanceCurrencyConversionFeign)
				.route(redirectToEurekaAndThenLoadBalanceCurrencyConversionRedirect)
				.build();
	}
	
}
