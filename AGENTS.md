# Project Patterns & Guidelines

## 1. Directory Naming Convention
- **snake_case** for all folder names (e.g., `my_folder`).

## 2. Framework
- **Spring Boot** (Version 4.0.3)
- **Java 21**

## 3. Tools & Dependencies
- **Spring Boot Starter WebMVC** (for the web and API layer)
- **PostgreSQL** (Database persistence, specifically PostgreSQL 16 run locally via Docker Compose)
- **Lombok** (Boilerplate code reduction via annotations)
- **Maven** (Build tool and dependency management)
- **Spring Boot DevTools** (Rapid application development)

## 4. API Features
Based on the `INFO.md` specs, this API handles the core functionalities for **DevRoast**:
- **Code Submission:** Accepts code snippets from users and returns instant quality ratings.
- **Roast Mode:** An optional toggle parameter providing heavily sarcastic and entertaining code analysis.
- **Detailed Analysis:** Detects what is wrong (and right) in the code and provides categorized feedback based on severity levels (critical, warning, good).
- **Fix Suggestions:** Returns structured data (e.g., visual diffs) showing how the code can be improved using best practices.
- **Shame Leaderboard:** Maintains and retrieves a ranking of the lowest-scored submissions ("worst code on the internet").

## 5. Overall Explanation
**DevRoast** is a backend application designed to provide humorous but genuinely constructive code reviews. The system gathers code snippets submitted by users and acts as a specialized linting/review engine. By allowing users to toggle a "Roast Mode," the service makes code analysis engaging and entertaining through maximum sarcasm. At the same time, it provides serious insights by grouping issues into severity categories and offering fix suggestions. To gamify the learning experience, it ranks the worst-written code on a public "Shame Leaderboard," making it a uniquely fun and educational platform for developers.
