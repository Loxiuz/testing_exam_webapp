package com.testing_exam_webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeDto {
    private String datetime;
    private String timezone;
    private String abbreviation;
    private Integer dayOfWeek;
    private Integer dayOfYear;
}

