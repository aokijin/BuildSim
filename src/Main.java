import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.awt.CardLayout;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * BuildSim - a simple construction project program.
 * This is the main window. Run this file.
 *
 * Every tab is in its own file:
 *   ProjectTab, AssistantTab, WorkersTab, MachinesTab, MaterialsTab, ShipmentsTab, AbsencesTab,
 *   ProblemsTab, TasksTab, DailyReportTab, CostsTab, GanttTab
 * Everything is saved to a CSV file (one file for each project) in the "projects" folder.
 */
public class Main extends JFrame {

    private Project project;                         // the project that is open (null = none yet)
    private JTabbedPane tabs = new JTabbedPane();
    private CardLayout contentLayout = new CardLayout();
    private JPanel contentCards = new JPanel(contentLayout);
    private JLabel projectBadge = new JLabel("PROJECT / NO PROJECT OPEN");

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

    public Main() {
        super("BuildSim - Construction Project");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.max(980, Math.min(1280, screen.width - 60)),
                Math.max(640, Math.min(820, screen.height - 100)));
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
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBorder(BorderFactory.createEmptyBorder(2, 8, 8, 8));
        tabs.addChangeListener(e -> updateTabColors());
        updateTabColors();

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        contentCards.add(buildWelcomeScreen(), "welcome");
        contentCards.add(tabs, "workspace");
        add(contentCards, BorderLayout.CENTER);

        // the other tabs wait until a project is created or opened
        for (int i = 1; i < tabs.getTabCount(); i++) {
            tabs.setEnabledAt(i, false);
        }
    }

    public static void main(String[] args) {
        Font bodyFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
        UIManager.put("defaultFont", bodyFont);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("Label.font", bodyFont);
        UIManager.put("Button.background", new Color(0xF3F3F3));
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.font", buttonFont);
        UIManager.put("Button.focus", new Color(0x777777));
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("TextField.font", bodyFont);
        UIManager.put("TextField.caretForeground", Color.BLACK);
        UIManager.put("TextField.selectionBackground", new Color(0x333333));
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("TextArea.foreground", Color.BLACK);
        UIManager.put("TextArea.font", bodyFont);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.BLACK);
        UIManager.put("Table.font", bodyFont);
        UIManager.put("TableHeader.background", new Color(0x282828));
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.font", buttonFont);
        UIManager.put("Table.gridColor", new Color(0xD5D5D5));
        UIManager.put("Table.selectionBackground", new Color(0x333333));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("TabbedPane.background", new Color(0x292929));
        UIManager.put("TabbedPane.foreground", Color.WHITE);
        UIManager.put("TabbedPane.font", buttonFont);
        UIManager.put("ComboBox.selectionBackground", new Color(0x333333));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("Component.focusColor", new Color(0x666666));
        UIManager.put("TitledBorder.titleColor", Color.BLACK);
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(new Color(0x181818));
        header.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        brand.setOpaque(false);
        JLabel logo = new JLabel();
        Image logoImage = loadImage("buildsim-logo.png");
        if (logoImage != null) logo.setIcon(new ImageIcon(logoImage.getScaledInstance(54, 54, Image.SCALE_SMOOTH)));
        brand.add(logo);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("BUILDSIM");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 23));
        JLabel subtitle = new JLabel("CONSTRUCTION PROJECT TRACKING");
        subtitle.setForeground(new Color(0xD0D0D0));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titles.add(name);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitle);
        brand.add(titles);
        header.add(brand, BorderLayout.WEST);

        projectBadge.setOpaque(true);
        projectBadge.setBackground(new Color(0x303030));
        projectBadge.setForeground(Color.WHITE);
        projectBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        projectBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x777777)),
                BorderFactory.createEmptyBorder(9, 13, 9, 13)));
        JPanel projectBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        projectBox.setOpaque(false);
        projectBox.add(projectBadge);
        header.add(projectBox, BorderLayout.EAST);
        return header;
    }

    private JPanel buildWelcomeScreen() {
        JPanel welcome = new JPanel(new BorderLayout());
        welcome.setBackground(Color.WHITE);
        JPanel hero = new JPanel(new GridBagLayout()) {
            private final Image image = loadImage("buildsim-hero.png");
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (image == null) return;
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                double scale = Math.max((double)getWidth() / image.getWidth(null), (double)getHeight() / image.getHeight(null));
                int w = (int)(image.getWidth(null) * scale), h = (int)(image.getHeight(null) * scale);
                g.drawImage(image, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);
                g.dispose();
            }
        };
        hero.setBorder(BorderFactory.createEmptyBorder(42, 54, 42, 54));
        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("CONSTRUCTION PROJECT MANAGEMENT");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 13));
        eyebrow.setForeground(new Color(0x333333));
        JLabel title = new JLabel("Build with clarity.");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.BLACK);
        JLabel subtitle = new JLabel("Keep your people, materials, schedule, and progress\nin one simple project workspace.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(0x282828));
        JButton start = new JButton("Get Started");
        start.setFont(new Font("Segoe UI", Font.BOLD, 16));
        start.setForeground(Color.WHITE);
        start.setBackground(Color.BLACK);
        start.setFocusPainted(false);
        start.setBorder(BorderFactory.createEmptyBorder(13, 26, 13, 26));
        start.addActionListener(e -> {
            contentLayout.show(contentCards, "workspace");
            tabs.setSelectedIndex(0);
        });
        copy.add(eyebrow);
        copy.add(Box.createVerticalStrut(14));
        copy.add(title);
        copy.add(Box.createVerticalStrut(12));
        copy.add(subtitle);
        copy.add(Box.createVerticalStrut(25));
        copy.add(start);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
        c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE;
        hero.add(copy, c);
        welcome.add(hero, BorderLayout.CENTER);
        JLabel hint = new JLabel("  Create or open a project to begin tracking work.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        welcome.add(hint, BorderLayout.SOUTH);
        return welcome;
    }

    private static Image loadImage(String name) {
        try {
            java.net.URL resource = Main.class.getResource("/" + name);
            return resource != null ? ImageIO.read(resource) : ImageIO.read(new File("src", name));
        } catch (IOException e) {
            return null;
        }
    }

    private void updateTabColors() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            boolean selected = i == tabs.getSelectedIndex();
            tabs.setBackgroundAt(i, selected ? Color.WHITE : new Color(0x292929));
            tabs.setForegroundAt(i, selected ? Color.BLACK : Color.WHITE);
        }
    }

    public Project getProject() {
        return project;
    }

    // Called by the Project tab when a project is created or opened
    public void setProject(Project newProject) {
        project = newProject;
        contentLayout.show(contentCards, "workspace");
        projectBadge.setText("PROJECT / " + project.getName().toUpperCase());
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

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(0xD5D5D5));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(0x282828));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
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
    public static void fillTab(JPanel tab, JPanel form, JTable table, JButton... actionButtons) {
        styleTable(table);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton button : actionButtons) buttons.add(button);

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
    public static JFormattedTextField dateField(int columns) {
        try {
            MaskFormatter formatter = new MaskFormatter("####-##-##");
            formatter.setPlaceholderCharacter('_');
            formatter.setAllowsInvalid(false);
            formatter.setOverwriteMode(true);
            JFormattedTextField field = new JFormattedTextField(formatter);
            field.setColumns(columns);
            return field;
        } catch (java.text.ParseException e) {
            throw new IllegalStateException("Could not create date input", e);
        }
    }

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
