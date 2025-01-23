package com.project.studentservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class GradeConfiguration {

    @Value("${grades.A_PLUS}")
    private Double aPlusPoints;

    @Value("${grades.A}")
    private Double aPoints;

    @Value("${grades.B_PLUS}")
    private Double bPlusPoints;

    @Value("${grades.B}")
    private Double bPoints;

    @Value("${grades.C_PLUS}")
    private Double cPlusPoints;

    @Value("${grades.C}")
    private Double cPoints;

    @Value("${grades.D}")
    private Double dPoints;

    @Value("${grades.F}")
    private Double fPoints;

    @Value("${grades.RC}")
    private Double rcPoints;

    @Value("${grades.RA}")
    private Double raPoints;

    // We'll use this later when calculating GPAs. It gets a bit messy with common courses in the mix
    // We basically have reach out to curriculum service to get the course type for each record if we get asked for the "Major GPA"
    public Map<String, Double> getGradePoints() {
        Map<String, Double> gradePointsMap = new HashMap<>();
        gradePointsMap.put("A_PLUS", aPlusPoints);
        gradePointsMap.put("A", aPoints);
        gradePointsMap.put("B_PLUS", bPlusPoints);
        gradePointsMap.put("B", bPoints);
        gradePointsMap.put("C_PLUS", cPlusPoints);
        gradePointsMap.put("C", cPoints);
        gradePointsMap.put("D", dPoints);
        gradePointsMap.put("F", fPoints);
        gradePointsMap.put("RC", rcPoints);
        gradePointsMap.put("RA", raPoints);
        return gradePointsMap;
    }
}