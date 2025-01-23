package com.project.studentservice.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Grade {
    A_PLUS("A+"), A("A"), B_PLUS("B+"), B("B"), C_PLUS("C+"), C("C"), D("D"), F("F"), RC("RC"), RA("RA");

    private final String value;

    Grade(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}