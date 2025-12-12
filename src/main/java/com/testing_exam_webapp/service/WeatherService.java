package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.WeatherDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final String defaultCity = "Copenhagen";

    public WeatherService(@Value("${weather.api.url}") String apiUrl,
                         @Value("${weather.api.key}") String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public WeatherDto getWeatherByCity(String city) {
        // Use default city if city is null or empty
        String cityToUse = (city == null || city.trim().isEmpty()) ? defaultCity : city.trim();
        
        // Convert Danish city names to English for API compatibility
        String originalCity = cityToUse;
        cityToUse = normalizeCityName(cityToUse);
        
        // Debug: Log the normalization (remove after testing)
        if (!originalCity.equals(cityToUse)) {
            System.out.println("DEBUG: Normalized city from '" + originalCity + "' to '" + cityToUse + "'");
        }

        // Check if API key is configured
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return createDefaultWeather(cityToUse);
        }

        try {
            // URL encode the city name to handle special characters
            String encodedCity = URLEncoder.encode(cityToUse, StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s&appid=%s&units=metric", 
                    apiUrl, encodedCity, apiKey);
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return mapToWeatherDto(response.getBody(), cityToUse);
            } else {
                return createDefaultWeather(defaultCity);
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            // Invalid API key - return default weather silently
            return createDefaultWeather(defaultCity);
        } catch (HttpClientErrorException.NotFound e) {
            // City not found - try with default city
            return createDefaultWeather(defaultCity);
        } catch (HttpClientErrorException e) {
            // Other HTTP errors - return default weather
            return createDefaultWeather(defaultCity);
        } catch (Exception e) {
            // On any other error, return default weather
            return createDefaultWeather(defaultCity);
        }
    }

    /**
     * Normalizes city names from Danish to English for API compatibility
     */
    private String normalizeCityName(String city) {
        if (city == null) {
            return defaultCity;
        }
        
        String normalized = city.trim();
        String lowerNormalized = normalized.toLowerCase();
        
        // Copenhagen/København - catch all variations including encoding issues
        // Check if it contains both "k" (or "K") and "benhavn" anywhere in the string
        if ((lowerNormalized.contains("k") || normalized.contains("K")) && 
            (lowerNormalized.contains("benhavn") || normalized.contains("benhavn"))) {
            return "Copenhagen";
        }
        
        // Exact matches for common variations
        if (lowerNormalized.equals("københavn") || 
            lowerNormalized.equals("kobenhavn") || 
            lowerNormalized.equals("kbh") || 
            lowerNormalized.equals("copenhagen")) {
            return "Copenhagen";
        }
        // Return as-is if no mapping found
        return normalized;
    }

    private WeatherDto mapToWeatherDto(Map<String, Object> response, String city) {
        WeatherDto dto = new WeatherDto();
        
        // Extract city and country
        Map<String, Object> sys = (Map<String, Object>) response.get("sys");
        if (sys != null) {
            dto.setCountry((String) sys.get("country"));
        }
        dto.setCity(city);

        // Extract main weather data
        Map<String, Object> main = (Map<String, Object>) response.get("main");
        if (main != null) {
            Object tempObj = main.get("temp");
            if (tempObj instanceof Number) {
                dto.setTemperature(((Number) tempObj).doubleValue());
            }
            Object humidityObj = main.get("humidity");
            if (humidityObj instanceof Number) {
                dto.setHumidity(((Number) humidityObj).doubleValue());
            }
        }

        // Extract weather description
        java.util.List<Map<String, Object>> weatherList = (java.util.List<Map<String, Object>>) response.get("weather");
        if (weatherList != null && !weatherList.isEmpty()) {
            Map<String, Object> weather = weatherList.get(0);
            dto.setDescription((String) weather.get("description"));
            dto.setCondition((String) weather.get("main"));
            dto.setIcon((String) weather.get("icon"));
        }

        // Extract wind speed
        Map<String, Object> wind = (Map<String, Object>) response.get("wind");
        if (wind != null) {
            Object speedObj = wind.get("speed");
            if (speedObj instanceof Number) {
                dto.setWindSpeed(((Number) speedObj).doubleValue());
            }
        }

        return dto;
    }

    private WeatherDto createDefaultWeather(String city) {
        WeatherDto dto = new WeatherDto();
        dto.setCity(city);
        dto.setCountry("DK");
        dto.setTemperature(15.0);
        dto.setDescription("Partly cloudy");
        dto.setCondition("Clouds");
        dto.setIcon("02d");
        dto.setHumidity(65.0);
        dto.setWindSpeed(10.0);
        return dto;
    }
}

