package com.project.studentservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GradeConfiguration {

    @Value("${grades.A_PLUS}") private Double aPlusPoints;
    @Value("${grades.A}") private Double aPoints;
    @Value("${grades.B_PLUS}") private Double bPlusPoints;
    @Value("${grades.B}") private Double bPoints;
    @Value("${grades.C_PLUS}") private Double cPlusPoints;
    @Value("${grades.C}") private Double cPoints;
    @Value("${grades.D}") private Double dPoints;
    @Value("${grades.F}") private Double fPoints;
    @Value("${grades.RC}") private Double rcPoints;
    @Value("${grades.RA}") private Double raPoints;

    private final Map<String, Double> gradePointsMap = new HashMap<>();

    @PostConstruct
    private void init() {
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
    }

    public Map<String, Double> getGradePoints() {
        return gradePointsMap;
    }
}
