package com.example.microservice.limitsservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.microservice.limitsservice.bean.Limits;

@RestController
public class LimitsController {

	@GetMapping("/limits")
	public Limits retrieveLimits() {
		return new Limits(1, 1);
	}
}
