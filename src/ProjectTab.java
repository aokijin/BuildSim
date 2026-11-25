import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Project tab: make a new project, or open one made before.
 * (The class Project at the bottom of this file keeps all the data and saves it to CSV.)
 */
public class ProjectTab extends JPanel {

    private Main app;

    private JTextField projectNameField = new JTextField(20);
    private JTextField startDateField = Main.dateField(20);
    private JTextField budgetField = new JTextField(20);
    private JTextField endDateField = Main.dateField(20);
    private JButton createButton = new JButton("Create project");

    private DefaultTableModel savedModel = Main.newModel("Project name", "Start date", "Budget (PHP)", "Target end date");
    private JTable savedTable = new JTable(savedModel);
    private ArrayList<File> savedFiles = new ArrayList<>();
    private JButton openButton = new JButton("Open selected project");
    private JLabel currentLabel = new JLabel("No project is open yet.");

    public ProjectTab(Main app) {
        this.app = app;

        // top: the form for a new project
        JLabel title = new JLabel("New project");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JPanel form = Main.makeForm("Project name", projectNameField,
                "Start date (yyyy-mm-dd)", startDateField,
                "Budget (PHP)", budgetField,
                "Target end date (yyyy-mm-dd)", endDateField);
        JPanel createRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createRow.add(createButton);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);
        top.add(createRow, BorderLayout.SOUTH);

        // middle: the projects made before
        JLabel savedTitle = new JLabel("Projects made before");
        savedTitle.setFont(savedTitle.getFont().deriveFont(Font.BOLD, 16f));
        Main.styleTable(savedTable);
        savedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel openRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        openRow.add(openButton);

        JPanel saved = new JPanel(new BorderLayout(5, 5));
        saved.add(savedTitle, BorderLayout.NORTH);
        saved.add(new JScrollPane(savedTable), BorderLayout.CENTER);
        saved.add(openRow, BorderLayout.SOUTH);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(top, BorderLayout.NORTH);
        add(saved, BorderLayout.CENTER);
        add(currentLabel, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createProject());
        openButton.addActionListener(e -> openSelectedProject());
        refreshSavedProjects();
    }

    private void createProject() {
        String name = projectNameField.getText().trim();
        if (name.isEmpty()) {
            Main.message("Please enter the project name.");
            return;
        }
        LocalDate start = Main.readDate(startDateField, "Start date");
        if (start == null) {
            return;
        }
        Double budget = Main.readNumber(budgetField, "Budget");
        if (budget == null) {
            return;
        }
        LocalDate end = Main.readDate(endDateField, "Target end date");
        if (end == null) {
            return;
        }
        if (end.isBefore(start)) {
            Main.message("The target end date cannot be before the start date.");
            return;
        }
        if (Project.fileFor(name).exists()) {
            Main.message("A project with this name already exists. Open it from the list below.");
            return;
        }

        Project project;
        try {
            project = new Project(name, start, budget, end);
        } catch (InvalidInputException e) {
            Main.message(e.getMessage());
            return;
        }
        try {
            project.save();
        } catch (IOException e) {
            Main.message("Could not save the project: " + e.getMessage());
            return;
        }
        projectNameField.setText("");
        startDateField.setText("");
        budgetField.setText("");
        endDateField.setText("");
        showProject(project);
    }

    private void openSelectedProject() {
        int row = Main.selectedRow(savedTable, "project");
        if (row < 0) {
            return;
        }
        try {
            showProject(Project.load(savedFiles.get(row)));
        } catch (IOException e) {
            Main.message("Could not open the project: " + e.getMessage());
        }
    }

    // Tells the window to use this project (it turns on the other tabs)
    private void showProject(Project project) {
        currentLabel.setText("Open project: " + project.getName() + "   |   " + project.getStartDate()
                + " to " + project.getEndDate() + "   |   Budget: PHP " + Main.money(project.getBudget()));
        app.setProject(project);
        refreshSavedProjects();
    }

    // Fills the "Projects made before" table from the CSV files
    private void refreshSavedProjects() {
        savedModel.setRowCount(0);
        savedFiles.clear();
        for (File file : Project.savedFiles()) {
            try {
                Project p = Project.load(file);
                savedModel.addRow(new Object[]{p.getName(), p.getStartDate(), Main.money(p.getBudget()), p.getEndDate()});
                savedFiles.add(file);
            } catch (IOException e) {
                // a file that is not a Main file is skipped
            }
        }
    }
}

// ---------------------------------------------------------------
// Abstraction: the interface and the abstract class that the data classes share
// ---------------------------------------------------------------

// Anything that can be saved as one line of the CSV file
interface CsvRecord {
    String toCsv();
}

// Everything the project pays for: a worker, a machine or a material.
// It has a name, and each kind calculates its own cost (the method getCost is abstract).
abstract class Resource implements CsvRecord {
    private String name;

    public Resource(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("The name cannot be empty.");
        }
        this.name = name;
    }

    public String getName() { return name; }

    // The cost of this resource for some working days. Worker, Machine and Material each do it differently.
    public abstract double getCost(int days);

    // The kind of resource: Worker, Machine or Material
    public abstract String getKind();
}

// ---------------------------------------------------------------
// The project: its details + all the lists. It can save and load
// itself to a CSV file in the "projects" folder.
// ---------------------------------------------------------------

class Project {
    private String name;
    private LocalDate startDate;
    private double budget;
    private LocalDate endDate;

    private ArrayList<Worker> workers = new ArrayList<>();
    private ArrayList<Machine> machines = new ArrayList<>();
    private ArrayList<Material> materials = new ArrayList<>();
    private ArrayList<Shipment> shipments = new ArrayList<>();
    private ArrayList<Absence> absences = new ArrayList<>();
    private ArrayList<Problem> problems = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<DailyNote> dailyNotes = new ArrayList<>();

