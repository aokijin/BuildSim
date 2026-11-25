import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * The Daily Report tab: one line for each day with the workers who came, the salary,
 * the percent finished and what happened.
 * (The calculations of the whole program are in the class Schedule at the bottom of this file.)
 */
public class DailyReportTab extends JPanel {

    private Main app;

    private DefaultTableModel dailyModel = Main.newModel("Date", "Workers present", "Salary (PHP)", "Finished (%)", "What happened");
    private JTable dailyTable = new JTable(dailyModel);
    private JTextField reportDate = Main.dateField(12);
    private JTextArea reportNote = new JTextArea(3, 55);
    private JButton saveNote = new JButton("Add / update daily note");

    public DailyReportTab(Main app) {
        this.app = app;

        Main.styleTable(dailyTable);
        dailyTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        dailyTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        dailyTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        dailyTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        dailyTable.getColumnModel().getColumn(4).setPreferredWidth(450);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(dailyTable), BorderLayout.CENTER);
        JPanel editor = new JPanel(new BorderLayout(8,8));
        editor.add(Main.makeForm("Report date (yyyy-mm-dd)",reportDate),BorderLayout.NORTH);
        reportNote.setLineWrap(true); reportNote.setWrapStyleWord(true);
        JPanel noteBox=new JPanel(new BorderLayout(4,4)); noteBox.add(new JLabel("What happened or was accomplished (optional)"),BorderLayout.NORTH); noteBox.add(new JScrollPane(reportNote),BorderLayout.CENTER);
        JPanel noteActions=new JPanel(new FlowLayout(FlowLayout.LEFT)); noteActions.add(saveNote); noteBox.add(noteActions,BorderLayout.SOUTH); editor.add(noteBox,BorderLayout.CENTER);
        JPanel bottom=new JPanel(new BorderLayout()); bottom.add(editor,BorderLayout.CENTER); bottom.add(new JLabel("Sunday is a day off. Select a date and add an optional note; daily notes are saved with the project."),BorderLayout.SOUTH);
        add(bottom,BorderLayout.SOUTH);
        saveNote.addActionListener(e -> saveDailyNote());
        dailyTable.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting() && dailyTable.getSelectedRow()>=0) { Object date=dailyModel.getValueAt(dailyTable.getSelectedRow(),0); reportDate.setText(String.valueOf(date)); reportNote.setText(noteFor(app.getProject(), LocalDate.parse(String.valueOf(date)))); } });
    }

    public void refresh() {
        dailyModel.setRowCount(0);
        for (DayRow r : Schedule.dailyRows(app.getProject())) {
            String note=noteFor(app.getProject(),r.date); String happened=r.problems;
            if(!note.isBlank()) happened += (happened.isBlank()?"":"; ")+note;
            dailyModel.addRow(new Object[]{r.date, r.workers, Main.money(r.salary), Main.number(r.percent), happened});
        }
    }

    private String noteFor(Project p, LocalDate date) { for(DailyNote n:p.getDailyNotes()) if(n.getDate().equals(date)) return n.getText(); return ""; }
    private void saveDailyNote() {
        LocalDate date=Main.readDate(reportDate,"Report date"); if(date==null)return;
        String text=reportNote.getText().trim(); Project p=app.getProject(); DailyNote found=null;
        for(DailyNote n:p.getDailyNotes()) if(n.getDate().equals(date)){found=n;break;}
        if(text.isEmpty()) { if(found!=null)p.getDailyNotes().remove(found); }
        else if(found==null)p.getDailyNotes().add(new DailyNote(date,text)); else found.setText(text);
        app.changed();
    }
}

class DailyNote implements CsvRecord {
    private LocalDate date; private String text;
    DailyNote(LocalDate date,String text){this.date=date;this.text=text==null?"":text;}
    LocalDate getDate(){return date;} String getText(){return text;} void setText(String value){text=value;}
    public String toCsv(){return "DAILY,"+date+","+Project.clean(text.replace(",","&#44;"));}
}

// ---------------------------------------------------------------
// Below: the calculations (new dates, day-by-day report, costs)
// ---------------------------------------------------------------

// One line of the day-by-day report
class DayRow {
    LocalDate date;
    boolean dayOff;          // Sunday: no work and no pay
    int workers;             // workers who came to work
    double salary;           // salary paid on that day
    double percent;          // how much of the whole project is finished by that day
    String problems = "";    // what went wrong on that day
}

/**
 * The calculations of the program:
 *   1. the new dates of the tasks after the problems
 *   2. the day-by-day report (workers, salary, percent finished)
 *   3. the costs
 */
class Schedule {

