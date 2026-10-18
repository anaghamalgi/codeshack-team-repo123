package com.smartcalc.calculator;

import jakarta.validation.constraints.NotBlank;

public record CalculationRequest(
        @NotBlank String operation,
        Double a,
        Double b,
        Double c,
        String angleUnit,
        String category,
        String fromUnit,
        String toUnit
) {}
