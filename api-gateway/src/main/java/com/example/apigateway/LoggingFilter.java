package com.example.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter {
	
	private Logger log = LoggerFactory.getLogger(getClass());

//	We want to log some information about the request.
//	Information about the request is available in the first parameter 'exchange'.
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("Path of the request received -> {}", exchange.getRequest().getPath());
		
		// After logging the request we want to let the execution continue as it was.
		return chain.filter(exchange);
	}
	
	

}
