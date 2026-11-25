import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;

// A problem on the site. There are 3 kinds: bad weather, a machine that broke, a late shipment
abstract class Problem implements CsvRecord {
    private LocalDate date;      // the day it happened
    private int daysLost;        // how many days the work is delayed

    public Problem(LocalDate date, int daysLost) {
        if (daysLost < 1) {
            throw new InvalidInputException("Days lost must be 1 or more.");
        }
        this.date = date;
        this.daysLost = daysLost;
    }

    public LocalDate getDate() { return date; }
    public int getDaysLost() { return daysLost; }

    // Every kind of problem says what it is and what it is about
    public abstract String getType();
    public abstract String getDetail();         // the machine or the material ("-" for the weather)

    // The text for the lists. It works for every kind because it asks the kind (getType, getDetail).
    public String getDescription() {
        String text = getType();
        if (!getDetail().equals("-")) {
            text = text + ": " + getDetail();
        }
        return text + " (" + daysLost + (daysLost == 1 ? " day" : " days") + " lost)";
    }

    public String toCsv() {
        return "PROBLEM," + getType() + "," + date + "," + Project.clean(getDetail()) + "," + daysLost;
    }

    // Makes the right kind of problem from its type (used by the Problems tab and when a file is loaded)
    public static Problem create(String type, LocalDate date, String detail, int daysLost) {
        if (type.equals("Weather")) {
            return new WeatherProblem(date, daysLost);
        } else if (type.equals("Machinery")) {
            return new MachineryProblem(date, detail, daysLost);
        } else if (type.equals("Shipment delay")) {
            return new ShipmentDelayProblem(date, detail, daysLost);
        }
        throw new InvalidInputException("Unknown kind of problem: " + type);
    }
}

// Bad weather
class WeatherProblem extends Problem {
    public WeatherProblem(LocalDate date, int daysLost) {
        super(date, daysLost);
    }

    public String getType() { return "Weather"; }
    public String getDetail() { return "-"; }
}

// A machine that broke
class MachineryProblem extends Problem {
    private String machineName;

    public MachineryProblem(LocalDate date, String machineName, int daysLost) {
        super(date, daysLost);
        this.machineName = machineName;
    }

    public String getType() { return "Machinery"; }
    public String getDetail() { return machineName; }
}

// A shipment of a material that came late
class ShipmentDelayProblem extends Problem {
    private String materialName;

    public ShipmentDelayProblem(LocalDate date, String materialName, int daysLost) {
        super(date, daysLost);
        this.materialName = materialName;
    }

    public String getType() { return "Shipment delay"; }
    public String getDetail() { return materialName; }
}

/** The Problems tab: add what went wrong, the date, and how many days are lost. */
public class ProblemsTab extends JPanel {

    private Main app;

    private JComboBox<String> problemTypeBox = new JComboBox<>(new String[]{"Weather", "Machinery", "Shipment delay"});
    private JComboBox<String> problemDetailBox = new JComboBox<>();
    private JTextField problemDateField = Main.dateField(15);
    private JTextField problemDaysField = new JTextField(15);
    private JButton addProblemButton = new JButton("Add problem");
    private JButton deleteProblemButton = new JButton("Delete selected");
    private DefaultTableModel problemModel = Main.newModel("Problem", "Date", "Machine / material", "Days lost");
    private JTable problemTable = new JTable(problemModel);

    public ProblemsTab(Main app) {
        this.app = app;
        JPanel form = Main.makeForm("Problem", problemTypeBox,
                "Machine / material", problemDetailBox,
                "Date it happened (yyyy-mm-dd)", problemDateField,
                "Days lost", problemDaysField);
        Main.fillTab(this, form, problemTable, addProblemButton, deleteProblemButton);
        add(new JLabel("A problem pushes back all the work that is not finished yet. See the new dates in the Tasks tab."), BorderLayout.SOUTH);

        problemTypeBox.addActionListener(e -> fillDetailBox());
        addProblemButton.addActionListener(e -> addProblem());
        deleteProblemButton.addActionListener(e -> deleteProblem());
    }

    // The second list shows the machines or the materials, depending on the kind of problem
    private void fillDetailBox() {
        Project project = app.getProject();
        if (project == null) {
            return;
        }
        problemDetailBox.removeAllItems();
        String type = (String) problemTypeBox.getSelectedItem();
        if (type.equals("Weather")) {
            problemDetailBox.addItem("-");
        } else if (type.equals("Machinery")) {
            for (Machine m : project.getMachines()) {
                problemDetailBox.addItem(m.getName());
            }
        } else {
            for (Material m : project.getMaterials()) {
                problemDetailBox.addItem(m.getName());
            }
        }
    }

    private void addProblem() {
        String type = (String) problemTypeBox.getSelectedItem();
        String detail = (String) problemDetailBox.getSelectedItem();
        if (detail == null) {
            Main.message("Add a machine (Machines tab) or a material (Materials tab) first.");
            return;
        }
        LocalDate date = Main.readDate(problemDateField, "Date");
        if (date == null) {
            return;
        }
        Double days = Main.readNumber(problemDaysField, "Days lost");
        if (days == null) {
            return;
        }
        if (days < 1 || days != Math.floor(days)) {
            Main.message("Days lost must be a whole number, 1 or more.");
            return;
        }
        try {
            app.getProject().getProblems().add(Problem.create(type, date, detail, (int) Math.round(days)));
        } catch (InvalidInputException e) {
            Main.message(e.getMessage());
            return;
        }
        problemDateField.setText("");
        problemDaysField.setText("");
        app.changed();
    }

    private void deleteProblem() {
        int row = Main.selectedRow(problemTable, "problem");
        if (row < 0) {
            return;
        }
        app.getProject().getProblems().remove(row);
        app.changed();
    }

    public void refresh() {
        fillDetailBox();
        problemModel.setRowCount(0);
        for (Problem p : app.getProject().getProblems()) {
            problemModel.addRow(new Object[]{p.getType(), p.getDate(), p.getDetail(), p.getDaysLost()});
        }
    }
}
