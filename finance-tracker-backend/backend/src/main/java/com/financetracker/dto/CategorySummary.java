package com.financetracker.dto;

// DTO = "Data Transfer Object" — a plain class whose only job is to shape
// the JSON we send back to the frontend / n8n. It's not stored in the DB;
// we build it on the fly from Expense + Budget data.
public class CategorySummary {

    private String category;
    private Double spent;
    private Double limit;       // null if no budget was set for this category
    private Double percentUsed; // null if no limit set (avoids divide-by-zero)
    private boolean overBudget;

    public CategorySummary(String category, Double spent, Double limit) {
        this.category = category;
        this.spent = spent;
        this.limit = limit;
        if (limit != null && limit > 0) {
            this.percentUsed = Math.round((spent / limit) * 1000.0) / 10.0; // one decimal place
            this.overBudget = spent > limit;
        } else {
            this.percentUsed = null;
            this.overBudget = false;
        }
    }

    public String getCategory() {
        return category;
    }

    public Double getSpent() {
        return spent;
    }

    public Double getLimit() {
        return limit;
    }

    public Double getPercentUsed() {
        return percentUsed;
    }

    public boolean isOverBudget() {
        return overBudget;
    }
}
