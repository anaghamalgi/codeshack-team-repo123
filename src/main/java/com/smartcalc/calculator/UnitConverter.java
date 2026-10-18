package com.smartcalc.calculator;

import java.util.Locale;
import java.util.Map;

final class UnitConverter {
    private UnitConverter() {}

    static double convert(double value, String category, String from, String to) {
        String cat = category.toUpperCase(Locale.ROOT);
        String f = from.toLowerCase(Locale.ROOT);
        String t = to.toLowerCase(Locale.ROOT);

        if (f.equals(t)) return value;

        return switch (cat) {
            case "LENGTH" -> value * factor(f, t, Map.of(
                    "mm", 0.001, "cm", 0.01, "m", 1.0, "km", 1000.0,
                    "in", 0.0254, "inch", 0.0254, "ft", 0.3048, "foot", 0.3048,
                    "yd", 0.9144, "mi", 1609.344));
            case "MASS" -> value * factor(f, t, Map.of(
                    "mg", 0.000001, "g", 0.001, "kg", 1.0, "tonne", 1000.0,
                    "lb", 0.45359237, "oz", 0.028349523125));
            case "TIME" -> value * factor(f, t, Map.of(
                    "ms", 0.001, "s", 1.0, "sec", 1.0, "min", 60.0,
                    "h", 3600.0, "hr", 3600.0, "day", 86400.0));
            case "SPEED" -> value * factor(f, t, Map.of(
                    "m/s", 1.0, "km/h", 1.0 / 3.6, "mph", 0.44704, "ft/s", 0.3048));
            case "AREA" -> value * factor(f, t, Map.of(
                    "m2", 1.0, "m²", 1.0, "cm2", 0.0001, "cm²", 0.0001,
                    "km2", 1_000_000.0, "km²", 1_000_000.0, "ft2", 0.09290304, "ft²", 0.09290304,
                    "acre", 4046.8564224, "ha", 10000.0));
            case "VOLUME" -> value * factor(f, t, Map.of(
                    "ml", 0.001, "l", 1.0, "liter", 1.0, "litre", 1.0,
                    "m3", 1000.0, "m³", 1000.0, "cm3", 0.001, "cm³", 0.001,
                    "us gal", 3.785411784));
            case "TEMPERATURE" -> convertTemperature(value, f, t);
            default -> throw new IllegalArgumentException("Unsupported conversion category: " + category);
        };
    }

    private static double factor(String from, String to, Map<String, Double> factors) {
        Double f = factors.get(from);
        Double t = factors.get(to);
        if (f == null || t == null)
            throw new IllegalArgumentException("Unsupported units. Check the unit names for this category.");
        // Factors represent each unit's size relative to the category's base unit.
        return f / t;
    }

    private static double convertTemperature(double value, String from, String to) {
        double celsius = switch (from) {
            case "c", "°c", "celsius" -> value;
            case "f", "°f", "fahrenheit" -> (value - 32) * 5 / 9;
            case "k", "kelvin" -> value - 273.15;
            default -> throw new IllegalArgumentException("Use C, F or K for temperature units.");
        };
        return switch (to) {
            case "c", "°c", "celsius" -> celsius;
            case "f", "°f", "fahrenheit" -> celsius * 9 / 5 + 32;
            case "k", "kelvin" -> celsius + 273.15;
            default -> throw new IllegalArgumentException("Use C, F or K for temperature units.");
        };
    }
}
