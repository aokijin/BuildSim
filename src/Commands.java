import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

// ---------------------------------------------------------------
// The commands of the Assistant. A command is one thing the assistant understood
// and can do, for example "add the worker Ana" or "add an absence".
// ---------------------------------------------------------------

// The names that exist (or will exist) in the project. They are used to check the names in the commands.
class Names {
    ArrayList<String> workers = new ArrayList<>();
    ArrayList<String> machines = new ArrayList<>();
    ArrayList<String> materials = new ArrayList<>();

    Names(Project project) {
        for (Worker w : project.getWorkers()) {
            workers.add(w.getName());
        }
        for (Machine m : project.getMachines()) {
            machines.add(m.getName());
        }
        for (Material m : project.getMaterials()) {
            materials.add(m.getName());
        }
    }

    // Finds the name in the list (capital letters do not matter) and gives it back as it is written in the list
    static String find(ArrayList<String> list, String typed, String what) {
        for (String name : list) {
            if (name.equalsIgnoreCase(typed)) {
                return name;
            }
        }
        throw new InvalidInputException("There is no " + what + " called '" + typed + "' yet. Add it first.");
    }

    // The name must not be in the list yet
    static void requireNew(ArrayList<String> list, String typed, String what) {
        for (String name : list) {
            if (name.equalsIgnoreCase(typed)) {
                throw new InvalidInputException("The " + what + " '" + typed + "' is already in the list.");
            }
        }
    }
}

// The parent of all the commands. Each kind of command does its own work (this is polymorphism):
// describe() says what it will do, and run() does it.
abstract class Command {

    // A sentence for the user, for example: Add worker Ana (PHP 600.00 per day)
    public abstract String describe();

    // Adds the item to the project. It throws InvalidInputException if something is wrong.
    public abstract void run(Project project);

    private static double number(String text) {
        return Double.parseDouble(text.replace(",", ""));
    }

    private static int wholeNumber(String text, String what) {
        double value = number(text);
        if (value != Math.floor(value)) {
            throw new InvalidInputException(what + " must be a whole number.");
        }
        return (int) value;
    }

    private static String status(String text) {
        String t = text.toLowerCase();
        if (t.startsWith("pend")) {
            return "Pending";
        } else if (t.startsWith("deliver")) {
            return "Delivered";
        } else if (t.startsWith("delay") || t.equals("late")) {
            return "Delayed";
        }
        throw new InvalidInputException("The status must be Pending, Delivered or Delayed (not '" + text + "').");
    }

    private static String problemType(String text) {
        String t = text.toLowerCase();
        if (t.startsWith("weather")) {
            return "Weather";
        } else if (t.startsWith("machin")) {
            return "Machinery";
        } else if (t.startsWith("shipment")) {
            return "Shipment delay";
        }
        throw new InvalidInputException("Unknown kind of problem: " + text);
    }

    // Makes the right command from one line of the AI's answer, for example ADD_WORKER|Ana|600
    public static Command fromLine(String line, Names names) {
        String[] p = line.split("\\|", -1);
        for (int i = 0; i < p.length; i++) {
            p[i] = p[i].trim();
        }
        String kind = p[0].toUpperCase();

        try {
            if (kind.equals("ADD_WORKER")) {
                Names.requireNew(names.workers, p[1], "worker");
                Command command = new AddWorkerCommand(p[1], number(p[2]));
                names.workers.add(p[1]);
                return command;
            } else if (kind.equals("ADD_MACHINE")) {
                Command command = new AddMachineCommand(p[1], number(p[2]));
                names.machines.add(p[1]);
                return command;
            } else if (kind.equals("ADD_MATERIAL")) {
                Names.requireNew(names.materials, p[1], "material");
                Command command = new AddMaterialCommand(p[1], number(p[2]), number(p[3]), number(p[4]));
                names.materials.add(p[1]);
                return command;
            } else if (kind.equals("ADD_SHIPMENT")) {
                String material = Names.find(names.materials, p[1], "material");
                return new AddShipmentCommand(material, number(p[2]), LocalDate.parse(p[3]), status(p[4]));
            } else if (kind.equals("ADD_ABSENCE")) {
                String worker = Names.find(names.workers, p[1], "worker");
                return new AddAbsenceCommand(worker, LocalDate.parse(p[2]));
            } else if (kind.equals("ADD_PROBLEM")) {
                String type = problemType(p[1]);
                String detail = "-";
                if (type.equals("Machinery")) {
                    detail = Names.find(names.machines, p[3], "machine");
                } else if (type.equals("Shipment delay")) {
                    detail = Names.find(names.materials, p[3], "material");
                }
                return new AddProblemCommand(type, LocalDate.parse(p[2]), detail, wholeNumber(p[4], "Days lost"));
            } else if (kind.equals("ADD_TASK")) {
                return new AddTaskCommand(p[1], LocalDate.parse(p[2]), LocalDate.parse(p[3]), wholeNumber(p[4], "Progress"));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidInputException("This line is not complete: " + line);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("A number is wrong in this line: " + line);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("A date is wrong in this line: " + line);
        }
        throw new InvalidInputException("I did not understand this line: " + line);
    }
}

// Each command makes the object in its constructor (the class checks the values),
// and adds it to the project when run() is called.

class AddWorkerCommand extends Command {
    private Worker worker;

