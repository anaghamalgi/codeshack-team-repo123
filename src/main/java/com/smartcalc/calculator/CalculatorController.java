package com.smartcalc.calculator;

import com.smartcalc.history.CalculationHistory;
import com.smartcalc.history.CalculationHistoryRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class CalculatorController {
    private final CalculatorService calculatorService;
    private final CalculationHistoryRepository historyRepository;

    public CalculatorController(CalculatorService calculatorService,
                                CalculationHistoryRepository historyRepository) {
        this.calculatorService = calculatorService;
        this.historyRepository = historyRepository;
    }

    @PostMapping("/calculations")
    public CalculationResponse calculate(@Valid @RequestBody CalculationRequest request) {
        return calculatorService.calculate(request);
    }

    @GetMapping("/history")
    public List<CalculationHistory> getHistory() {
        return historyRepository.findAllByOrderByCreatedAtDesc();
    }

    @DeleteMapping("/history/{id}")
    public void deleteHistoryItem(@PathVariable Long id) {
        if (!historyRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "History item not found.");
        historyRepository.deleteById(id);
    }

    @DeleteMapping("/history")
    public void clearHistory() {
        historyRepository.deleteAllInBatch();
    }
}
