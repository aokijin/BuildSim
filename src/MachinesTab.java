import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// A machine: the name and the rate per day
class Machine extends Resource {
    private double ratePerDay;

    public Machine(String name, double ratePerDay) {
        super(name);
        if (ratePerDay < 0) {
            throw new InvalidInputException("The rate per day cannot be negative.");
        }
        this.ratePerDay = ratePerDay;
    }

    public double getRatePerDay() { return ratePerDay; }

    public String getKind() { return "Machine"; }

    // The rent for some working days
    public double getCost(int days) { return ratePerDay * days; }

    public String toCsv() { return "MACHINE," + Project.clean(getName()) + "," + ratePerDay; }
}

/** The Machines tab: add the machines with their rate per day. */
public class MachinesTab extends JPanel {

    private Main app;

    private JTextField machineNameField = new JTextField(15);
    private JTextField machineRateField = new JTextField(15);
    private JButton addMachineButton = new JButton("Add machine");
    private JButton deleteMachineButton = new JButton("Delete selected");
    private DefaultTableModel machineModel = Main.newModel("Machine name", "Rate per day (PHP)");
    private JTable machineTable = new JTable(machineModel);

    public MachinesTab(Main app) {
        this.app = app;
        JPanel form = Main.makeForm("Machine name", machineNameField, "Rate per day (PHP)", machineRateField);
        Main.fillTab(this, form, machineTable, addMachineButton, deleteMachineButton);
        addMachineButton.addActionListener(e -> addMachine());
        deleteMachineButton.addActionListener(e -> deleteMachine());
    }

    private void addMachine() {
        String name = machineNameField.getText().trim();
        if (name.isEmpty()) {
            Main.message("Please enter the machine name.");
            return;
        }
        Double rate = Main.readNumber(machineRateField, "Rate per day");
        if (rate == null) {
            return;
        }
        try {
            app.getProject().getMachines().add(new Machine(name, rate));
        } catch (InvalidInputException e) {
            Main.message(e.getMessage());
            return;
        }
        machineNameField.setText("");
        machineRateField.setText("");
        app.changed();
    }

    private void deleteMachine() {
        Project project = app.getProject();
        int row = Main.selectedRow(machineTable, "machine");
        if (row < 0) {
            return;
        }
        Machine removed = project.getMachines().remove(row);
        project.removeProblems("Machinery", removed.getName());      // its breakdowns are removed too
        app.changed();
    }

    public void refresh() {
        machineModel.setRowCount(0);
        for (Machine m : app.getProject().getMachines()) {
            machineModel.addRow(new Object[]{m.getName(), Main.money(m.getRatePerDay())});
        }
    }
}
