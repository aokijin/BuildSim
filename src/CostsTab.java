import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;

/** The Costs tab: materials, salaries, machines, the total cost and the budget. */
public class CostsTab extends JPanel {

    private Main app;

    private DefaultTableModel costModel = Main.newModel("Item", "Amount (PHP)");
    private JTable costTable = new JTable(costModel);

    public CostsTab(Main app) {
        this.app = app;

        Main.styleTable(costTable);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(costTable), BorderLayout.CENTER);
        add(new JLabel("<html>Salaries and machines are counted for every working day, from the start date to the new last day of the work.<br>"
                + "Materials still in stock were bought but not used: that money is not lost, but it is not working yet.</html>"), BorderLayout.SOUTH);
    }

    public void refresh() {
        Project p = app.getProject();
        ArrayList<DayRow> rows = Schedule.dailyRows(p);

        double materials = Schedule.materialsTotal(p);
        double salaries = Schedule.salaryTotal(rows);
        double machines = Schedule.machinesTotal(p, rows);
        double total = materials + salaries + machines;
        double left = p.getBudget() - total;

        costModel.setRowCount(0);
        costModel.addRow(new Object[]{"Materials (quantity x price each)", Main.money(materials)});
        costModel.addRow(new Object[]{"Salaries of the workers (" + Schedule.workingDays(rows) + " working days)", Main.money(salaries)});
        costModel.addRow(new Object[]{"Machines (every machine, every working day)", Main.money(machines)});
        costModel.addRow(new Object[]{"TOTAL COST", Main.money(total)});
        costModel.addRow(new Object[]{"Budget", Main.money(p.getBudget())});
        costModel.addRow(new Object[]{left >= 0 ? "Money left" : "OVER THE BUDGET BY", Main.money(Math.abs(left))});
        costModel.addRow(new Object[]{"Payment deducted for absences (already not paid)", Main.money(p.getTotalDeduction())});
        costModel.addRow(new Object[]{"Materials still in stock (value)", Main.money(Schedule.stockValue(p))});
    }
}
