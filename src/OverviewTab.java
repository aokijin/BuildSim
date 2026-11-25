import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.temporal.ChronoUnit;

/** Dashboard summary for the project currently open in BuildSim. */
public class OverviewTab extends JPanel {
    private final Main app;
    private final JLabel projectName = new JLabel("No project selected");
    private final JLabel budgetValue = new JLabel("—");
    private final JLabel costValue = new JLabel("—");
    private final JLabel remainingValue = new JLabel("—");
    private final JLabel overallValue = new JLabel("0%");
    private final JLabel progressValue = new JLabel("0%");
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel progressCaption = new JLabel("Create or open a project to see progress.");
    private final JLabel timelineStartValue = new JLabel("—");
    private final JLabel timelineEndValue = new JLabel("—");
    private final JLabel delayedValue = new JLabel("0");
    private final JLabel pendingValue = new JLabel("0");
    private final JLabel problemValue = new JLabel("0");
    private final DefaultTableModel taskModel = Main.newModel("TASK", "PLANNED DATES", "PROGRESS", "STATUS");

    public OverviewTab(Main app) {
        this.app = app;
        setLayout(new BorderLayout(12, 14));
        setBackground(new Color(0xF3F5F9));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel intro = new JPanel(new BorderLayout(10, 4));
        intro.setOpaque(false);
        JLabel subtitle = new JLabel("Your project at a glance");
        subtitle.setForeground(new Color(0x64748B));
        projectName.setFont(new Font("Segoe UI", Font.BOLD, 21));
        projectName.setForeground(new Color(0x17263C));
        intro.add(projectName, BorderLayout.WEST);
        intro.add(subtitle, BorderLayout.EAST);

        JPanel metrics = new JPanel(new GridLayout(1, 4, 10, 0));
        metrics.setOpaque(false);
        metrics.add(metricCard("PROJECT BUDGET", budgetValue, new Color(0xE87816)));
        metrics.add(metricCard("ESTIMATED COST", costValue, new Color(0x3975B9)));
        metrics.add(metricCard("BUDGET REMAINING", remainingValue, new Color(0x2B9461)));
        metrics.add(metricCard("OVERALL PROGRESS", overallValue, new Color(0xE87816)));

        JPanel leftColumn = new JPanel(new BorderLayout(0, 12));
        leftColumn.setOpaque(false);
        leftColumn.add(progressCard(), BorderLayout.NORTH);
        leftColumn.add(taskCard(), BorderLayout.CENTER);

        JPanel rightColumn = new JPanel();
        rightColumn.setOpaque(false);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.add(timelineCard());
        rightColumn.add(Box.createVerticalStrut(12));
        rightColumn.add(alertCard());

        JPanel dashboard = new JPanel(new BorderLayout(12, 12));
        dashboard.setOpaque(false);
        dashboard.add(leftColumn, BorderLayout.CENTER);
        dashboard.add(rightColumn, BorderLayout.EAST);
        rightColumn.setPreferredSize(new Dimension(270, 300));

        JPanel dashboardContent = new JPanel(new BorderLayout(0, 14));
        dashboardContent.setOpaque(false);
        dashboardContent.add(metrics, BorderLayout.NORTH);
        dashboardContent.add(dashboard, BorderLayout.CENTER);
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.add(intro, BorderLayout.NORTH);
        content.add(dashboardContent, BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
        refresh();
    }

    private JPanel metricCard(String label, JLabel value, Color accent) {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 10));
        JLabel caption = new JLabel(label);
        caption.setForeground(new Color(0x64748B));
        caption.setFont(new Font("Segoe UI", Font.BOLD, 11));
        value.setForeground(new Color(0x17263C));
        value.setFont(new Font("Segoe UI", Font.BOLD, 22));
        card.add(caption, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0xE1E6EE)),
                        BorderFactory.createEmptyBorder(13, 14, 13, 12))));
        return card;
    }

    private JPanel progressCard() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(10, 12));
        JLabel title = sectionTitle("Construction Progress");
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(title, BorderLayout.WEST);
        heading.add(progressValue, BorderLayout.EAST);
        progressValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        progressValue.setForeground(new Color(0xE87816));
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(0xE87816));
        progressBar.setBackground(new Color(0xE2E7EF));
        progressBar.setPreferredSize(new Dimension(300, 18));
        card.add(heading, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setOpaque(false);
        body.add(progressBar, BorderLayout.NORTH);
        body.add(progressCaption, BorderLayout.CENTER);
        progressCaption.setForeground(new Color(0x475569));
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel taskCard() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.add(sectionTitle("Task Schedule"), BorderLayout.NORTH);
        JTable table = new JTable(taskModel);
        Main.styleTable(table);
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(500, 210));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel timelineCard() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.add(sectionTitle("Project Timeline"), BorderLayout.NORTH);
        JPanel dates = new JPanel();
        dates.setOpaque(false);
        dates.setLayout(new BoxLayout(dates, BoxLayout.Y_AXIS));
        dates.add(timelineRow("Start date", timelineStartValue));
        dates.add(Box.createVerticalStrut(10));
        dates.add(timelineRow("Target end", timelineEndValue));
        card.add(dates, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(270, 154));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 154));
        return card;
    }

    private JPanel alertCard() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.add(sectionTitle("Project Alerts"), BorderLayout.NORTH);
        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.add(alertRow("Delayed shipments", delayedValue));
        rows.add(Box.createVerticalStrut(9));
        rows.add(alertRow("Pending shipments", pendingValue));
        rows.add(Box.createVerticalStrut(9));
        rows.add(alertRow("Recorded problems", problemValue));
        card.add(rows, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(270, 170));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 170));
        return card;
    }

    private JPanel timelineRow(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout(4, 1));
        row.setOpaque(false);
        JLabel caption = new JLabel(label);
        caption.setForeground(new Color(0x64748B));
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        value.setForeground(new Color(0x17263C));
        value.setFont(new Font("Segoe UI", Font.BOLD, 13));
        row.add(caption, BorderLayout.NORTH);
        row.add(value, BorderLayout.CENTER);
        return row;
    }

    private JPanel alertRow(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        JLabel caption = new JLabel(label);
        caption.setForeground(new Color(0x334155));
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        value.setForeground(new Color(0xB84E08));
        value.setFont(new Font("Segoe UI", Font.BOLD, 13));
        row.add(caption, BorderLayout.CENTER);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD9E0EA)),
                BorderFactory.createEmptyBorder(15, 16, 15, 16)));
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel title = new JLabel(text);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(new Color(0x17263C));
        return title;
    }

    public void refresh() {
        Project project = app.getProject();
        if (project == null) return;
        projectName.setText(project.getName());
        budgetValue.setText("₱" + Main.money(project.getBudget()));
        int projectDays = Math.max(1, (int) ChronoUnit.DAYS.between(project.getStartDate(), project.getEndDate()));
        double estimated = 0;
        for (Worker resource : project.getWorkers()) estimated += resource.getCost(projectDays);
        for (Machine resource : project.getMachines()) estimated += resource.getCost(projectDays);
        for (Material resource : project.getMaterials()) estimated += resource.getCost(projectDays);
        costValue.setText("₱" + Main.money(estimated));
        remainingValue.setText("₱" + Main.money(project.getBudget() - estimated));

        int totalProgress = 0;
        for (Task task : project.getTasks()) totalProgress += task.getProgress();
        int progress = project.getTasks().isEmpty() ? 0 : totalProgress / project.getTasks().size();
        overallValue.setText(progress + "%");
        progressValue.setText(progress + "%");
        progressBar.setValue(progress);
        progressCaption.setText(progress + "% of planned work completed across " + project.getTasks().size() + " tasks");
        timelineStartValue.setText(project.getStartDate().toString());
        timelineEndValue.setText(project.getEndDate().toString());

        int delayed = 0, pending = 0;
        for (Shipment shipment : project.getShipments()) {
            if ("Delayed".equals(shipment.getStatus())) delayed++;
            if ("Pending".equals(shipment.getStatus())) pending++;
        }
        int problems = project.getProblems().size();
        delayedValue.setText(String.valueOf(delayed));
        pendingValue.setText(String.valueOf(pending));
        problemValue.setText(String.valueOf(problems));

        taskModel.setRowCount(0);
        for (Task task : project.getTasks()) {
            String status = task.getProgress() >= 100 ? "Completed" : task.getProgress() > 0 ? "In progress" : "Pending";
            taskModel.addRow(new Object[]{task.getName(), task.getStartDate() + " – " + task.getEndDate(),
                    task.getProgress() + "%", status});
        }
    }
}