    public AddWorkerCommand(String name, double wage) {
        worker = new Worker(name, wage);
    }

    public String describe() {
        return "Add worker " + worker.getName() + " (PHP " + Main.money(worker.getDailyWage()) + " per day)";
    }

    public void run(Project project) {
        for (Worker w : project.getWorkers()) {
            if (w.getName().equalsIgnoreCase(worker.getName())) {
                throw new InvalidInputException("The worker " + worker.getName() + " is already in the list.");
            }
        }
        project.getWorkers().add(worker);
    }
}

class AddMachineCommand extends Command {
    private Machine machine;

    public AddMachineCommand(String name, double rate) {
        machine = new Machine(name, rate);
    }

    public String describe() {
        return "Add machine " + machine.getName() + " (PHP " + Main.money(machine.getRatePerDay()) + " per day)";
    }

    public void run(Project project) {
        project.getMachines().add(machine);
    }
}

class AddMaterialCommand extends Command {
    private Material material;

    public AddMaterialCommand(String name, double quantity, double price, double stock) {
        material = new Material(name, quantity, price, stock);
    }

    public String describe() {
        return "Add material " + material.getName() + " (" + Main.number(material.getQuantity()) + " in total, PHP "
                + Main.money(material.getPriceEach()) + " each, " + Main.number(material.getInStock()) + " in stock)";
    }

    public void run(Project project) {
        for (Material m : project.getMaterials()) {
            if (m.getName().equalsIgnoreCase(material.getName())) {
                throw new InvalidInputException("The material " + material.getName() + " is already in the list.");
            }
        }
        project.getMaterials().add(material);
    }
}

class AddShipmentCommand extends Command {
    private Shipment shipment;

    public AddShipmentCommand(String materialName, double quantity, LocalDate date, String status) {
        shipment = new Shipment(materialName, quantity, date, status);
    }

    public String describe() {
        return "Add shipment of " + shipment.getMaterialName() + ": " + Main.number(shipment.getQuantity())
                + ", expected " + shipment.getExpectedDate() + ", " + shipment.getStatus();
    }

    public void run(Project project) {
        project.getShipments().add(shipment);
    }
}

class AddAbsenceCommand extends Command {
    private Absence absence;

    public AddAbsenceCommand(String workerName, LocalDate date) {
        absence = new Absence(workerName, date);
    }

    public String describe() {
        return "Add absence: " + absence.getWorkerName() + " on " + absence.getDate();
    }

    public void run(Project project) {
        project.getAbsences().add(absence);
    }
}

class AddProblemCommand extends Command {
    private Problem problem;

    public AddProblemCommand(String type, LocalDate date, String detail, int daysLost) {
        problem = Problem.create(type, date, detail, daysLost);
    }

    public String describe() {
        return "Add problem: " + problem.getDescription() + " on " + problem.getDate();
    }

    public void run(Project project) {
        project.getProblems().add(problem);
    }
}

class AddTaskCommand extends Command {
    private Task task;

    public AddTaskCommand(String name, LocalDate start, LocalDate end, int progress) {
        task = new Task(name, start, end, progress);
    }

    public String describe() {
        return "Add task " + task.getName() + ", " + task.getStartDate() + " to " + task.getEndDate()
                + ", " + task.getProgress() + "% done";
    }

    public void run(Project project) {
        project.getTasks().add(task);
    }
}
