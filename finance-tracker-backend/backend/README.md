# Finance Tracker — Backend

Spring Boot REST API for the Personal Finance Tracker project.

## Requirements
- Java 17 or higher
- Maven (or use an IDE like IntelliJ which has it built in)

## How to run

```bash
cd backend
mvn spring-boot:run
```

First run will take a minute or two — Maven downloads all dependencies.
You'll know it worked when you see:

```
Finance Tracker backend is running on http://localhost:8080
```

## How to verify it's working

Open in a browser: http://localhost:8080/api/expenses
You should see an empty array: `[]`

## API Endpoints

| Method | Path                  | Description                              |
|--------|-----------------------|-------------------------------------------|
| GET    | /api/expenses         | List all expenses                        |
| GET    | /api/expenses/{id}    | Get one expense                          |
| POST   | /api/expenses         | Create an expense                        |
| POST   | /api/expenses/bulk    | Create many expenses at once (used by n8n)|
| PUT    | /api/expenses/{id}    | Update an expense                        |
| DELETE | /api/expenses/{id}    | Delete an expense                        |
| GET    | /api/budgets          | List all budgets                         |
| POST   | /api/budgets          | Create/update a category's monthly limit |
| DELETE | /api/budgets/{id}     | Delete a budget                          |
| GET    | /api/summary          | Monthly spend vs budget per category (used by n8n alert workflow) |

## Example requests (curl)

Create an expense:
```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d "{\"amount\":250,\"category\":\"Food\",\"date\":\"2026-06-15\",\"note\":\"lunch\"}"
```

Set a budget:
```bash
curl -X POST http://localhost:8080/api/budgets \
  -H "Content-Type: application/json" \
  -d "{\"category\":\"Food\",\"monthlyLimit\":5000}"
```

Get the monthly summary:
```bash
curl http://localhost:8080/api/summary
```

## Database

Uses H2, a file-based database — no install needed. Data is saved to
`backend/data/financetracker.mv.db` and persists across restarts.

To browse the data visually, go to http://localhost:8080/h2-console while
the app is running, and use this JDBC URL: `jdbc:h2:file:./data/financetracker`
(username `sa`, no password).

## Notes

- `spring.jpa.hibernate.ddl-auto=update` in `application.properties` means
  tables are auto-created from the Java model classes. Fine for development,
  not recommended for production.
- CORS is open (`@CrossOrigin(origins = "*")`) so the plain HTML/JS frontend
  can call this API from the browser without being blocked. Fine for a local
  learning project, but you'd lock this down before deploying anywhere public.
