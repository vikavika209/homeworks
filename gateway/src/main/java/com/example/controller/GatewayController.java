package com.example.controller;

import com.example.service.GatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class GatewayController {
    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("info/{passport}")
    public ResponseEntity<Map<String, String>> info(@PathVariable String passport) {
        return ResponseEntity.ok(gatewayService.getPersonVaccinationFullData(passport));
    }
}
