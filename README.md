BuildSim
BuildSim is a Java console application that simulates a construction project one working day at a time. The user enters the project plan: the tasks, the workers, machines, and materials, and the dates when materials are delivered. The program then runs the project day by day while real-world problems (delayed shipments, worker absences, bad weather, and machinery breakdowns) interrupt the work.

After every day, the program updates the progress percentage, the number of workers present and their salaries, the material usage and cost, and the projected finish date.

PROBLEM BEING ADDRESSED:
BuildSim aims to address the following problems:

Material Delivery Delays – Late deliveries can prevent construction tasks from proceeding as planned.
Worker Absences – The absence of workers reduces the amount of work completed in a day and affects productivity.
Unexpected Weather Conditions – Bad weather can affect working days and slow down construction progress.
Machinery Breakdowns – Equipment failures can interrupt construction activities and reduce daily productivity.
PROJECT OBJECTIVES
General Objectives
To develop a Java-based application that will simulate a construction project that tracks daily progress, labor, material, and equipment costs, and automatically adjusts the projected completion date when problems occur.

Specific Objectives
To allow users to enter a project plan, including the timeframe, budget, tasks, workers, machines, materials, shipments, and task dependencies.

To simulate the project one working day at a time (Monday to Saturday) and record the percentage of work finished each day.

To track the number of workers present, their daily salaries, and the cost of every material used, waste and losses are easy to spot.

To simulate four problems (shipment delay, worker absence, bad weather, and machinery breakdown), either randomly using a seed or manually added by the user.

To automatically recalculate the projected end date and total cost after each day, and show whether the project is on time or late.

To generate reports, including a dashboard, task progress, daily log, cost summary, material stock, and workforce summary.

To demonstrate classes and objects, methods, encapsulation, inheritance, polymorphism, abstraction, exception handling, and data handling in the program.

TARGET USERS
Target Users	How they will use the system
Project Managers and Constructors	Enter a project plan, run the simulation, and see how delays, absences, weather, and breakdowns affect the finish date and budget before making decisions.
Site Engineers and Planners	Test schedules and "what-if" scenarios by adding problems on chosen dates, and review reports to find waste and bottlenecks.
Students and Instructors	Use the sample project (a two-storey house) to learn scheduling, resource management, and cost tracking, and to study how the OOP concepts are applied.
MAIN FEATURES
Feature	Description
Project Setup	The user enters a project name, start and target end dates, budget (PHP), workers with daily wages, machines with daily rental rates, materials with unit costs, shipments with expected dates, and tasks with prerequisites. Setup is locked once the simulation starts.
Day-by-Day Simulation	The engine runs each working day: it applies problems, delivers shipments, counts workers present, pays them, charges machine rental, uses materials, and records the work done.
Problem Simulation	Four problems are modeled: shipment delay (1 to 5 days), worker absence, rain (stops outdoor work), and machinery breakdown (1 to 3 days). They can be random, using a seed for repeatable results, or manually added.
Automatic Schedule Adjustment	Problems reduce the work done each day, which pushes back the projected end date. The dashboard shows whether the project is on time or how many days late.
Cost Tracking and Reports	The system totals labor, material, and equipment costs against the budget, shows unused stocks as a possible loss, and provides a dashboard, task progress, daily log, cost summary, material stock, and workforce reports.
Sample Project	One click loads a two-storey house project so the program can be tried right away.
Visual Charts	A progress-over-time chart and a Gantt chart show planned work, work done, and delays beyond the plan.
TECHNOLOGIES USED
TEAM MEMBERS AND ROLES
Leader: Steve Andrei Maldonado — (DITO ANG ROLES)
Members:
Alessandra Ashanti Abdon —(DITO ANG ROLES)
Kaixer Emmanuel Antonio Oscar Alvar — (DITO ANG ROLES)
Marc Andrei Dita — (DITO ANG ROLES)
OOP CONCEPTS DEMONSTRATED
OOP Concept	How It Will Be Used
Classes and Objects	Real-world things are modeled as classes, such as Project, Task, Worker, Machine, Material, and Shipment. Each task, worker, or problem in a running simulation is an object.
Methods	Methods perform the program’s work, for example processDay() in SimulationEngine, calculateCost() in Resource, apply() in Problem, and getProgress() in Task.
Encapsulation	Attributes such as task progress, material stock, and dates are private and can only be changed through validated getters, setters, or methods.
Inheritance	Worker, Machine, and Material extend the Resource class. WeatherProblem, WorkerAbsence, MachineryBreakdown, and ShipmentDelay extend the Problem class.
Polymorphism	The engine calls apply() on any Problem and calculateCost() on any Resource without knowing the exact type. Each subclass responds in its own way.
Abstraction	Resource and Problem are abstract classes that declare what every subclass must do (calculateCost() and apply()) while leaving the details to each subclass.
Exception Handling	Custom exceptions such as InsufficientStockException, CyclicDependencyException, and InvalidDateException, plus try-catch blocks for invalid user input.
Arrays / Data Handling	ArrayList and similar collections store tasks, workers, machines, materials, shipments, and daily logs. Arrays and loops are used for reports and calculations.
INSTRUCTIONS FOR RUNNING THE PROGRAM
SAMPLE SCREENSHOTS
KNOWN LIMITATIONS
FUTURE IMPROVEMENTS
PROJECT CONTRIBUTORS
