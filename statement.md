# Project Statement: Cinematic Archive & Review Engine

**Course:** Programming in Java  
**Target Platform:** Java Virtual Machine (JDK 17+)  
**Student Name:** Deepanshu  
**Registration Number:** 25BAI11163  
**Repository:** [https://github.com/DeepDevelops06/CinematicArchive](https://github.com/DeepDevelops06/CinematicArchive)  

---

## 1. Problem Statement
Managing and retrieving localized cinematic metadata and user reviews traditionally requires heavy web-based architectures and complex database server configurations. Setting up external database daemons (such as MySQL or PostgreSQL) introduces unnecessary configuration friction, operational overhead, and network latency for single-user and local terminal workflows. Furthermore, terminal-based applications often freeze during bulk ingestion or crash abruptly when users enter malformed numeric inputs.

There is a critical need for a lightweight, terminal-executable archiving solution that:
1. Securely persists relational data using an embedded zero-configuration database.
2. Handles malformed user inputs gracefully without breaking the interactive CLI loop.
3. Ingests bulk metadata asynchronously in the background using multi-threaded concurrency without blocking user operations.

---

## 2. Scope of the Project
The **Cinematic Archive & Review Engine** is a modular, production-ready Java CLI application engineered with a clean layered architecture (Model-Repository-Service-Util). The scope encompasses:
* Full lifecycle CRUD operations for film domain entities with category/genre filtering.
* Foreign-key relational linking of user critique reviews and fractional ratings (1.0 to 5.0) to specific film catalog entries.
* Direct persistence and query execution via embedded SQLite (`cinematic_archive.db`) through parameterized JDBC transactions.
* Asynchronous background ingestion utilizing Java `Thread` and `Runnable` to process bulk metadata without UI blocking.
* Robust exception isolation protecting against `InputMismatchException`, database connectivity issues, and invalid domain data.

---

## 3. Target Users
* **Local Archivists & Film Enthusiasts:** Users who need a fast, keyboard-driven terminal tool to catalog motion pictures, search by genre, and record personal reviews.
* **Data Entry Personnel & Curators:** Operators who require continuous, uninterrupted terminal interaction while background threads process bulk metadata imports.
* **Academic Reviewers & Java Developers:** Engineers seeking an exemplar implementation of clean architecture, JDBC persistence, concurrency, and JUnit 5 unit testing in core Java.

---

## 4. Functional Requirements (FR)

| Requirement ID | Module | Description |
| :--- | :--- | :--- |
| **FR1** | **Film Catalog Management (CRUD)** | Users can create new film records (Title, Director, Genre, Release Year), retrieve all records, query films by unique ID, and dynamically filter films by genre case-insensitively. |
| **FR2** | **Relational Review Tracking** | Users can submit numeric ratings (1.0–5.0) and text critiques relationally mapped to specific films via foreign keys (`film_id`). The system also dynamically calculates average ratings per film. |
| **FR3** | **Asynchronous Batch Ingestion** | Background worker threads (`BatchImportService`) ingest bulk metadata arrays asynchronously with simulated I/O latency, keeping the primary terminal interactive. |
| **FR4** | **Data Serialization & Export** | Ability to export current in-database records to structured flat files (`archive_data.txt`) and ingest existing records upon request. |

---

## 5. Non-Functional Requirements (NFR)

| Requirement ID | Category | Specification |
| :--- | :--- | :--- |
| **NFR1** | **Usability (CLI)** | The system provides an intuitive numeric menu loop (options 1–8) with clear input prompts and structured console feedback. |
| **NFR2** | **Reliability & Error Handling** | Scanner buffer clearing and custom domain exceptions (`InvalidDataException`, `ResourceNotFoundException`, `DatabaseOperationException`) prevent crashes and infinite loops on invalid input. |
| **NFR3** | **Security** | All SQL queries are executed strictly via `PreparedStatement` with parameterized placeholders (`?`) to prevent SQL injection vulnerabilities. Foreign key constraints are enforced via SQLite PRAGMA. |
| **NFR4** | **Resource Efficiency & Portability** | Operates as a self-contained embedded SQLite database (`cinematic_archive.db`), eliminating the need for standalone database servers or network services. |
| **NFR5** | **Maintainability & Testability** | Organized according to standard Maven layout with clean package separation (`model`, `repository`, `service`, `exception`, `util`) and automated JUnit 5 test coverage. |
