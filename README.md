 <h1 align="center">BuildSim</h1>

<p align="justify">
  BuildSim is a Java console application that simulates a construction project one working day at a time. Users enter a project plan, including tasks, workers, machines, materials, and delivery schedules. The program then runs the project day by day while real-world problems such as delayed shipments, worker absences, bad weather, and machinery breakdowns interrupt the work.
</p>

<p align="justify">
  After every working day, the program updates the project's progress percentage, the number of workers present and their salaries, material usage and costs, and the projected completion date.
</p>

---

<h2 align="center">PROBLEM BEING ADDRESSED</h2>

<p align="justify">
  BuildSim aims to address the following problems commonly experienced in construction projects:
</p>

| Problem | Description |
|:---|:---|
| **Material Delivery Delays** | Late deliveries can prevent construction tasks from proceeding as planned. |
| **Worker Absences** | The absence of workers reduces the amount of work completed in a day and affects productivity. |
| **Unexpected Weather Conditions** | Bad weather can affect working days and slow down construction progress. |
| **Machinery Breakdowns** | Equipment failures can interrupt construction activities and reduce daily productivity. |

---

<h2 align="center">PROJECT OBJECTIVES</h2>

<h3>General Objective</h3>

<p align="justify">
  To develop a Java-based application that simulates a construction project by tracking daily progress, labor, material, and equipment costs, while automatically adjusting the projected completion date when problems occur.
</p>

<h3>Specific Objectives</h3>

1. To allow users to enter a project plan, including the timeframe, budget, tasks, workers, machines, materials, shipments, and task dependencies.
2. To simulate the project one working day at a time (Monday to Saturday) and record the percentage of work completed each day.
3. To track the number of workers present, their daily salaries, and the cost of every material used to identify waste and losses.
4. To simulate four problems: shipment delays, worker absences, bad weather, and machinery breakdowns. These can be generated randomly using a seed or manually added by the user.
5. To automatically recalculate the projected completion date and total cost after each day and show whether the project is on schedule or delayed.
6. To generate reports, including a dashboard, task progress, daily log, cost summary, material stock, and workforce summary.
7. To demonstrate classes and objects, methods, encapsulation, inheritance, polymorphism, abstraction, exception handling, and data handling in the program.

---

<h2 align="center">TARGET USERS</h2>

| Target Users | How They Will Use the System |
|:---|:---|
| **Project Managers and Constructors** | Enter a project plan, run the simulation, and see how delays, absences, weather, and breakdowns affect the finish date and budget before making decisions. |
| **Site Engineers and Planners** | Test schedules and "what-if" scenarios by adding problems on chosen dates and reviewing reports to find waste and bottlenecks. |
| **Students and Instructors** | Use the sample project (a two-storey house) to learn scheduling, resource management, cost tracking, and OOP concepts. |

---

<h2 align="center">MAIN FEATURES</h2>

| Feature | Description |
|:---|:---|
| **Project Setup** | Allows users to enter a project name, start and target end dates, budget (PHP), workers with daily wages, machines with daily rental rates, materials with unit costs, shipments with expected dates, and tasks with prerequisites. Setup is locked once the simulation starts. |
| **Day-by-Day Simulation** | Runs each working day, applies problems, processes deliveries, counts workers present, calculates salaries, charges machine rental, uses materials, and records daily progress. |
| **Problem Simulation** | Models four problems: shipment delays (1 to 5 days), worker absences, rain (which stops outdoor work), and machinery breakdowns (1 to 3 days). Problems can be generated randomly using a seed for repeatable results or manually added. |
| **Automatic Schedule Adjustment** | Adjusts the projected completion date when problems reduce daily productivity. The dashboard shows whether the project is on schedule or how many days it is delayed. |
| **Cost Tracking and Reports** | Tracks labor, material, and equipment costs against the budget. It also identifies unused materials as possible losses and generates project reports. |
| **Sample Project** | Provides a sample two-storey house project so users can try the application right away. |
| **Visual Charts** | Displays a progress-over-time chart and a Gantt chart to compare planned work, actual progress, and delays beyond the original schedule. |

---

<h2 align="center">TECHNOLOGIES USED</h2>

| Technology | Purpose |
|:---|:---|
| Java | Main programming language used to develop the application. |
| [Add technology] | [Add its purpose.] |

---

<h2 align="center">TEAM MEMBERS AND ROLES</h2>

| Position | Name | Assigned Role |
|:---|:---|:---|
| Leader | Steve Andrei Maldonado | (DITO ANG ROLES) |
| Member | Alessandra Ashanti Abdon | (DITO ANG ROLES) |
| Member | Kaixer Emmanuel Antonio Oscar Alvar | (DITO ANG ROLES) |
| Member | Marc Andrei Dita | (DITO ANG ROLES) |

---

<h2 align="center">OOP CONCEPTS DEMONSTRATED</h2>

| OOP Concept | How It Will Be Used |
|:---|:---|
| **Classes and Objects** | Real-world things are modeled as classes, such as Project, Task, Worker, Machine, Material, and Shipment. Each task, worker, or problem in a running simulation is an object. |
| **Methods** | Methods perform the program's work, for example `processDay()` in SimulationEngine, `calculateCost()` in Resource, `apply()` in Problem, and `getProgress()` in Task. |
| **Encapsulation** | Attributes such as task progress, material stock, and dates are private and can only be changed through validated getters, setters, or methods. |
| **Inheritance** | Worker, Machine, and Material extend the Resource class. WeatherProblem, WorkerAbsence, MachineryBreakdown, and ShipmentDelay extend the Problem class. |
| **Polymorphism** | The engine calls `apply()` on any Problem and `calculateCost()` on any Resource without knowing the exact type. Each subclass responds in its own way. |
| **Abstraction** | Resource and Problem are abstract classes that declare what every subclass must do (`calculateCost()` and `apply()`) while leaving the details to each subclass. |
| **Exception Handling** | Custom exceptions such as InsufficientStockException, CyclicDependencyException, and InvalidDateException, plus try-catch blocks for invalid user input. |
| **Arrays / Data Handling** | ArrayList and similar collections store tasks, workers, machines, materials, shipments, and daily logs. Arrays and loops are used for reports and calculations. |

---

<h2 align="center">INSTRUCTIONS FOR RUNNING THE PROGRAM</h2>

1. Make sure Java is installed on your computer.
2. Download or clone the BuildSim repository.
3. Open the project folder using your preferred Java IDE or code editor.
4. Locate the main Java file.
5. Compile and run the application.
6. Follow the instructions displayed in the console.

---

<h2 align="center">SAMPLE SCREENSHOTS</h2>

<p align="center">
  <i>Insert screenshots of the BuildSim application below.</i>
</p>

---

<h2 align="center">KNOWN LIMITATIONS</h2>

- (Add the known limitations of the application.)
- (Add features that are not yet fully implemented.)

---

<h2 align="center">FUTURE IMPROVEMENTS</h2>

- (Add planned improvements for future versions.)
- (Add possible additional features.)

---

<h2 align="center">PROJECT CONTRIBUTORS</h2>

<p align="center">
  <strong>Developed by:</strong><br>
  Steve Andrei Maldonado<br>
  Alessandra Ashanti Abdon<br>
  Kaixer Emmanuel Antonio Oscar Alvar<br>
  Marc Andrei Dita
</p>

<p align="center">
  <strong>BuildSim – Construction Project Simulation and Cost Tracking System</strong>
</p>
