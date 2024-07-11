package com.ncc.employee_management.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ObjectNotValidException extends RuntimeException {

    private final Set<String> violations;

    private final String violationSource;
}
