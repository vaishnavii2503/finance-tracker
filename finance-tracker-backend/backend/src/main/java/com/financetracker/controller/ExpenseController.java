package com.financetracker.controller;

import com.financetracker.model.Expense;
import com.financetracker.repository.ExpenseRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = every method's return value is automatically converted
// to JSON and written to the HTTP response body.
// @RequestMapping("/api/expenses") = every endpoint here starts with this path.
@RestController
@RequestMapping("/api/expenses")
// Allows the frontend (opened as a local HTML file, or served from a
// different origin) to call this API from the browser without being blocked.
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    // Constructor injection: Spring sees this constructor, notices it needs
    // an ExpenseRepository, and automatically supplies one. This is "Dependency Injection."
    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // GET /api/expenses -> list every expense
    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // GET /api/expenses/5 -> get one expense by id
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return expenseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/expenses -> create a new expense
    // @Valid triggers the validation annotations we put on the Expense class
    // (e.g. @NotBlank, @Positive). If validation fails, Spring auto-returns a 400.
    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        Expense saved = expenseRepository.save(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // POST /api/expenses/bulk -> create many expenses at once.
    // This is the endpoint the n8n CSV-import workflow will call.
    @PostMapping("/bulk")
    public ResponseEntity<List<Expense>> createExpensesBulk(@RequestBody List<Expense> expenses) {
        List<Expense> saved = expenseRepository.saveAll(expenses);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/expenses/5 -> update an existing expense
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense updated) {
        return expenseRepository.findById(id)
                .map(existing -> {
                    existing.setAmount(updated.getAmount());
                    existing.setCategory(updated.getCategory());
                    existing.setDate(updated.getDate());
                    existing.setNote(updated.getNote());
                    return ResponseEntity.ok(expenseRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/expenses/5 -> delete an expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        if (!expenseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        expenseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
