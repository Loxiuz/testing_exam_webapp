package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.TimeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimeService Tests")
class TimeServiceTest {

    @InjectMocks
    private TimeService timeService;

    @Test
    @DisplayName("getCurrentTime - No timezone - Returns time with default timezone")
    void getCurrentTime_NoTimezone_ReturnsDefaultTime() {
        TimeDto result = timeService.getCurrentTime();
        
        assertNotNull(result);
        assertNotNull(result.getDatetime());
        assertEquals("Europe/Copenhagen", result.getTimezone());
    }

    @Test
    @DisplayName("getCurrentTime - Valid timezone - Returns time for timezone")
    void getCurrentTime_ValidTimezone_ReturnsTimeForTimezone() {
        TimeDto result = timeService.getCurrentTime("America/New_York");
        
        assertNotNull(result);
        assertNotNull(result.getDatetime());
    }

    @Test
    @DisplayName("getCurrentTime - Null timezone - Returns default time")
    void getCurrentTime_NullTimezone_ReturnsDefaultTime() {
        TimeDto result = timeService.getCurrentTime(null);
        
        assertNotNull(result);
        assertEquals("Europe/Copenhagen", result.getTimezone());
    }

    @Test
    @DisplayName("getCurrentTime - Empty timezone - Returns default time")
    void getCurrentTime_EmptyTimezone_ReturnsDefaultTime() {
        TimeDto result = timeService.getCurrentTime("");
        
        assertNotNull(result);
        assertEquals("Europe/Copenhagen", result.getTimezone());
    }
}

