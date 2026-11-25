import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;

// An absence: the worker who did not come to work and the date
class Absence implements CsvRecord {
    private String workerName;
    private LocalDate date;

    public Absence(String workerName, LocalDate date) {
        if (workerName == null || workerName.isBlank()) {
            throw new InvalidInputException("The worker name cannot be empty.");
        }
        this.workerName = workerName;
        this.date = date;
    }

    public String getWorkerName() { return workerName; }
    public LocalDate getDate() { return date; }

    public String toCsv() { return "ABSENCE," + Project.clean(workerName) + "," + date; }
}

/** The Absences tab: the days the workers were absent. One daily wage is deducted for each absence. */
public class AbsencesTab extends JPanel {

    private Main app;

    private JComboBox<String> absWorkerBox = new JComboBox<>();
    private JTextField absDateField = Main.dateField(15);
    private JButton addAbsenceButton = new JButton("Add absence");
    private JButton deleteAbsenceButton = new JButton("Delete selected");
    private DefaultTableModel absenceModel = Main.newModel("Worker", "Date of absence", "Payment deducted (PHP)");
    private JTable absenceTable = new JTable(absenceModel);
    private JLabel totalDeductionLabel = new JLabel("Total payment deducted: PHP 0.00");

    public AbsencesTab(Main app) {
        this.app = app;
        JPanel form = Main.makeForm("Worker", absWorkerBox, "Date of absence (yyyy-mm-dd)", absDateField);
        Main.fillTab(this, form, absenceTable, addAbsenceButton, deleteAbsenceButton);
        add(totalDeductionLabel, BorderLayout.SOUTH);
        addAbsenceButton.addActionListener(e -> addAbsence());
        deleteAbsenceButton.addActionListener(e -> deleteAbsence());
    }

    private void addAbsence() {
        String workerName = (String) absWorkerBox.getSelectedItem();
        if (workerName == null) {
            Main.message("Add a worker first (Workers tab).");
            return;
        }
        LocalDate date = Main.readDate(absDateField, "Date of absence");
        if (date == null) {
            return;
        }
        try {
            app.getProject().getAbsences().add(new Absence(workerName, date));
        } catch (InvalidInputException e) {
            Main.message(e.getMessage());
            return;
        }
        absDateField.setText("");
        app.changed();
    }

    private void deleteAbsence() {
        int row = Main.selectedRow(absenceTable, "absence");
        if (row < 0) {
            return;
        }
        app.getProject().getAbsences().remove(row);
        app.changed();
    }

    public void refresh() {
        Project p = app.getProject();

        // the worker list (keeps the one that was selected)
        Object oldWorker = absWorkerBox.getSelectedItem();
        absWorkerBox.removeAllItems();
        for (Worker w : p.getWorkers()) {
            absWorkerBox.addItem(w.getName());
        }
        if (oldWorker != null) {
            absWorkerBox.setSelectedItem(oldWorker);
        }

        // each absence takes away one daily wage
        absenceModel.setRowCount(0);
        for (Absence a : p.getAbsences()) {
            absenceModel.addRow(new Object[]{a.getWorkerName(), a.getDate(), Main.money(p.getWorkerWage(a.getWorkerName()))});
        }
        totalDeductionLabel.setText("Total payment deducted: PHP " + Main.money(p.getTotalDeduction()));
    }
}
