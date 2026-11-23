# BuildSim

## Project Description

BuildSim is a Java desktop application for keeping basic construction project information in one place. It tracks workers, machines, materials, shipments, absences, site problems, task progress, daily notes, schedule changes, and estimated costs. Project data is saved locally in CSV files.

## Problem Being Addressed

Small construction projects may track schedules, resources, deliveries, and costs in separate notes or spreadsheets. This makes it difficult to keep information up to date and understand how delays affect the project. BuildSim brings these records together in one application and updates its reports when project data changes.

## Project Objectives

- Organize construction project information in one application.
- Track workers, machines, materials, shipments, absences, problems, and tasks.
- Edit material, shipment, and task information, including task progress.
- Show adjusted task dates after recorded problems.
- Record optional daily notes about what happened or was accomplished.
- Summarize estimated material, labor, and machine costs against the project budget.
- Demonstrate the four fundamental object-oriented programming principles in Java.

## Target Users

- Students learning Java and object-oriented programming.
- Small construction project managers who need a simple local project tracker.

BuildSim is a course project, not a replacement for professional construction-management or accounting software.

## Main Features

| Feature | Description |
|---|---|
| Project management | Create and reopen projects with a name, dates, and budget. |
| Assistant | Convert a plain-language request into proposed data-entry commands. Commands are added only after the user reviews and confirms them. |
| Workers and machines | Record workers' daily wages and machines' daily rates. |
| Materials | Record material quantity, price, and stock; edit existing materials. |
| Shipments | Record a material, quantity, expected date, and Pending, Delayed, or Delivered status; edit existing shipments. |
| Absences and problems | Track worker absences and weather, machinery, or shipment-delay problems. |
| Tasks | Track start and end dates and percentage complete; edit existing tasks. |
| Daily Report | Review daily schedule information and save an optional note for each date. |
| Costs | Review estimated material, salary, and machine costs and compare them with the budget. |
| Gantt Chart | View planned task duration, added delay days, and progress in grayscale. |

## Technologies Used

- Java 17 or newer
- Java Swing for the desktop interface
- Java `HttpClient` for optional Gemini Assistant requests
- CSV files for local project storage
- Git and GitHub for source control and team collaboration

The program uses no third-party Java libraries to compile.

## Team Members and Roles

List the actual team members and the work each person is responsible for. Course guidelines specify a team of three to four members, subject to instructor direction.

| Team member | Role | Responsibilities |
|---|---|---|
| [Maldonado] | Project lead | [Actual responsibilities] |
| [Name] | Lead programmer | [Actual responsibilities] |
| [Name] | System/design lead | [Actual responsibilities] |
| [Name or remove this row] | Documentation and testing | [Actual responsibilities] |

## OOP Concepts Demonstrated

| Principle | Example in BuildSim |
|---|---|
| Encapsulation | Domain data is stored in private fields and accessed through constructors, getters, and controlled methods. Constructors validate values such as names, dates, quantities, and progress. |
| Inheritance | `Worker`, `Machine`, and `Material` extend `Resource`. `WeatherProblem`, `MachineryProblem`, and `ShipmentDelayProblem` extend `Problem`. Assistant command classes extend `Command`. |
| Abstraction | Abstract classes `Resource`, `Problem`, and `Command` define shared behavior. Interfaces `CsvRecord` and `Interpreter` define behavior contracts. |
| Polymorphism | Cost calculation calls `getCost` on different resource types. Saving calls `toCsv` on different `CsvRecord` objects. Problem and command subclasses provide their own behavior. |

## Instructions for Running the Program

### Requirements

- Install JDK 17 or newer.
- Internet access and a Gemini API key are needed only for the optional Assistant.

### Compile and run on Windows PowerShell

Open PowerShell in the `BuildSim_Simple` folder, which contains the `src` folder, then run:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\*.java
java -cp out BuildSim
```

Compile all Java source files together. Alternatively, open `src/BuildSim.java` in an IDE configured with JDK 17 or newer and run its `main` method.

### Optional Gemini Assistant setup

The application checks the `GEMINI_API_KEY` environment variable, `gemini-key.txt` in the run folder, or `src/gemini-key.txt`. The local key file is ignored by Git. Configure the key locally; never put it in Java source, commit it, or distribute it with the desktop app. A desktop application cannot keep an embedded shared key secret. Without a configured key, the Assistant is unavailable, but the other features continue to work.

Optional settings:

| Environment variable | Purpose | Default |
|---|---|---|
| `GEMINI_API_KEY` | Gemini API credential | None |
| `GEMINI_MODEL` | Gemini model name | `gemini-flash-lite-latest` |
| `GEMINI_BASE_URL` | Gemini API base URL | `https://generativelanguage.googleapis.com/v1beta` |

## Sample Screenshots

Add screenshots of the final running application to the `screenshots` folder. Replace these placeholders with the actual image files before submission. Do not include API keys or private project data.

![Project overview screenshot](screenshots/project-overview.png)

![Tasks and Gantt chart screenshot](screenshots/tasks-and-gantt.png)

![Daily report and costs screenshot](screenshots/daily-report-and-costs.png)

## Known Limitations

- Data is stored in local CSV files. There is no database, cloud synchronization, user login, or multi-user editing.
- The simple CSV format sanitizes commas and line breaks in saved text.
- Schedule and cost figures use simplified assumptions. Sundays are non-working days, absent workers are excluded from that day's salary, and machine rates are applied for each working day.
- The daily completion estimate is based on task dates, not measured construction quantities.
- The Assistant requires internet access and an owner-configured Gemini API key. Gemini can misunderstand a request, so users should review proposed commands before confirming them.
- The application is a desktop program and is not designed for mobile or web browsers.

## Future Improvements

- Use a database or a robust CSV library and add backup and export options.
- Add task dependencies, configurable workdays, weekends, and holidays to schedule calculations.
- Add material usage history, shipment history, search, and filtering.
- Add automated tests and improve the process for recording test evidence.
- Provide centrally managed AI access through a secure service if shared Assistant access is required.

## Project Contributors

List every person who made an actual contribution to the project. Include technical and documentation work, and provide GitHub evidence when available.

| Contributor | Contribution | Evidence (commit, pull request, or files) |
|---|---|---|
| [Name] | [Actual contribution] | [Commit, pull request, or files] |
| [Name] | [Actual contribution] | [Commit, pull request, or files] |
| [Name] | [Actual contribution] | [Commit, pull request, or files] |
| [Name or remove this row] | [Actual contribution] | [Commit, pull request, or files] |
