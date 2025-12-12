package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.TimeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class TimeService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String defaultTimezone = "Europe/Copenhagen";

    public TimeService(@Value("${time.api.url}") String apiUrl) {
        this.apiUrl = apiUrl;
        this.restTemplate = new RestTemplate();
    }

    public TimeDto getCurrentTime(String timezone) {
        String timezoneToUse = (timezone == null || timezone.trim().isEmpty()) 
                ? defaultTimezone 
                : timezone.trim();

        try {
            String url = String.format("%s/timezone/%s", apiUrl, timezoneToUse);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return mapToTimeDto(response.getBody());
            } else {
                return createDefaultTime();
            }
        } catch (Exception e) {
            // On any error, return default time
            return createDefaultTime();
        }
    }

    public TimeDto getCurrentTime() {
        return getCurrentTime(defaultTimezone);
    }

    private TimeDto mapToTimeDto(Map<String, Object> response) {
        TimeDto dto = new TimeDto();
        dto.setDatetime((String) response.get("datetime"));
        dto.setTimezone((String) response.get("timezone"));
        dto.setAbbreviation((String) response.get("abbreviation"));
        
        Object dayOfWeek = response.get("day_of_week");
        if (dayOfWeek instanceof Number) {
            dto.setDayOfWeek(((Number) dayOfWeek).intValue());
        }
        
        Object dayOfYear = response.get("day_of_year");
        if (dayOfYear instanceof Number) {
            dto.setDayOfYear(((Number) dayOfYear).intValue());
        }

        return dto;
    }

    private TimeDto createDefaultTime() {
        TimeDto dto = new TimeDto();
        LocalDateTime now = LocalDateTime.now();
        dto.setDatetime(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setTimezone(defaultTimezone);
        dto.setAbbreviation("CET");
        dto.setDayOfWeek(now.getDayOfWeek().getValue());
        dto.setDayOfYear(now.getDayOfYear());
        return dto;
    }
}

