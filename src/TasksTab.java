import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.ArrayList;

// A task: the name, the planned dates and how much is done (percent)
class Task implements CsvRecord {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int progress;        // 0 to 100 (percent done)

    public Task(String name, LocalDate startDate, LocalDate endDate, int progress) {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("The task name cannot be empty.");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidInputException("The end date cannot be before the start date.");
        }
        if (progress < 0 || progress > 100) {
            throw new InvalidInputException("The progress must be from 0 to 100.");
        }
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.progress = progress;
    }

    public String getName() { return name; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getProgress() { return progress; }

    public String toCsv() {
        return "TASK," + Project.clean(name) + "," + startDate + "," + endDate + "," + progress;
    }
}

/** The Tasks tab: add the tasks. The table also shows the new dates after the problems. */
public class TasksTab extends JPanel {

    private Main app;

    private JTextField taskNameField = new JTextField(15);
    private JTextField taskStartField = Main.dateField(15);
    private JTextField taskEndField = Main.dateField(15);
    private JTextField taskProgressField = new JTextField(15);
    private JButton addTaskButton = new JButton("Add task");
    private JButton deleteTaskButton = new JButton("Delete selected");
    private JButton editTaskButton = new JButton("Edit selected");
    private DefaultTableModel taskModel = Main.newModel("Task", "Planned start", "Planned end", "New start", "New end", "Progress (%)");
    private JTable taskTable = new JTable(taskModel);

    public TasksTab(Main app) {
        this.app = app;
        JPanel form = Main.makeForm("Task name", taskNameField,
                "Start date (yyyy-mm-dd)", taskStartField,
                "End date (yyyy-mm-dd)", taskEndField,
                "Progress (0 to 100 %)", taskProgressField);
        Main.fillTab(this, form, taskTable, addTaskButton, editTaskButton, deleteTaskButton);
        addTaskButton.addActionListener(e -> addTask());
        editTaskButton.addActionListener(e -> editTask());
        deleteTaskButton.addActionListener(e -> deleteTask());
    }

    private void editTask() {
        int row=Main.selectedRow(taskTable,"task"); if(row<0)return; Task old=app.getProject().getTasks().get(row);
        JTextField name=new JTextField(old.getName()), progress=new JTextField(String.valueOf(old.getProgress())); JTextField start=Main.dateField(15), end=Main.dateField(15); start.setText(old.getStartDate().toString()); end.setText(old.getEndDate().toString());
        int result=JOptionPane.showConfirmDialog(this,Main.makeForm("Task",name,"Start date",start,"End date",end,"Progress (0-100%)",progress),"Edit task",JOptionPane.OK_CANCEL_OPTION);
        if(result!=JOptionPane.OK_OPTION)return; LocalDate s=Main.readDate(start,"Start date"), e=Main.readDate(end,"End date"); Double p=Main.readNumber(progress,"Progress");
        if(s==null||e==null||p==null||name.getText().trim().isEmpty())return; if(e.isBefore(s)||p>100){Main.message("Check the dates and progress (0 to 100).");return;}
        app.getProject().getTasks().set(row,new Task(name.getText().trim(),s,e,(int)Math.round(p))); app.changed();
    }

    private void addTask() {
        String name = taskNameField.getText().trim();
        if (name.isEmpty()) {
            Main.message("Please enter the task name.");
            return;
        }
        LocalDate start = Main.readDate(taskStartField, "Start date");
        if (start == null) {
            return;
        }
        LocalDate end = Main.readDate(taskEndField, "End date");
        if (end == null) {
            return;
        }
        if (end.isBefore(start)) {
            Main.message("The end date cannot be before the start date.");
            return;
        }
        Double progress = Main.readNumber(taskProgressField, "Progress");
        if (progress == null) {
            return;
        }
        if (progress > 100) {
            Main.message("Progress cannot be more than 100.");
            return;
        }
        try {
            app.getProject().getTasks().add(new Task(name, start, end, (int) Math.round(progress)));
        } catch (InvalidInputException e) {
            Main.message(e.getMessage());
            return;
        }
        taskNameField.setText("");
        taskStartField.setText("");
        taskEndField.setText("");
        taskProgressField.setText("");
        app.changed();
    }

    private void deleteTask() {
        int row = Main.selectedRow(taskTable, "task");
        if (row < 0) {
            return;
        }
        app.getProject().getTasks().remove(row);
        app.changed();
    }

    public void refresh() {
        Project p = app.getProject();
        ArrayList<LocalDate[]> newDates = Schedule.newDates(p);     // the dates after the problems
        taskModel.setRowCount(0);
        for (int i = 0; i < p.getTasks().size(); i++) {
            Task t = p.getTasks().get(i);
            taskModel.addRow(new Object[]{t.getName(), t.getStartDate(), t.getEndDate(),
                    newDates.get(i)[0], newDates.get(i)[1], t.getProgress()});
        }
    }
}
