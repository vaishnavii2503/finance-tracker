package com.financetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

// @Entity tells Spring/Hibernate: "this class maps to a database table."
// By default the table will be named "expense".
@Entity
public class Expense {

    // @Id marks this as the primary key. @GeneratedValue means the DB
    // auto-increments it for us (1, 2, 3, ...) — we never set it manually.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull
    private LocalDate date;

    // Optional free-text note, can be null
    private String note;

    // --- Constructors ---

    // Required by JPA: it needs a no-args constructor to build objects
    // when reading rows back from the database.
    public Expense() {
    }

    public Expense(Double amount, String category, LocalDate date, String note) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    // --- Getters and setters ---
    // Spring uses these to convert between Java objects and JSON automatically.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
