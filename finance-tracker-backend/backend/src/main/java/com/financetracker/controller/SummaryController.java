package com.financetracker.controller;

import com.financetracker.dto.CategorySummary;
import com.financetracker.model.Budget;
import com.financetracker.model.Expense;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.ExpenseRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin(origins = "*")
public class SummaryController {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public SummaryController(ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    // GET /api/summary
    // Returns, for every category that has EITHER a budget OR expenses this
    // month: how much was spent, what the limit is, and whether it's over.
    //
    // This is the endpoint n8n's daily workflow calls. n8n then loops over
    // the array and sends an alert for any entry where overBudget == true.
    @GetMapping
    public List<CategorySummary> getMonthlySummary() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        List<Expense> monthExpenses = expenseRepository.findByDateBetween(start, end);

        // Group this month's expenses by category and sum the amounts.
        // e.g. {"Food": 3200.0, "Travel": 1500.0}
        Map<String, Double> spentByCategory = monthExpenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

        List<Budget> budgets = budgetRepository.findAll();
        Map<String, Double> limitByCategory = budgets.stream()
                .collect(Collectors.toMap(Budget::getCategory, Budget::getMonthlyLimit));

        // Union of category names from both maps, so a category with a budget
        // but zero spending this month still shows up (spent = 0).
        return java.util.stream.Stream.concat(spentByCategory.keySet().stream(), limitByCategory.keySet().stream())
                .distinct()
                .map(category -> new CategorySummary(
                        category,
                        spentByCategory.getOrDefault(category, 0.0),
                        limitByCategory.get(category) // may be null - CategorySummary handles that
                ))
                .collect(Collectors.toList());
    }
}
