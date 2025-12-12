package com.testing_exam_webapp.controller;

import com.testing_exam_webapp.dto.TimeDto;
import com.testing_exam_webapp.service.TimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/time")
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TimeDto> getCurrentTime(
            @RequestParam(required = false) String timezone) {
        TimeDto time = timezone != null 
                ? timeService.getCurrentTime(timezone)
                : timeService.getCurrentTime();
        return new ResponseEntity<>(time, HttpStatus.OK);
    }
}

