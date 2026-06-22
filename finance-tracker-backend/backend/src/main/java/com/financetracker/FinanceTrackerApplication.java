package com.financetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This annotation tells Spring Boot: "scan this package, find all my controllers,
// repositories, etc., wire them together, and start an embedded web server."
@SpringBootApplication
public class FinanceTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceTrackerApplication.class, args);
        System.out.println("Finance Tracker backend is running on http://localhost:8080");
    }
}
