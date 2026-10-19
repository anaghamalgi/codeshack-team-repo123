package com.smartcalc.calculator;

import java.time.LocalDateTime;

public record CalculationResponse(
        Long id,
        String operation,
        String expression,
        String result,
        LocalDateTime createdAt
) {}