    // Number of days of a task, counting the first and the last day
    public static int length(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    // ------------------ 1. new dates after the problems ------------------

    // The new start and end date of every task (same order as the task list).
    // A problem pushes back all the work that is not finished on the day it happened:
    //    - a task that did not start yet moves later (start and end)
    //    - a task that already started ends later
    //    - a task that is 100% done does not move
    public static ArrayList<LocalDate[]> newDates(Project project) {
        ArrayList<LocalDate[]> dates = new ArrayList<>();
        for (Task t : project.getTasks()) {
            dates.add(new LocalDate[]{t.getStartDate(), t.getEndDate()});
        }

        ArrayList<Problem> problems = new ArrayList<>(project.getProblems());
        problems.sort((a, b) -> a.getDate().compareTo(b.getDate()));    // earliest problem first

        for (Problem p : problems) {
            for (int i = 0; i < dates.size(); i++) {
                if (project.getTasks().get(i).getProgress() >= 100) {
                    continue;
                }
                LocalDate[] d = dates.get(i);
                if (!d[0].isBefore(p.getDate())) {
                    d[0] = d[0].plusDays(p.getDaysLost());
                    d[1] = d[1].plusDays(p.getDaysLost());
                } else if (!d[1].isBefore(p.getDate())) {
                    d[1] = d[1].plusDays(p.getDaysLost());
                }
            }
        }
        return dates;
    }

    // The last day of the work (the project end date if there are no tasks)
    public static LocalDate lastDay(Project project, ArrayList<LocalDate[]> dates) {
        LocalDate last = project.getEndDate();
        if (!dates.isEmpty()) {
            last = dates.get(0)[1];
            for (LocalDate[] d : dates) {
                if (d[1].isAfter(last)) {
                    last = d[1];
                }
            }
        }
        for (DailyNote note : project.getDailyNotes()) {
            if (note.getDate().isAfter(last)) last = note.getDate();
        }
        return last;
    }

    // ------------------ 2. day-by-day report ------------------

    private static boolean isAbsent(Project project, String workerName, LocalDate day) {
        for (Absence a : project.getAbsences()) {
            if (a.getWorkerName().equals(workerName) && a.getDate().equals(day)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<DayRow> dailyRows(Project project) {
        ArrayList<DayRow> rows = new ArrayList<>();
        ArrayList<LocalDate[]> dates = newDates(project);
        LocalDate last = lastDay(project, dates);

        int totalLength = 0;                       // all the task days together
        for (LocalDate[] d : dates) {
            totalLength = totalLength + length(d[0], d[1]);
        }

        for (LocalDate day = project.getStartDate(); !day.isAfter(last); day = day.plusDays(1)) {
            DayRow row = new DayRow();
            row.date = day;
            row.dayOff = (day.getDayOfWeek() == DayOfWeek.SUNDAY);

            // workers and salary (the workers who were absent are not paid)
            if (!row.dayOff) {
                for (Worker w : project.getWorkers()) {
                    if (!isAbsent(project, w.getName(), day)) {
                        row.workers++;
                        row.salary = row.salary + w.getDailyWage();
                    }
                }
            }

            // percent finished: every task is finished little by little between its new dates
            double done = 0;
            for (LocalDate[] d : dates) {
                int taskLength = length(d[0], d[1]);
                if (!day.isBefore(d[1])) {
                    done = done + taskLength;                                  // finished
                } else if (!day.isBefore(d[0])) {
                    done = done + length(d[0], day);                           // on the way
                }
            }
            row.percent = (totalLength == 0) ? 0 : done * 100 / totalLength;

            // what happened that day
            if (row.dayOff) {
                row.problems = "Sunday (day off)";
            }
            for (Problem p : project.getProblems()) {
                if (p.getDate().equals(day)) {
                    row.problems = row.problems + (row.problems.isEmpty() ? "" : "; ") + p.getDescription();
                }
            }
            for (Absence a : project.getAbsences()) {
                if (a.getDate().equals(day)) {
                    row.problems = row.problems + (row.problems.isEmpty() ? "" : "; ") + a.getWorkerName() + " absent";
                }
            }
            rows.add(row);
        }
        return rows;
    }

    // ------------------ 3. costs ------------------

    // The cost of a list of resources for some working days.
    // The call r.getCost(days) is the same, but each kind does its own calculation:
    // a Worker (wage x days), a Machine (rate x days) or a Material (quantity x price each).
    public static double costOf(ArrayList<? extends Resource> resources, int days) {
        double total = 0;
        for (Resource r : resources) {
            total = total + r.getCost(days);
        }
        return total;
    }

    // What we paid for all the materials (quantity x price each)
    public static double materialsTotal(Project project) {
        return costOf(project.getMaterials(), 0);
    }

    // The value of the materials that are still in stock (not used yet)
    public static double stockValue(Project project) {
        double total = 0;
        for (Material m : project.getMaterials()) {
            total = total + m.getInStock() * m.getPriceEach();
        }
        return total;
    }

    public static double salaryTotal(ArrayList<DayRow> rows) {
        double total = 0;
        for (DayRow r : rows) {
            total = total + r.salary;
        }
        return total;
    }

    public static int workingDays(ArrayList<DayRow> rows) {
        int days = 0;
        for (DayRow r : rows) {
            if (!r.dayOff) {
                days++;
            }
        }
        return days;
    }

    // Every machine is counted for every working day
    public static double machinesTotal(Project project, ArrayList<DayRow> rows) {
        return costOf(project.getMachines(), workingDays(rows));
    }
}
