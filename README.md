# \# BuildSim

# 

# \*\*A Construction Project Simulator with Risk Forecasting\*\*

# 

# \## Description

# 

# BuildSim is a Java application that simulates a construction project one working day at a time. The user enters a project plan (tasks, workers, machines, materials, and material deliveries), and the program runs the project while real-world problems interrupt the work: delayed shipments, worker absences, bad weather, and machinery breakdowns.

# 

# After every day, BuildSim updates the progress percentage, the workers present and their salaries, the material usage and cost, and the projected finish date. When a problem happens, the schedule and the end date adjust automatically, so the manager can see right away how it affects the timeline and the budget.

# 

# \## Features

# 

# \### Project planning

# \- Set the timeframe of the whole construction (start date, target end date) and the budget

# \- Define tasks measured in worker-days, marked as outdoor or indoor

# \- Link tasks with prerequisites, so a task starts only when the tasks before it are finished

# \- Assign workers, a machine, and a material to each task

# 

# \### Real-world problems

# \- \*\*Delayed shipments:\*\* only the tasks that need the late material wait

# \- \*\*Worker absences:\*\* the worker does no work and is not paid that day

# \- \*\*Bad weather:\*\* outdoor tasks stop, while present workers are still paid

# \- \*\*Machinery breakdowns:\*\* tasks needing that machine stop for one or more days

# \- Problems can be entered manually or generated randomly with a seed, so results are repeatable

# 

# \### Daily tracking and automatic adjustment

# \- Schedule and finish date adjust automatically when a problem occurs

# \- Daily progress percentage for every task and for the whole project

# \- Number of workers present each day and their salaries

# \- Cost and stock of every material, with stock that can never go negative, so there are no hidden losses

# \- Equipment rental cost and a projected end date (on time or late)

# 

# \### Reports

# \- Dashboard with progress, projected end date, workers today, salary today, and total cost against budget

# \- Task progress, daily log, cost summary, material stock, and workforce summary

# 

# \### Two ways to use it

# \- Console app with a full text menu

# \- Swing GUI with a dashboard, task table with progress bars, daily log, and simulation controls

