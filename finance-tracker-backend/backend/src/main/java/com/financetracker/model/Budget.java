package com.financetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// One row per category, e.g. ("Food", 5000.0) means
// "I want to spend at most 5000 per month on Food."
@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true means the DB will reject a second budget row for the
    // same category name — keeps things simple (one limit per category).
    @NotBlank
    @Column(unique = true)
    private String category;

    @NotNull
    @Positive
    private Double monthlyLimit;

    public Budget() {
    }

    public Budget(String category, Double monthlyLimit) {
        this.category = category;
        this.monthlyLimit = monthlyLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }
}
