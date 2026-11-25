import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// A worker: the name and the daily wage
class Worker extends Resource {
    private double dailyWage;

    public Worker(String name, double dailyWage) {
        super(name);
        if (dailyWage < 0) {
            throw new InvalidInputException("The daily wage cannot be negative.");
        }
        this.dailyWage = dailyWage;
    }

    public double getDailyWage() { return dailyWage; }

    public String getKind() { return "Worker"; }

    // The wage for some working days
    public double getCost(int days) { return dailyWage * days; }

    public String toCsv() { return "WORKER," + Project.clean(getName()) + "," + dailyWage; }
}

/** The Workers tab: add the workers with their daily wage. */
public class WorkersTab extends JPanel {

    private Main app;

    private JTextField workerNameField = new JTextField(15);
    private JTextField wageField = new JTextField(15);
    private JButton addWorkerButton = new JButton("Add worker");
    private JButton deleteWorkerButton = new JButton("Delete selected");
    private DefaultTableModel workerModel = Main.newModel("Worker name", "Daily wage (PHP)");
    private JTable workerTable = new JTable(workerModel);

    public WorkersTab(Main app) {
        this.app = app;
        JPanel form = Main.makeForm("Worker name", workerNameField, "Daily wage (PHP)", wageField);
        Main.fillTab(this, form, workerTable, addWorkerButton, deleteWorkerButton);
        addWorkerButton.addActionListener(e -> addWorker());
        deleteWorkerButton.addActionListener(e -> deleteWorker());
    }

    private void addWorker() {
        Project project = app.getProject();
        String name = workerNameField.getText().trim();
        if (name.isEmpty()) {
            Main.message("Please enter the worker name.");
            return;
        }
        for (Worker w : project.getWorkers()) {
            if (w.getName().equals(name)) {
                Main.message("This worker is already in the list.");
                return;
            }
        }
        Double wage = Main.readNumber(wageField, "Daily wage");
        if (wage == null) {
            return;
        }
        try {
            project.getWorkers().add(new Worker(name, wage));
        } catch (InvalidInputException e) {
            Main.message(e.getMessage());
            return;
        }
        workerNameField.setText("");
        wageField.setText("");
        app.changed();
    }

    private void deleteWorker() {
        Project project = app.getProject();
        int row = Main.selectedRow(workerTable, "worker");
        if (row < 0) {
            return;
        }
        Worker removed = project.getWorkers().remove(row);
        // the absences of this worker are removed too
        for (int i = project.getAbsences().size() - 1; i >= 0; i--) {
            if (project.getAbsences().get(i).getWorkerName().equals(removed.getName())) {
                project.getAbsences().remove(i);
            }
        }
        app.changed();
    }

    public void refresh() {
        workerModel.setRowCount(0);
        for (Worker w : app.getProject().getWorkers()) {
            workerModel.addRow(new Object[]{w.getName(), Main.money(w.getDailyWage())});
        }
    }
}
