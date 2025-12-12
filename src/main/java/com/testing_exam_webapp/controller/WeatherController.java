package com.testing_exam_webapp.controller;

import com.testing_exam_webapp.dto.WeatherDto;
import com.testing_exam_webapp.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<WeatherDto> getWeather(
            @RequestParam(required = false, defaultValue = "Copenhagen") String city) {
        WeatherDto weather = weatherService.getWeatherByCity(city);
        return new ResponseEntity<>(weather, HttpStatus.OK);
    }
}

