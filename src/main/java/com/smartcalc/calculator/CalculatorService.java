package com.smartcalc.calculator;

import com.smartcalc.history.CalculationHistory;
import com.smartcalc.history.CalculationHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CalculatorService {
    private final CalculationHistoryRepository historyRepository;

    public CalculatorService(CalculationHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public CalculationResponse calculate(CalculationRequest request) {
        String op = request.operation().trim().toUpperCase(Locale.ROOT);
        double a = required(request.a(), "a");
        double b = request.b() == null ? 0.0 : request.b();
        String expression;
        String result;

        switch (op) {
            case "ADD" -> { expression = a + " + " + b; result = number(a + b); }
            case "SUBTRACT" -> { expression = a + " - " + b; result = number(a - b); }
            case "MULTIPLY" -> { expression = a + " * " + b; result = number(a * b); }
            case "DIVIDE" -> {
                if (b == 0.0) throw new IllegalArgumentException("Cannot divide by zero.");
                expression = a + " / " + b; result = number(a / b);
            }
            case "MODULUS" -> {
                if (b == 0.0) throw new IllegalArgumentException("Modulus by zero is undefined.");
                expression = a + " % " + b; result = number(a % b);
            }
            case "POWER" -> { expression = a + " ^ " + b; result = number(Math.pow(a, b)); }
            case "DEG_TO_RAD" -> { expression = a + " degrees to radians"; result = number(Math.toRadians(a)); }
            case "RAD_TO_DEG" -> { expression = a + " radians to degrees"; result = number(Math.toDegrees(a)); }
            case "SIN", "COS", "TAN" -> {
                double radians = toRadians(a, request.angleUnit());
                double value = switch (op) {
                    case "SIN" -> Math.sin(radians);
                    case "COS" -> Math.cos(radians);
                    default -> {
                        if (Math.abs(Math.cos(radians)) < 1e-12)
                            throw new IllegalArgumentException("Tangent is undefined for this angle.");
                        yield Math.tan(radians);
                    }
                };
                expression = op.toLowerCase(Locale.ROOT) + "(" + a + " " + unitLabel(request.angleUnit()) + ")";
                result = number(value);
            }
            case "ASIN", "ACOS", "ATAN" -> {
                if ((op.equals("ASIN") || op.equals("ACOS")) && (a < -1 || a > 1))
                    throw new IllegalArgumentException(op.toLowerCase() + " input must be between -1 and 1.");
                double radians = switch (op) {
                    case "ASIN" -> Math.asin(a);
                    case "ACOS" -> Math.acos(a);
                    default -> Math.atan(a);
                };
                String outUnit = request.angleUnit() == null ? "DEG" : request.angleUnit().toUpperCase(Locale.ROOT);
                double value = outUnit.equals("RAD") ? radians : Math.toDegrees(radians);
                expression = op.toLowerCase(Locale.ROOT) + "(" + a + ")";
                result = number(value) + (outUnit.equals("RAD") ? " rad" : "°");
            }
            case "LN" -> {
                if (a <= 0) throw new IllegalArgumentException("Natural logarithm requires a positive input.");
                expression = "ln(" + a + ")"; result = number(Math.log(a));
            }
            case "LOG10" -> {
                if (a <= 0) throw new IllegalArgumentException("log10 requires a positive input.");
                expression = "log10(" + a + ")"; result = number(Math.log10(a));
            }
            case "LOG_BASE" -> {
                if (a <= 0) throw new IllegalArgumentException("Logarithm argument must be positive.");
                if (b <= 0 || b == 1) throw new IllegalArgumentException("Logarithm base must be positive and not equal to 1.");
                expression = "log base " + b + " of " + a;
                result = number(Math.log(a) / Math.log(b));
            }
            case "QUADRATIC" -> {
                double coefficientB = required(request.b(), "b");
                double coefficientC = required(request.c(), "c");
                if (a == 0) throw new IllegalArgumentException("Coefficient a must not be zero for a quadratic equation.");
                double discriminant = coefficientB * coefficientB - 4 * a * coefficientC;
                String roots;
                if (discriminant > 0) {
                    double r1 = (-coefficientB + Math.sqrt(discriminant)) / (2 * a);
                    double r2 = (-coefficientB - Math.sqrt(discriminant)) / (2 * a);
                    roots = "x1 = " + number(r1) + ", x2 = " + number(r2);
                } else if (Math.abs(discriminant) < 1e-12) {
                    roots = "x = " + number(-coefficientB / (2 * a));
                } else {
                    double real = -coefficientB / (2 * a);
                    double imaginary = Math.sqrt(-discriminant) / Math.abs(2 * a);
                    roots = "x1 = " + number(real) + " + " + number(imaginary) + "i, x2 = "
                            + number(real) + " - " + number(imaginary) + "i";
                }
                expression = number(a) + "x^2 + " + number(coefficientB) + "x + " + number(coefficientC) + " = 0";
                result = "Discriminant = " + number(discriminant) + "; " + roots;
            }
            case "CONVERT" -> {
                if (request.category() == null || request.fromUnit() == null || request.toUnit() == null)
                    throw new IllegalArgumentException("For conversion, provide category, fromUnit and toUnit.");
                double converted = UnitConverter.convert(a, request.category(), request.fromUnit(), request.toUnit());
                expression = number(a) + " " + request.fromUnit() + " to " + request.toUnit();
                result = number(converted) + " " + request.toUnit();
            }
            default -> throw new IllegalArgumentException("Unsupported operation: " + op);
        }

        if (result.equals("NaN") || result.equals("Infinity") || result.equals("-Infinity"))
            throw new IllegalArgumentException("The calculation produced a non-finite result. Check the input values.");

        CalculationHistory saved = historyRepository.save(new CalculationHistory(op, expression, result));
        return new CalculationResponse(saved.getId(), saved.getOperation(), saved.getExpression(),
                saved.getResult(), saved.getCreatedAt());
    }

    private static double required(Double value, String name) {
        if (value == null) throw new IllegalArgumentException("Input '" + name + "' is required for this operation.");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Input '" + name + "' must be a finite number.");
        return value;
    }

    private static double toRadians(double angle, String unit) {
        return unit != null && unit.equalsIgnoreCase("RAD") ? angle : Math.toRadians(angle);
    }

    private static String unitLabel(String unit) {
        return unit != null && unit.equalsIgnoreCase("RAD") ? "rad" : "deg";
    }

    private static String number(double value) {
        if (Math.abs(value) < 1e-12) value = 0;
        return String.format(Locale.US, "%.10g", value);
    }
}
