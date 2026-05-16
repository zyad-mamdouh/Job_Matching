# 🎯 Job Matching System

A Java 17 Maven application that matches job applicants to job openings using a JavaFX GUI, socket-based client/server communication, and a pluggable matching strategy.

---

## 📋 Table of Contents

- [Overview](#overview)
- [System Architecture](#system-architecture)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Build & Run](#build--run)
- [How to Use](#how-to-use)
- [CV File Format](#cv-file-format)
- [Matching Strategies](#matching-strategies)
- [Network Protocol](#network-protocol)
- [Package Reference](#package-reference)

---

## Overview

The system takes a plain-text CV file, parses it into an `Applicant` object, sends it to a server via a socket, calculates a match score against a selected job, and displays the result in the GUI.

**Full runtime flow:**

```
User selects TXT file
        ↓
  ResumeParser  →  Applicant object
        ↓
  network.Client  →  Socket  →  Server
                                    ↓
                              ClientHandler
                                    ↓
                           MatchingStrategy
                                    ↓
                             Score + Result
        ↓
  JavaFX TextArea (result displayed)
```

---

## System Architecture

```
┌─────────────────────────────────────────────┐
│  ui  ──  JavaFX GUI, FileChooser, events    │
└──────────────────────┬──────────────────────┘
                       ↓
┌─────────────────────────────────────────────┐
│  service  ──  ResumeParser, MatchingEngine  │
└──────────────────────┬──────────────────────┘
                       ↓
┌─────────────────────────────────────────────┐
│  model  ──  Applicant, Job, Skill           │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  network  ──  Client, Server, ClientHandler │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│  strategy  ──  Basic / Advanced matching    │
└─────────────────────────────────────────────┘
```

---

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Java JDK | 17+ | Compile and run the application |
| Apache Maven | 3.8+ | Build tool, dependency management |

> ⚠️ JavaFX is pulled automatically by Maven. You do **not** need to install it separately.

**Verify your setup:**

```bash
java -version    # should show 17 or higher
mvn -version     # should show Maven 3.x
```

---

## Project Structure

```
job-matching/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── model/
    │   │   │   ├── Applicant.java
    │   │   │   ├── Job.java
    │   │   │   ├── Skill.java
    │   │   │   └── Application.java
    │   │   ├── service/
    │   │   │   ├── ResumeParser.java
    │   │   │   └── MatchingEngine.java
    │   │   ├── strategy/
    │   │   │   ├── MatchingStrategy.java
    │   │   │   ├── BasicMatchingStrategy.java
    │   │   │   └── AdvancedMatchingStrategy.java
    │   │   ├── network/
    │   │   │   ├── Client.java
    │   │   │   ├── Server.java
    │   │   │   └── ClientHandler.java
    │   │   ├── ui/
    │   │   │   └── CvSelectionApp.java
    │   │   └── storage/
    │   │       ├── DataStore.java
    │   │       └── FileDataStore.java
    │   └── resources/
    │       └── resume-example.txt
    └── test/
        └── java/
```

---

## Build & Run

### Step 1 — Compile the project

```bash
mvn compile
```

### Step 2 — Start the Server

Open a **new terminal** and run:

```bash
mvn exec:java -Dexec.mainClass=network.Server
```

You should see the server start on port `5000`.  
**Keep this terminal open** — the server must be running before you launch the client.

### Step 3 — Start the JavaFX Client

Open a **second terminal** and run:

```bash
mvn javafx:run
```

The GUI window will open.

### Optional — Package as JAR

```bash
mvn package
```

Output: `target/job-matching-1.0.0.jar`

---

## How to Use

Once both the server and client are running:

1. **Enter a job number** in the "Job Number" field (e.g., `1`, `2`, `3`).
2. **Select a strategy** from the dropdown — `basic` or `advanced`.
3. **Click "Choose CV"** — a file dialog will open.
4. **Select a `.txt` resume file** in the correct format (see below).
5. The output area will display the available jobs and the match result.

**Result interpretation:**

| Score | Status |
|-------|--------|
| ≥ 80  | ✅ HIGHLY RECOMMENDED |
| ≥ 50  | 🟡 POSSIBLE MATCH |
| < 50  | ❌ NOT RECOMMENDED |

---

## CV File Format

The resume must be a plain `.txt` file using this exact format:

```
Name: Sara Ahmed
YearsOfExperience: 3
EducationLevel: 2
ExpectedSalary: 8500

Skills:
Java:3
SQL:2
Python:2
```

### Field Reference

| Field | Type | Description |
|-------|------|-------------|
| `Name` | String | Full applicant name |
| `YearsOfExperience` | Integer | Total years of professional experience |
| `EducationLevel` | Integer (1–3) | 1 = High School, 2 = Bachelor, 3 = Master/PhD |
| `ExpectedSalary` | Decimal | Monthly or annual salary expectation |
| `Skills` | Section header | All lines below are parsed as `SkillName:Level` |

### Skill Format

Each skill line must follow `SkillName:Level` where level is an integer (e.g., `1`–`5`):

```
Java:3
SQL:2
Python:2
```

### Rules

- Lines starting with `#` are treated as comments and skipped.
- Blank lines are ignored.
- All four main fields (`Name`, `YearsOfExperience`, `EducationLevel`, `ExpectedSalary`) are **required**.
- `EducationLevel` must be `1`, `2`, or `3`.
- The `Skills:` section header must appear after the four main fields.

---

## Matching Strategies

### Basic Strategy

Compares **skill names only** (case-insensitive). Ignores experience, education, and salary.

```
Score = (matched skills / required skills) × 100
```

**Example:**

```
Required: Java, SQL, Python
Applicant: Java, SQL
Score: 2/3 × 100 = 66.67
```

### Advanced Strategy

Calculates a **weighted score** across four criteria:

| Criterion | Weight |
|-----------|--------|
| Skills match | 50% |
| Experience | 25% |
| Education level | 15% |
| Salary fit | 10% |

```
Final Score = (skills × 0.50) + (experience × 0.25) + (education × 0.15) + (salary × 0.10)
```

**Salary scoring:** If expected salary ≤ offered salary → 100. Otherwise score decreases proportionally.

---

## Network Protocol

The client and server communicate over TCP sockets on **port 5000** using a line-based text protocol:

```
Client                          Server
──────                          ──────
GET_JOBS               →
                       ←        1. Software Engineer ...
                       ←        2. Data Analyst ...
                       ←        END_JOBS

{job_number}           →
{name,years,edu,salary}→
{Skill1:level,...}     →
{basic|advanced}       →
                       ←        Result text
```

**Applicant line format:**
```
Sara Ahmed,3,2,8500.0
```

**Skills line format:**
```
Java:3,SQL:2,Python:2
```

If the applicant has no skills:
```
none
```

---

## Package Reference

| Package | Key Classes | Responsibility |
|---------|-------------|----------------|
| `model` | `Applicant`, `Job`, `Skill`, `Application` | Business domain objects |
| `service` | `ResumeParser`, `MatchingEngine` | File parsing, matching orchestration |
| `strategy` | `BasicMatchingStrategy`, `AdvancedMatchingStrategy` | Score calculation algorithms |
| `network` | `Client`, `Server`, `ClientHandler` | Socket communication |
| `ui` | `CvSelectionApp` | JavaFX GUI |
| `storage` | `DataStore`, `FileDataStore` | Future persistence layer (placeholder) |

---

## Troubleshooting

**"Cannot connect. Is the server running?"**
→ Make sure you started the server first with `mvn exec:java -Dexec.mainClass=network.Server` and it is still running.

**"Job number must be a valid number."**
→ The job number field must contain a plain integer (e.g., `1`).

**"Invalid line" or "Unknown field" error**
→ Your CV file has a formatting issue. Check that all field names are spelled exactly as shown in the CV format section above.

**JavaFX window does not open**
→ Make sure your JDK version is 17 or higher. Run `java -version` to verify.

---

## Tech Stack

- **Java 17**
- **Apache Maven 3.8+**
- **JavaFX 17** (via `org.openjfx:javafx-controls`)
- **TCP Sockets** (standard Java `java.net`)
- **Strategy Design Pattern**