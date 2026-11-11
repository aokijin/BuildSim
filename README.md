<h1 align="center">BuildSim</h1>
<p align="justify">
  BuildSim is a Java console application that simulates a construction project one working day at a time. The user enters the project plan: the tasks, the workers, machines, and materials, and the dates when materials are delivered. The program then runs the project day by day while real-world problems (delayed shipments, worker absences, bad weather, and machinery breakdowns) interrupt the work.
  <br><br>
  After every day, the program updates the progress percentage, the number of workers present and their salaries, the material usage and cost, and the projected finish date.
</p>
<h2 align="left">PROBLEM BEING ADDRESSED:</h2>

<p>BuildSim aims to address the following problems:</p>

<ul>
  <li><strong>Material Delivery Delays</strong> – Late deliveries can prevent construction tasks from proceeding as planned.</li>
  <li><strong>Worker Absences</strong> – The absence of workers reduces the amount of work completed in a day and affects productivity.</li> 
  <li><strong>Unexpected Weather Conditions</strong> – Bad weather can affect working days and slow down construction progress.</li>
  <li><strong>Machinery Breakdowns</strong> – Equipment failures can interrupt construction activities and reduce daily productivity.</li>
</ul>
<h2 align="center">PROJECT OBJECTIVES</h2>

## General Objectives

To develop a Java-based application that will simulate a construction project that tracks daily progress, labor, material, and equipment costs, and automatically adjusts the projected completion date when problems occur.

## Specific Objectives

1. To allow users to enter a project plan, including the timeframe, budget, tasks, workers, machines, materials, shipments, and task dependencies.

2. To simulate the project one working day at a time (Monday to Saturday) and record the percentage of work finished each day.

3. To track the number of workers present, their daily salaries, and the cost of every material used, waste and losses are easy to spot.

4. To simulate four problems (shipment delay, worker absence, bad weather, and machinery breakdown), either randomly using a seed or manually added by the user.

5. To automatically recalculate the projected end date and total cost after each day, and show whether the project is on time or late.

6. To generate reports, including a dashboard, task progress, daily log, cost summary, material stock, and workforce summary.

7. To demonstrate classes and objects, methods, encapsulation, inheritance, polymorphism, abstraction, exception handling, and data handling in the program.

<h2 align="center">TARGET USERS</h2>

| Target Users | How they will use the system |
|---|---|
| **Project Managers and Constructors** | Enter a project plan, run the simulation, and see how delays, absences, weather, and breakdowns affect the finish date and budget before making decisions. |
| **Site Engineers and Planners** | Test schedules and "what-if" scenarios by adding problems on chosen dates, and review reports to find waste and bottlenecks. |
| **Students and Instructors** | Use the sample project (a two-storey house) to learn scheduling, resource management, and cost tracking, and to study how the OOP concepts are applied. |

<h2 align="center">MAIN FEATURES</h2>

| Feature | Description |
|---|---|
| **Project Setup** | The user enters a project name, start and target end dates, budget (PHP), workers with daily wages, machines with daily rental rates, materials with unit costs, shipments with expected dates, and tasks with prerequisites. Setup is locked once the simulation starts. |
| **Day-by-Day Simulation** | The engine runs each working day: it applies problems, delivers shipments, counts workers present, pays them, charges machine rental, uses materials, and records the work done. |
| **Problem Simulation** | Four problems are modeled: shipment delay (1 to 5 days), worker absence, rain (stops outdoor work), and machinery breakdown (1 to 3 days). They can be random, using a seed for repeatable results, or manually added. |
| **Automatic Schedule Adjustment** | Problems reduce the work done each day, which pushes back the projected end date. The dashboard shows whether the project is on time or how many days late. |
| **Cost Tracking and Reports** | The system totals labor, material, and equipment costs against the budget, shows unused stocks as a possible loss, and provides a dashboard, task progress, daily log, cost summary, material stock, and workforce reports. |
| **Sample Project** | One click loads a two-storey house project so the program can be tried right away. |
| **Visual Charts** | A progress-over-time chart and a Gantt chart show planned work, work done, and delays beyond the plan. |

<h2 align="center">TECHNOLOGIES USED</h2>

<h2 align="center">TEAM MEMBERS AND ROLES</h2>

<ul>
  <li><strong>Leader:</strong> Steve Andrei Maldonado — (DITO ANG ROLES)</li>
  <li><strong>Members:</strong>
    <ul>
      <li>Alessandra Ashanti Abdon —(DITO ANG ROLES) </li>
      <li>Kaixer Emmanuel Antonio Oscar Alvar — (DITO ANG ROLES)</li>
      <li>Marc Andrei Dita — (DITO ANG ROLES)</li>
    </ul>
  </li>
</ul>

<h2 align="center">OOP CONCEPTS DEMONSTRATED</h2>

<h2 align="center">INSTRUCTIONS FOR RUNNING THE PROGRAM</h2>

<h2 align="center">SAMPLE SCREENSHOTS</h2>

<h2 align="center">KNOWN LIMITATIONS</h2>

<h2 align="center">FUTURE IMPROVEMENTS</h2>

<h2 align="center">PROJECT CONTRIBUTORS</h2>
