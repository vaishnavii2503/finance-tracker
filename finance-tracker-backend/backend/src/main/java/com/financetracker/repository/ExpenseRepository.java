package com.financetracker.repository;

import com.financetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

// Extending JpaRepository<Expense, Long> gives us, for free, methods like:
// save(), findAll(), findById(), deleteById() — no implementation needed,
// Spring generates it at runtime.
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Spring Data JPA reads this method NAME and builds the SQL query
    // automatically: "find all expenses where date is between start and end."
    // No SQL or implementation required — just declare the method signature.
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);

    List<Expense> findByCategoryAndDateBetween(String category, LocalDate start, LocalDate end);
}
