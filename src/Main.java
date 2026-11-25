import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.geom.Path2D;
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
    private JLabel pageHeading = new JLabel("Project Overview");
    private java.util.List<JButton> navigationButtons = new java.util.ArrayList<>();

    // one object for each tab
    private ProjectTab projectTab = new ProjectTab(this);
    private OverviewTab overviewTab = new OverviewTab(this);
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

        tabs.addTab("Overview", overviewTab);
        tabs.addTab("Projects", projectTab);
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
        tabs.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));
        tabs.setBackground(new Color(0xF3F5F9));
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override protected int calculateTabAreaHeight(int placement, int runs, int maxHeight) { return 0; }
            @Override protected int calculateTabAreaWidth(int placement, int runs, int maxWidth) { return 0; }
            @Override protected void paintTabArea(Graphics g, int placement, int selectedIndex) { }
        });
        tabs.addChangeListener(e -> updateNavigation());

        setLayout(new BorderLayout());
        contentCards.add(buildWelcomeScreen(), "welcome");
        contentCards.add(tabs, "workspace");
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(new Color(0xF3F5F9));
        mainArea.add(buildHeader(), BorderLayout.NORTH);
        mainArea.add(contentCards, BorderLayout.CENTER);
        add(buildSidebar(), BorderLayout.WEST);
        add(mainArea, BorderLayout.CENTER);

        // the other tabs wait until a project is created or opened
        for (int i = 2; i < tabs.getTabCount(); i++) {
            tabs.setEnabledAt(i, false);
        }
        updateNavigation();
    }

    public static void main(String[] args) {
        Font bodyFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
        UIManager.put("defaultFont", bodyFont);
        UIManager.put("Panel.background", new Color(0xF3F5F9));
        UIManager.put("OptionPane.background", new Color(0xF3F5F9));
        UIManager.put("Label.foreground", new Color(0x17263C));
        UIManager.put("Label.font", bodyFont);
        UIManager.put("Button.background", new Color(0xE87816));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", buttonFont);
        UIManager.put("Button.focus", new Color(0x777777));
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("TextField.font", bodyFont);
        UIManager.put("TextField.caretForeground", Color.BLACK);
        UIManager.put("TextField.selectionBackground", new Color(0xE87816));
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("TextArea.foreground", new Color(0x17263C));
        UIManager.put("TextArea.font", bodyFont);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.BLACK);
        UIManager.put("Table.font", bodyFont);
        UIManager.put("TableHeader.background", new Color(0xE5EAF2));
        UIManager.put("TableHeader.foreground", new Color(0x17263C));
        UIManager.put("TableHeader.font", buttonFont);
        UIManager.put("Table.gridColor", new Color(0xE1E6EE));
        UIManager.put("Table.selectionBackground", new Color(0xDDEBFA));
        UIManager.put("Table.selectionForeground", new Color(0x17263C));
        UIManager.put("TabbedPane.selected", new Color(0xF3F5F9));
        UIManager.put("TabbedPane.background", new Color(0xF3F5F9));
        UIManager.put("TabbedPane.foreground", new Color(0x17263C));
        UIManager.put("TabbedPane.font", buttonFont);
        UIManager.put("ComboBox.selectionBackground", new Color(0xE87816));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("Component.focusColor", new Color(0xE87816));
        UIManager.put("TitledBorder.titleColor", new Color(0x17263C));
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(new Color(0xF8F9FC));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE1E6EE)),
                BorderFactory.createEmptyBorder(12, 22, 12, 22)));
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel breadcrumb = new JLabel("⌂   /   Projects   /   BuildSim");
        breadcrumb.setForeground(new Color(0x64748B));
        breadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageHeading.setForeground(new Color(0x17263C));
        pageHeading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.add(breadcrumb);
        heading.add(Box.createVerticalStrut(3));
        heading.add(pageHeading);
        header.add(heading, BorderLayout.WEST);

        projectBadge.setOpaque(true);
        projectBadge.setBackground(new Color(0xFFF0DF));
        projectBadge.setForeground(new Color(0xB84E08));
        projectBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        projectBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xF7C38D)),
                BorderFactory.createEmptyBorder(9, 13, 9, 13)));
        JPanel projectBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        projectBox.setOpaque(false);
        projectBox.add(projectBadge);
        header.add(projectBox, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setBackground(new Color(0x172A41));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 9, 12, 9));
        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        brand.setOpaque(false);
        JLabel logo = new JLabel();
        Image logoImage = loadImage("buildsim-logo.png");
        if (logoImage != null) logo.setIcon(new ImageIcon(logoImage.getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
        JLabel brandName = new JLabel("BuildSim");
        brandName.setForeground(Color.WHITE);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brand.add(logo);
        brand.add(brandName);
        navigation.add(brand);
        JLabel tagline = new JLabel("  Construction Project Simulator");
        tagline.setForeground(new Color(0xC5D0DE));
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tagline.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        navigation.add(tagline);
        addNavigationButton(navigation, "Overview", 0, "overview");
        addNavigationButton(navigation, "Projects", 1, "project");
        addSectionLabel(navigation, "PROJECT MANAGEMENT");
        addNavigationButton(navigation, "Workers", 3, "workers");
        addNavigationButton(navigation, "Machines", 4, "machines");
        addNavigationButton(navigation, "Materials", 5, "materials");
        addNavigationButton(navigation, "Shipments", 6, "shipments");
        addNavigationButton(navigation, "Absences", 7, "absences");
        addNavigationButton(navigation, "Problems", 8, "problems");
        addNavigationButton(navigation, "Tasks", 9, "tasks");
        addNavigationButton(navigation, "Daily Reports", 10, "reports");
        addNavigationButton(navigation, "Costs", 11, "costs");
        addNavigationButton(navigation, "Gantt Chart", 12, "gantt");
        addNavigationButton(navigation, "AI Assistant", 2, "assistant");
        sidebar.add(navigation, BorderLayout.NORTH);
        JLabel footer = new JLabel("  PLAN  •  TRACK  •  BUILD");
        footer.setForeground(new Color(0xAAB8CA));
        footer.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addSectionLabel(JPanel parent, String text) {
        JLabel label = new JLabel("  " + text);
        label.setForeground(new Color(0x91A3B9));
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setBorder(BorderFactory.createEmptyBorder(12, 0, 5, 0));
        parent.add(label);
    }

    private void addNavigationButton(JPanel parent, String text, int tabIndex, String iconName) {
        JButton button = new JButton(text);
        button.setIcon(new FeatureIcon(iconName));
        button.setIconTextGap(13);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(0xEDF2F8));
        button.setBackground(new Color(0x172A41));
        button.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 7));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 39));
        button.addActionListener(e -> {
            if (tabs.isEnabledAt(tabIndex)) {
                contentLayout.show(contentCards, "workspace");
                tabs.setSelectedIndex(tabIndex);
            }
        });
        navigationButtons.add(button);
        parent.add(button);
    }

    /** Small white line icons with BuildSim orange details for the sidebar. */
    private static class FeatureIcon implements Icon {
        private final String name;
        private final Color white = new Color(0xF5F7FA);
        private final Color orange = new Color(0xFF941F);

        FeatureIcon(String name) { this.name = name; }
        public int getIconWidth() { return 25; }
        public int getIconHeight() { return 25; }

        @Override public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(x, y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(white);
            switch (name) {
                case "overview":
                    g.drawLine(3, 12, 12, 4); g.drawLine(12, 4, 22, 12);
                    g.drawRoundRect(6, 11, 12, 11, 1, 1); g.drawRect(10, 15, 4, 7);
                    g.setColor(orange); g.drawLine(3, 12, 6, 12); break;
                case "project":
                    g.drawArc(4, 7, 17, 13, 0, 180); g.drawLine(3, 14, 22, 14);
                    g.drawRoundRect(4, 13, 17, 7, 4, 4); g.setColor(orange);
                    g.drawLine(8, 13, 8, 9); g.drawLine(12, 13, 12, 7); g.drawLine(16, 13, 16, 9); break;
                case "workers":
                    g.drawOval(9, 3, 7, 7); g.drawOval(1, 7, 6, 6); g.drawOval(18, 7, 6, 6);
                    g.drawArc(6, 11, 14, 12, 0, 180); g.drawArc(0, 14, 9, 9, 0, 170); g.drawArc(16, 14, 9, 9, 10, 170);
                    g.setColor(orange); g.drawArc(8, 13, 10, 8, 10, 160); break;
                case "machines":
                    g.drawLine(5, 22, 5, 5); g.drawLine(2, 22, 9, 22); g.drawLine(5, 6, 23, 6);
                    g.drawLine(8, 9, 20, 9); g.drawLine(20, 6, 20, 16); g.drawLine(20, 16, 18, 18);
                    g.drawLine(5, 9, 2, 12); g.drawLine(2, 12, 5, 15); g.setColor(orange); g.drawLine(9, 6, 5, 10); break;
                case "materials":
                    g.drawRect(3, 7, 19, 14); g.drawLine(3, 7, 12, 3); g.drawLine(22, 7, 12, 3);
                    g.drawLine(12, 3, 12, 17); g.drawLine(3, 7, 12, 12); g.drawLine(22, 7, 12, 12);
                    g.setColor(orange); g.drawLine(12, 12, 12, 21); break;
                case "shipments":
                    g.drawRect(2, 7, 13, 10); g.drawLine(15, 10, 20, 10); g.drawLine(20, 10, 23, 14); g.drawLine(23, 14, 23, 17); g.drawLine(15, 17, 23, 17);
                    g.drawOval(5, 16, 4, 4); g.drawOval(18, 16, 4, 4); g.setColor(orange); g.drawLine(1, 10, 4, 10); g.drawLine(0, 13, 3, 13); break;
                case "absences":
                    g.drawRoundRect(3, 5, 19, 17, 2, 2); g.drawLine(3, 10, 22, 10); g.drawLine(8, 3, 8, 7); g.drawLine(17, 3, 17, 7);
                    g.setColor(orange); g.drawLine(8, 15, 11, 18); g.drawLine(11, 18, 17, 12); break;
                case "problems":
                    g.drawLine(12, 3, 23, 21); g.drawLine(23, 21, 1, 21); g.drawLine(1, 21, 12, 3);
                    g.setColor(orange); g.drawLine(12, 9, 12, 15); g.fillOval(11, 17, 3, 3); break;
                case "tasks":
                    for (int row = 0; row < 3; row++) {
                        int yy = 5 + row * 7;
                        g.setColor(orange); g.drawLine(2, yy + 2, 4, yy + 4); g.drawLine(4, yy + 4, 7, yy);
                        g.setColor(white); g.drawLine(11, yy + 2, 23, yy + 2);
                    } break;
                case "reports":
                    g.drawRoundRect(4, 2, 17, 21, 2, 2); g.drawLine(8, 7, 17, 7); g.drawLine(8, 10, 17, 10);
                    g.setColor(orange); g.fillRect(8, 15, 3, 5); g.fillRect(13, 12, 3, 8); g.fillRect(18, 9, 2, 11); break;
                case "costs":
                    g.drawOval(3, 5, 14, 7); g.drawArc(3, 8, 14, 9, 180, 180); g.drawLine(3, 8, 3, 15); g.drawLine(17, 8, 17, 15);
                    g.drawArc(3, 12, 14, 7, 0, 180); g.setColor(orange); g.drawOval(10, 2, 13, 7); g.drawArc(10, 5, 13, 9, 180, 180); break;
                case "gantt":
                    g.drawLine(3, 5, 3, 21); g.drawLine(3, 21, 23, 21); g.setColor(orange);
                    g.fillRoundRect(5, 7, 10, 3, 2, 2); g.fillRoundRect(10, 12, 11, 3, 2, 2); g.fillRoundRect(7, 17, 8, 3, 2, 2); break;
                case "assistant":
                    Path2D star = new Path2D.Double(); star.moveTo(12, 2); star.lineTo(15, 9); star.lineTo(23, 12); star.lineTo(15, 15); star.lineTo(12, 23); star.lineTo(9, 15); star.lineTo(2, 12); star.lineTo(9, 9); star.closePath();
                    g.setColor(white); g.draw(star); g.setColor(orange); g.fillOval(10, 10, 5, 5); break;
                default: break;
            }
            g.dispose();
        }
    }

    private void updateNavigation() {
        int[] indexes = {0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 2};
        String[] titles = {"Project Overview", "Projects", "Workers", "Machines", "Materials",
                "Shipments", "Absences", "Problems", "Tasks", "Daily Reports", "Costs", "Gantt Chart", "AI Assistant"};
        int selectedIndex = tabs.getSelectedIndex();
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] == selectedIndex) {
                pageHeading.setText(titles[i]);
                break;
            }
        }
        for (int i = 0; i < navigationButtons.size(); i++) {
            JButton button = navigationButtons.get(i);
            int tabIndex = indexes[i];
            button.setBackground(tabs.getSelectedIndex() == tabIndex
                    ? new Color(0xE87816) : new Color(0x172A41));
            button.setForeground(tabs.isEnabledAt(tabIndex) ? Color.WHITE : new Color(0x8291A5));
            button.setEnabled(tabs.isEnabledAt(tabIndex));
        }
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
        title.setForeground(new Color(0x17263C));
        JLabel subtitle = new JLabel("Keep your people, materials, schedule, and progress\nin one simple project workspace.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(0x334155));
        JButton start = new JButton("Get Started");
        start.setFont(new Font("Segoe UI", Font.BOLD, 16));
        start.setForeground(Color.WHITE);
        start.setBackground(new Color(0xE87816));
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
        tabs.setSelectedIndex(0);
        overviewTab.refresh();
        updateNavigation();
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
        overviewTab.refresh();
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
        table.setGridColor(new Color(0xE1E6EE));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(0x17263C));
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(0xE5EAF2));
        header.setForeground(new Color(0x17263C));
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
