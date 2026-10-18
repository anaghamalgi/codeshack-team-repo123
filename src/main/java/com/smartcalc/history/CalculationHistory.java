package com.smartcalc.history;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_history")
public class CalculationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String operation;

    @Column(nullable = false, length = 500)
    private String expression;

    @Column(nullable = false, length = 1000)
    private String result;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected CalculationHistory() {}

    public CalculationHistory(String operation, String expression, String result) {
        this.operation = operation;
        this.expression = expression;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getOperation() { return operation; }
    public String getExpression() { return expression; }
    public String getResult() { return result; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
