// Base URL of the backend. Since the backend runs on port 8080 and this
// frontend is just opened as a local HTML file (or served separately),
// every request needs the full address.
const API_BASE = "http://localhost:8080/api";

// ---------- Helpers ----------

function formatMoney(value) {
  // Show money with 2 decimal places, e.g. 1234.5 -> "1234.50"
  return Number(value).toFixed(2);
}

function todayISO() {
  // Returns "YYYY-MM-DD" for today, used to default the date field
  // and to show the current month in the header.
  return new Date().toISOString().slice(0, 10);
}

function showMonthLabel() {
  const now = new Date();
  const label = now.toLocaleString("default", { month: "long", year: "numeric" });
  document.getElementById("monthLabel").textContent = label;
}

// ---------- API calls ----------
// Each function wraps one backend endpoint. If the backend isn't running,
// fetch() throws — every caller below catches that and shows a clear message
// instead of failing silently.

async function fetchExpenses() {
  const res = await fetch(`${API_BASE}/expenses`);
  if (!res.ok) throw new Error("Failed to load expenses");
  return res.json();
}

async function createExpense(expense) {
  const res = await fetch(`${API_BASE}/expenses`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(expense),
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || "Failed to add expense. Check that all fields are valid.");
  }
  return res.json();
}

async function deleteExpense(id) {
  const res = await fetch(`${API_BASE}/expenses/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error("Failed to delete expense");
}

async function createBudget(budget) {
  const res = await fetch(`${API_BASE}/budgets`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(budget),
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || "Failed to save budget. Check that all fields are valid.");
  }
  return res.json();
}

async function fetchSummary() {
  const res = await fetch(`${API_BASE}/summary`);
  if (!res.ok) throw new Error("Failed to load summary");
  return res.json();
}

// ---------- Rendering ----------

function renderExpenseTable(expenses) {
  const tbody = document.getElementById("expenseTableBody");
  const emptyState = document.getElementById("expenseEmptyState");
  tbody.innerHTML = "";

  if (expenses.length === 0) {
    emptyState.style.display = "block";
    return;
  }
  emptyState.style.display = "none";

  // Most recent first
  const sorted = [...expenses].sort((a, b) => b.date.localeCompare(a.date));

  for (const expense of sorted) {
    const row = document.createElement("tr");

    const dateCell = document.createElement("td");
    dateCell.textContent = expense.date;

    const categoryCell = document.createElement("td");
    categoryCell.textContent = expense.category;

    const noteCell = document.createElement("td");
    noteCell.textContent = expense.note || "—";

    const amountCell = document.createElement("td");
    amountCell.className = "num";
    amountCell.textContent = formatMoney(expense.amount);

    const actionCell = document.createElement("td");
    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete";
    deleteBtn.className = "delete-btn";
    deleteBtn.addEventListener("click", () => handleDelete(expense.id));
    actionCell.appendChild(deleteBtn);

    row.append(dateCell, categoryCell, noteCell, amountCell, actionCell);
    tbody.appendChild(row);
  }
}

function renderSummary(summary) {
  const container = document.getElementById("summaryList");
  container.innerHTML = "";

  if (summary.length === 0) {
    container.innerHTML = '<p class="empty-state">No expenses or budgets yet. Add one to see your summary here.</p>';
    return;
  }

  // Categories with a budget first, sorted by how close to/over the limit they are
  const sorted = [...summary].sort((a, b) => {
    if (a.limit == null && b.limit == null) return 0;
    if (a.limit == null) return 1;
    if (b.limit == null) return -1;
    return (b.percentUsed || 0) - (a.percentUsed || 0);
  });

  for (const row of sorted) {
    const wrapper = document.createElement("div");
    wrapper.className = "summary-row";

    const top = document.createElement("div");
    top.className = "summary-row-top";

    const categoryLabel = document.createElement("span");
    categoryLabel.className = "summary-category";
    categoryLabel.textContent = row.category;

    const amounts = document.createElement("span");
    amounts.className = "summary-amounts";
    if (row.limit != null) {
      amounts.innerHTML = `<strong>₹${formatMoney(row.spent)}</strong> of ₹${formatMoney(row.limit)}`;
    } else {
      amounts.innerHTML = `<strong>₹${formatMoney(row.spent)}</strong> spent (no budget set)`;
    }

    top.append(categoryLabel, amounts);
    wrapper.appendChild(top);

    if (row.limit != null) {
      const track = document.createElement("div");
      track.className = "gauge-track";
      const fill = document.createElement("div");
      fill.className = "gauge-fill" + (row.overBudget ? " over" : "");
      fill.style.width = `${Math.min(row.percentUsed, 100)}%`;
      track.appendChild(fill);
      wrapper.appendChild(track);

      if (row.overBudget) {
        const flag = document.createElement("span");
        flag.className = "over-flag";
        flag.textContent = `Over budget by ₹${formatMoney(row.spent - row.limit)}`;
        wrapper.appendChild(flag);
      }
    }

    container.appendChild(wrapper);
  }
}

// ---------- Load + refresh ----------

async function refreshAll() {
  try {
    const [expenses, summary] = await Promise.all([fetchExpenses(), fetchSummary()]);
    renderExpenseTable(expenses);
    renderSummary(summary);
  } catch (err) {
    // Most likely cause: the Spring Boot backend isn't running.
    document.getElementById("summaryList").innerHTML =
      `<p class="empty-state">Couldn't reach the backend. Make sure it's running at ${API_BASE.replace("/api", "")}.</p>`;
    console.error(err);
  }
}

// ---------- Event handlers ----------

async function handleDelete(id) {
  try {
    await deleteExpense(id);
    await refreshAll();
  } catch (err) {
    alert(err.message);
  }
}

document.getElementById("expenseForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errorEl = document.getElementById("expenseError");
  errorEl.textContent = "";

  const expense = {
    amount: parseFloat(document.getElementById("expenseAmount").value),
    category: document.getElementById("expenseCategory").value.trim(),
    date: document.getElementById("expenseDate").value,
    note: document.getElementById("expenseNote").value.trim() || null,
  };

  try {
    await createExpense(expense);
    e.target.reset();
    document.getElementById("expenseDate").value = todayISO();
    await refreshAll();
  } catch (err) {
    errorEl.textContent = err.message;
  }
});

document.getElementById("budgetForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errorEl = document.getElementById("budgetError");
  errorEl.textContent = "";

  const budget = {
    category: document.getElementById("budgetCategory").value.trim(),
    monthlyLimit: parseFloat(document.getElementById("budgetLimit").value),
  };

  try {
    await createBudget(budget);
    e.target.reset();
    await refreshAll();
  } catch (err) {
    errorEl.textContent = err.message;
  }
});

// ---------- Init ----------

document.getElementById("expenseDate").value = todayISO();
showMonthLabel();
refreshAll();