    public Project(String name, LocalDate startDate, double budget, LocalDate endDate) {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("The project name cannot be empty.");
        }
        if (budget < 0) {
            throw new InvalidInputException("The budget cannot be negative.");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidInputException("The target end date cannot be before the start date.");
        }
        this.name = name;
        this.startDate = startDate;
        this.budget = budget;
        this.endDate = endDate;
    }

    public String getName() { return name; }
    public LocalDate getStartDate() { return startDate; }
    public double getBudget() { return budget; }
    public LocalDate getEndDate() { return endDate; }

    public ArrayList<Worker> getWorkers() { return workers; }
    public ArrayList<Machine> getMachines() { return machines; }
    public ArrayList<Material> getMaterials() { return materials; }
    public ArrayList<Shipment> getShipments() { return shipments; }
    public ArrayList<Absence> getAbsences() { return absences; }
    public ArrayList<Problem> getProblems() { return problems; }
    public ArrayList<Task> getTasks() { return tasks; }
    public ArrayList<DailyNote> getDailyNotes() { return dailyNotes; }

    // Daily wage of a worker (0 if the worker is not found)
    public double getWorkerWage(String workerName) {
        for (Worker w : workers) {
            if (w.getName().equals(workerName)) {
                return w.getDailyWage();
            }
        }
        return 0;
    }

    // Every absence takes away one daily wage
    public double getTotalDeduction() {
        double total = 0;
        for (Absence a : absences) {
            total = total + getWorkerWage(a.getWorkerName());
        }
        return total;
    }

    // Removes the problems of one kind about one machine or material
    public void removeProblems(String type, String detail) {
        for (int i = problems.size() - 1; i >= 0; i--) {
            if (problems.get(i).getType().equals(type) && problems.get(i).getDetail().equals(detail)) {
                problems.remove(i);
            }
        }
    }

    // ----------------------- CSV saving -----------------------

    // The file of a project: projects/<name>.csv
    public static File fileFor(String projectName) {
        String safeName = projectName.replaceAll("[^a-zA-Z0-9 _-]", "_");
        return new File("projects", safeName + ".csv");
    }

    // Commas would break the CSV, so we change them to spaces
    static String clean(String text) {
        return text.replace(",", " ").replace("\n", " ");
    }

    public void save() throws IOException {
        new File("projects").mkdirs();

        // every kind of data is a CsvRecord, so one loop saves them all
        // (each object writes its own line: this is polymorphism)
        ArrayList<CsvRecord> records = new ArrayList<>();
        records.addAll(workers);
        records.addAll(machines);
        records.addAll(materials);
        records.addAll(shipments);
        records.addAll(absences);
        records.addAll(problems);
        records.addAll(tasks);
        records.addAll(dailyNotes);

        try (PrintWriter out = new PrintWriter(new FileWriter(fileFor(name), StandardCharsets.UTF_8))) {
            out.println("PROJECT," + clean(name) + "," + startDate + "," + budget + "," + endDate);
            for (CsvRecord record : records) {
                out.println(record.toCsv());
            }
        }
    }

    // ----------------------- CSV loading -----------------------

    public static Project load(File file) throws IOException {
        Project project = null;
        try (BufferedReader in = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(",", -1);
                if (p[0].equals("PROJECT")) {
                    project = new Project(p[1], LocalDate.parse(p[2]), Double.parseDouble(p[3]), LocalDate.parse(p[4]));
                } else if (project == null) {
                    throw new IOException("The PROJECT line is missing.");
                } else if (p[0].equals("WORKER")) {
                    project.workers.add(new Worker(p[1], Double.parseDouble(p[2])));
                } else if (p[0].equals("MACHINE")) {
                    project.machines.add(new Machine(p[1], Double.parseDouble(p[2])));
                } else if (p[0].equals("MATERIAL")) {
                    project.materials.add(new Material(p[1], Double.parseDouble(p[2]),
                            Double.parseDouble(p[3]), Double.parseDouble(p[4])));
                } else if (p[0].equals("SHIPMENT")) {
                    project.shipments.add(new Shipment(p[1], Double.parseDouble(p[2]),
                            LocalDate.parse(p[3]), p[4]));
                } else if (p[0].equals("ABSENCE")) {
                    project.absences.add(new Absence(p[1], LocalDate.parse(p[2])));
                } else if (p[0].equals("PROBLEM")) {
                    project.problems.add(Problem.create(p[1], LocalDate.parse(p[2]), p[3], Integer.parseInt(p[4])));
                } else if (p[0].equals("TASK")) {
                    project.tasks.add(new Task(p[1], LocalDate.parse(p[2]), LocalDate.parse(p[3]),
                            Integer.parseInt(p[4])));
                } else if (p[0].equals("DAILY")) {
                    project.dailyNotes.add(new DailyNote(LocalDate.parse(p[1]), p.length > 2 ? p[2].replace("&#44;", ",") : ""));
                }
            }
        } catch (RuntimeException e) {
            // a number or date in the file is wrong
            throw new IOException("The file is not correct (" + e.getMessage() + ")");
        }
        if (project == null) {
            throw new IOException("This is not a BuildSim file.");
        }
        return project;
    }

    // All the saved project files, sorted by name
    public static File[] savedFiles() {
        File[] files = new File("projects").listFiles((folder, fileName) -> fileName.endsWith(".csv"));
        if (files == null) {
            return new File[0];
        }
        Arrays.sort(files);
        return files;
    }
}
