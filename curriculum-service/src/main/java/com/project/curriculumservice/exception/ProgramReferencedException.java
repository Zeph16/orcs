package com.project.curriculumservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProgramReferencedException extends RuntimeException {
    public ProgramReferencedException(String message) {
        super(message);
    }
}
