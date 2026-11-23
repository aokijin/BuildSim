import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * BuildSim - a simple construction project program.
 * This is the main window. Run this file.
 *
 * Every tab is in its own file:
 *   ProjectTab, AssistantTab, WorkersTab, MachinesTab, MaterialsTab, ShipmentsTab, AbsencesTab,
 *   ProblemsTab, TasksTab, DailyReportTab, CostsTab, GanttTab
 * Everything is saved to a CSV file (one file for each project) in the "projects" folder.
 */
public class BuildSim extends JFrame {

    private Project project;                         // the project that is open (null = none yet)
    private JTabbedPane tabs = new JTabbedPane();

    // one object for each tab
    private ProjectTab projectTab = new ProjectTab(this);
    private AssistantTab assistantTab = new AssistantTab(this);
    private WorkersTab workersTab = new WorkersTab(this);
    private MachinesTab machinesTab = new MachinesTab(this);
    private MaterialsTab materialsTab = new MaterialsTab(this);
    private ShipmentsTab shipmentsTab = new ShipmentsTab(this);
    private AbsencesTab absencesTab = new AbsencesTab(this);
    private ProblemsTab problemsTab = new ProblemsTab(this);
    private TasksTab tasksTab = new TasksTab(this);
    private DailyReportTab dailyReportTab = new DailyReportTab(this);
    private CostsTab costsTab = new CostsTab(this);
    private GanttTab ganttTab = new GanttTab(this);

    public BuildSim() {
        super("BuildSim - Construction Project");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 640);
        setLocationRelativeTo(null);

        tabs.addTab("Project", projectTab);
        tabs.addTab("Assistant", assistantTab);
        tabs.addTab("Workers", workersTab);
        tabs.addTab("Machines", machinesTab);
        tabs.addTab("Materials", materialsTab);
        tabs.addTab("Shipments", shipmentsTab);
        tabs.addTab("Absences", absencesTab);
        tabs.addTab("Problems", problemsTab);
        tabs.addTab("Tasks", tasksTab);
        tabs.addTab("Daily Report", dailyReportTab);
        tabs.addTab("Costs", costsTab);
        tabs.addTab("Gantt Chart", ganttTab);
        add(tabs);

        // the other tabs wait until a project is created or opened
        for (int i = 1; i < tabs.getTabCount(); i++) {
            tabs.setEnabledAt(i, false);
        }
    }

    public static void main(String[] args) {
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("Button.background", new Color(0xF0F0F0));
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("TextArea.foreground", Color.BLACK);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.BLACK);
        UIManager.put("Table.selectionBackground", new Color(0x333333));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("TabbedPane.background", new Color(0xE8E8E8));
        UIManager.put("TabbedPane.foreground", Color.BLACK);
        UIManager.put("ComboBox.selectionBackground", new Color(0x333333));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("TextField.selectionBackground", new Color(0x333333));
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("Component.focusColor", new Color(0x666666));
        SwingUtilities.invokeLater(() -> new BuildSim().setVisible(true));
    }

    public Project getProject() {
        return project;
    }

    // Called by the Project tab when a project is created or opened
    public void setProject(Project newProject) {
        project = newProject;
        for (int i = 1; i < tabs.getTabCount(); i++) {
            tabs.setEnabledAt(i, true);
        }
        setTitle("BuildSim - " + project.getName());
        refreshAll();
        tabs.setSelectedIndex(assistantTab.isReady() ? 1 : 2);     // the Assistant if the key is set, else Workers
    }

    // After every change: save to the CSV file and show the new data
    public void changed() {
        try {
            project.save();
        } catch (java.io.IOException e) {
            message("Could not save the project: " + e.getMessage());
        }
        refreshAll();
    }

    private void refreshAll() {
        assistantTab.refresh();
        workersTab.refresh();
        machinesTab.refresh();
        materialsTab.refresh();
        shipmentsTab.refresh();
        absencesTab.refresh();
        problemsTab.refresh();
        tasksTab.refresh();
        dailyReportTab.refresh();
        costsTab.refresh();
        ganttTab.refresh();
    }

    // ====================================================================
    //  Helpers used by all the tabs
    // ====================================================================

    // A table that the user cannot type in
    public static DefaultTableModel newModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // A form: label, field, label, field, ...
    public static JPanel makeForm(Object... labelsAndFields) {
        JPanel grid = new JPanel(new GridLayout(labelsAndFields.length / 2, 2, 10, 8));
        for (int i = 0; i < labelsAndFields.length; i += 2) {
            grid.add(new JLabel((String) labelsAndFields[i]));
            grid.add((JComponent) labelsAndFields[i + 1]);
        }
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.add(grid);
        return wrapper;
    }

    // Puts the form + buttons on top and the table below, inside the tab
    public static void fillTab(JPanel tab, JPanel form, JButton addButton, JButton deleteButton, JTable table) {
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(deleteButton);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        tab.setLayout(new BorderLayout(10, 10));
        tab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tab.add(top, BorderLayout.NORTH);
        tab.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public static void message(String text) {
        JOptionPane.showMessageDialog(null, text);
    }

    public static String money(double amount) {
        return String.format("%,.2f", amount);
    }

    public static String number(double value) {
        return String.format("%,.1f", value);
    }

    // Reads a date like 2026-11-02. Shows a message and returns null if it is wrong.
    public static LocalDate readDate(JTextField field, String name) {
        try {
            return LocalDate.parse(field.getText().trim());
        } catch (DateTimeParseException e) {
            message(name + " must look like 2026-11-02 (year-month-day).");
            return null;
        }
    }

    // Reads a number. Shows a message and returns null if it is wrong.
    public static Double readNumber(JTextField field, String name) {
        try {
            double value = Double.parseDouble(field.getText().trim().replace(",", ""));
            if (value < 0) {
                message(name + " cannot be negative.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            message(name + " must be a number.");
            return null;
        }
    }

    // The row the user clicked in a table (shows a message if there is none)
    public static int selectedRow(JTable table, String what) {
        int row = table.getSelectedRow();
        if (row < 0) {
            message("Select a " + what + " in the table first.");
        }
        return row;
    }
}

// Our own exception: it is thrown when a value is not allowed (for example a negative wage).
// The classes check their own data, and the tabs catch this exception and show the message.
class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
