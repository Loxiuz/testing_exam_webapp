package com.testing_exam_webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDto {
    private String city;
    private String country;
    private Double temperature;
    private String description;
    private String icon;
    private Double humidity;
    private Double windSpeed;
    private String condition;
}

