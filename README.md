# AI SQL Assistant

Ask questions about your PostgreSQL data in plain English. The app uses an AI model through OpenRouter to generate a SQL query, runs it against your database, and returns both the results and a natural-language summary. The React interface also displays database schema, keeps recent queries in the current browser session, and can chart numeric results.

## Features

- Natural-language questions translated into PostgreSQL queries
- Query execution and AI-generated summaries
- Database schema viewer
- Recent query history (up to 10 items in browser memory)
- Bar-chart visualization for suitable result sets
- Backend health-check endpoint

## Tech stack

- **Frontend:** React, Vite, Recharts
- **Backend:** Java 25, Spring Boot, Spring Web, Spring Data JPA/JDBC
- **Database:** PostgreSQL
- **AI provider:** OpenRouter (configured to use `google/gemini-3.5-flash-lite`)

## Prerequisites

- Java 25
- Node.js and npm (use a version supported by Vite 8)
- PostgreSQL
- An OpenRouter API key

## Setup

### 1. Create the database

Create a PostgreSQL database named `ai_sql_assistant`, then create the table currently used by the SQL-generation prompt:

```sql
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(100),
    salary INTEGER
);
```

Add your own employee records if you want to test queries against data.

### 2. Configure the backend

The backend reads database credentials and the OpenRouter key from environment variables. Set them in the shell that will run Spring Boot.

**PowerShell:**

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/ai_sql_assistant"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "your-postgres-password"
$env:OPENROUTER_API_KEY = "your-openrouter-api-key"
```

**macOS/Linux:**

```bash
export DB_URL="jdbc:postgresql://localhost:5432/ai_sql_assistant"
export DB_USERNAME="postgres"
export DB_PASSWORD="your-postgres-password"
export OPENROUTER_API_KEY="your-openrouter-api-key"
```

From the repository root, start the backend:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux, use `./mvnw spring-boot:run`.

The API starts at `http://localhost:8080`.

### 3. Start the frontend

In a second terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open the local URL printed by Vite (normally `http://localhost:5173`). The frontend is configured to call the backend at `http://localhost:8080`.

## API endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/health` | Check whether the backend is running |
| `GET` | `/api/database/schema` | Return columns and data types in the PostgreSQL `public` schema |
| `POST` | `/api/chat` | Generate and execute a query for a natural-language question |

Example request to `/api/chat`:

```json
{
  "question": "Which employees have the highest salaries?"
}
```

The response contains the original `question`, generated `sql`, query `result`, and natural-language `answer`.

## Development commands

Run backend tests from the repository root:

```powershell
.\mvnw.cmd test
```

Build and lint the frontend from the `frontend` directory:

```powershell
npm run build
npm run lint
```

## Notes

- The SQL-generation prompt currently describes the `employees` table and its `id`, `name`, `department`, and `salary` columns. Update the prompt in the backend if your schema differs.
- The backend currently allows only queries whose SQL starts with `SELECT`; do not expose this development application to untrusted users or production data without a security review and stronger database/query restrictions.
- The JPA schema mode is configured to update tables automatically during development.
- Never commit API keys, database passwords, or other credentials. Use environment variables or a local, ignored configuration file.
