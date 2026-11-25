import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/** The Gantt Chart tab: one bar for each task. */
public class GanttTab extends JPanel {

    private Main app;
    private GanttPanel ganttPanel = new GanttPanel();

    public GanttTab(Main app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(ganttPanel), BorderLayout.CENTER);
        add(new JLabel("<html>Light gray = planned days.  Medium gray = days added by problems.  Black = how much is done.<br>"
                + "When a problem happens before a task starts, the whole bar moves later.</html>"), BorderLayout.SOUTH);
    }

    public void refresh() {
        ganttPanel.revalidate();
        ganttPanel.repaint();
    }

    // The drawing area
    private class GanttPanel extends JPanel {

        public GanttPanel() {
            setBackground(Color.WHITE);
        }

        public Dimension getPreferredSize() {
            Project project = app.getProject();
            int rows = (project == null) ? 0 : project.getTasks().size();
            return new Dimension(800, 80 + rows * 34);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Project project = app.getProject();
            if (project == null || project.getTasks().isEmpty()) {
                g.setColor(Color.BLACK);
                g.drawString("No tasks yet. Add tasks in the Tasks tab.", 20, 30);
                return;
            }
            ArrayList<Task> tasks = project.getTasks();
            ArrayList<LocalDate[]> newDates = Schedule.newDates(project);

            // the first day and the last day of all the bars
            LocalDate first = tasks.get(0).getStartDate();
            LocalDate last = newDates.get(0)[1];
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getStartDate().isBefore(first)) {
                    first = tasks.get(i).getStartDate();
                }
                if (newDates.get(i)[1].isAfter(last)) {
                    last = newDates.get(i)[1];
                }
            }
            int totalDays = Schedule.length(first, last);

            int left = 160;        // space for the task names
            int top = 40;          // space for the dates
            int rowHeight = 34;
            double dayWidth = (getWidth() - left - 50) / (double) totalDays;

            // the dates on top (every 7 days)
                g.setColor(new Color(0xD0D0D0));
            for (int day = 0; day < totalDays; day += 7) {
                int x = left + (int) (day * dayWidth);
                g.drawLine(x, top - 6, x, top + tasks.size() * rowHeight);
                g.drawString(first.plusDays(day).toString().substring(5), x + 3, top - 12);
            }

            // one bar for each task
            for (int i = 0; i < tasks.size(); i++) {
                Task t = tasks.get(i);
                LocalDate newStart = newDates.get(i)[0];
                LocalDate newEnd = newDates.get(i)[1];
                int plannedLength = Schedule.length(t.getStartDate(), t.getEndDate());
                int newLength = Schedule.length(newStart, newEnd);

                int y = top + i * rowHeight + 6;
                int x = left + (int) (ChronoUnit.DAYS.between(first, newStart) * dayWidth);
                int plannedWidth = Math.max(4, (int) (plannedLength * dayWidth));
                int addedWidth = (int) ((newLength - plannedLength) * dayWidth);

                String name = t.getName();
                if (name.length() > 20) {
                    name = name.substring(0, 20) + "...";
                }
                g.setColor(Color.BLACK);
                g.drawString(name, 10, y + 16);

                g.setColor(new Color(0xE5E5E5));                        // planned days
                g.fillRect(x, y, plannedWidth, 22);
                g.setColor(new Color(0xA0A0A0));                        // days added by problems
                g.fillRect(x + plannedWidth, y, addedWidth, 22);
                g.setColor(Color.BLACK);                                // the part that is done
                g.fillRect(x, y, (plannedWidth + addedWidth) * t.getProgress() / 100, 22);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, plannedWidth + addedWidth, 22);
                g.drawString(t.getProgress() + "%", x + plannedWidth + addedWidth + 6, y + 16);
            }
        }
    }
}
