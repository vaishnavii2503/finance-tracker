package com.financetracker.controller;

import com.financetracker.model.Budget;
import com.financetracker.repository.BudgetRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetRepository budgetRepository;

    public BudgetController(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    // POST /api/budgets -> create or update a category's monthly limit.
    // If a budget for that category already exists, we update it instead of
    // creating a duplicate (keeps "one limit per category" true).
    @PostMapping
    public ResponseEntity<Budget> setBudget(@Valid @RequestBody Budget budget) {
        Budget toSave = budgetRepository.findByCategory(budget.getCategory())
                .map(existing -> {
                    existing.setMonthlyLimit(budget.getMonthlyLimit());
                    return existing;
                })
                .orElse(budget);
        Budget saved = budgetRepository.save(toSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        if (!budgetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        budgetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
