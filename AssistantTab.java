import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The Assistant tab: a chat where you type what you want to add, for example
 * "Ana was absent on Nov 3". Gemini turns the sentence into commands, the tab shows what it
 * understood, and it adds the items only after you say yes.
 */
public class AssistantTab extends JPanel {

    private static final String DEFAULT_MODEL = "gemini-flash-lite-latest";
    private static final String DEFAULT_URL = "https://generativelanguage.googleapis.com/v1beta";

    private Main app;
    private Project shownProject;                   // used to show the welcome text once for each project
    private ArrayList<Command> pending;             // what was understood and waits for yes / no

    private JLabel keyLabel = new JLabel();
    private JTextArea chatArea = new JTextArea();
    private JTextField inputField = new JTextField();
    private JButton sendButton = new JButton("Send");
    private JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JButton yesButton = new JButton("Yes, add them");
    private JButton noButton = new JButton("No, cancel");

    public AssistantTab(Main app) {
        this.app = app;

        // top: the status of the API key
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(keyLabel);

        // middle: the chat
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        chatArea.setMargin(new Insets(8, 8, 8, 8));

        // bottom: the yes / no buttons (only when something waits) and the typing field
        confirmPanel.add(new JLabel("Add what I understood?"));
        confirmPanel.add(yesButton);
        confirmPanel.add(noButton);
        confirmPanel.setVisible(false);

        JPanel typing = new JPanel(new BorderLayout(8, 0));
        typing.add(inputField, BorderLayout.CENTER);
        typing.add(sendButton, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout(0, 6));
        bottom.add(confirmPanel, BorderLayout.NORTH);
        bottom.add(typing, BorderLayout.SOUTH);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        inputField.addActionListener(e -> send());          // the Enter key sends too
        sendButton.addActionListener(e -> send());
        yesButton.addActionListener(e -> confirm());
        noButton.addActionListener(e -> cancel());
        updateKeyLabel();
    }

    // ====================================================================
    //  The API key and the model
    // ====================================================================

    // Configure the owner's key through GEMINI_API_KEY or a local ignored gemini-key.txt file.
    private String findKey() {
        String fromEnvironment = System.getenv("GEMINI_API_KEY");
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return fromEnvironment.trim();
        }
        try {
            String text = Files.readString(Paths.get("gemini-key.txt")).trim();
            if (!text.isEmpty()) {
                return text.split("\\s+")[0];
            }
        } catch (IOException e) {
            try {
                String text = Files.readString(Paths.get("src", "gemini-key.txt")).trim();
                if (!text.isEmpty()) return text.split("\\s+")[0];
            } catch (IOException ignored) {
                // the owner has not configured a local key file
            }
        }
        return null;
    }

    public boolean isReady() {
        return findKey() != null;
    }

    private String modelName() {
        String fromEnvironment = System.getenv("GEMINI_MODEL");
        return (fromEnvironment == null || fromEnvironment.isBlank()) ? DEFAULT_MODEL : fromEnvironment.trim();
    }

    private String baseUrl() {
        String fromEnvironment = System.getenv("GEMINI_BASE_URL");
        return (fromEnvironment == null || fromEnvironment.isBlank()) ? DEFAULT_URL : fromEnvironment.trim();
    }

    private void updateKeyLabel() {
        if (isReady()) {
            keyLabel.setText("Assistant ready   |   Model: " + modelName());
            keyLabel.setForeground(Color.BLACK);
        } else {
            keyLabel.setText("Assistant unavailable: owner must configure GEMINI_API_KEY or gemini-key.txt.");
            keyLabel.setForeground(Color.BLACK);
        }
    }

    // ====================================================================
    //  The chat
    // ====================================================================

    private void addLine(String text) {
        chatArea.append(text + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // The names and dates of the open project. Gemini needs them to understand names like "ana".
    private String projectInfo() {
        Project p = app.getProject();
        return "The open project is called " + p.getName() + ". It runs from " + p.getStartDate() + " to " + p.getEndDate() + ".\n"
                + "Workers: " + namesOf(new Names(p).workers) + "\n"
                + "Machines: " + namesOf(new Names(p).machines) + "\n"
                + "Materials: " + namesOf(new Names(p).materials);
    }

    private String namesOf(ArrayList<String> names) {
        return names.isEmpty() ? "(none yet)" : String.join(", ", names);
    }

    // Called by the window after every change: shows the welcome text when a new project is open
    public void refresh() {
        Project project = app.getProject();
        if (project != shownProject) {
            shownProject = project;
            pending = null;
            confirmPanel.setVisible(false);
            chatArea.setText("");
            addLine("Bot: The project \"" + project.getName() + "\" is open. Tell me what to add, for example:");
            addLine("   add workers Ana 600 and Ben 600 a day");
            addLine("   excavator rental 1000 per day");
            addLine("   cement 100 bags at 280 each, 40 in stock");
            addLine("   cement shipment of 60 bags on nov 10, pending");
            addLine("   ana was absent on nov 3 and nov 4");
            addLine("   rain on 11-10, 1 day lost");
            addLine("   task Dig from 11-02 to 11-06, 100% done");
            addLine("I show what I understood and I ask before I add anything.");
            addLine("");
        }
        updateKeyLabel();
    }

    private void send() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || !sendButton.isEnabled()) {
            return;
        }
        inputField.setText("");
        addLine("You: " + text);

        // while something waits, "yes" and "no" answer it
        if (pending != null) {
            String answer = text.toLowerCase();
            if (answer.equals("yes") || answer.equals("y") || answer.equals("ok")) {
                confirm();
                return;
            }
            if (answer.equals("no") || answer.equals("n") || answer.equals("cancel")) {
                cancel();
                return;
            }
            pending = null;                         // a new message: the old question is dropped
            confirmPanel.setVisible(false);
        }

        String key = findKey();
        if (key == null) {
            addLine("Bot: The project owner needs to configure the Assistant. See the README for GEMINI_API_KEY setup.");
            return;
        }

        // the call to Gemini takes some seconds, so it runs in its own thread and the window does not freeze
        Interpreter interpreter = new GeminiInterpreter(key, modelName(), baseUrl());
        String info = projectInfo();
        sendButton.setEnabled(false);
        addLine("Bot: ...");
        new Thread(() -> {
            String reply = null;
            String error = null;
            try {
                reply = interpreter.interpret(text, info);
            } catch (IOException e) {
                error = e.getMessage();
            }
            String finalReply = reply;
            String finalError = error;
            SwingUtilities.invokeLater(() -> showReply(finalReply, finalError));
        }).start();
    }

    // Shows what Gemini answered: the questions, what was understood, and the problems
    private void showReply(String reply, String error) {
        sendButton.setEnabled(true);
        inputField.requestFocusInWindow();
        if (error != null) {
            addLine("Bot: " + error);
            return;
        }

        Names names = new Names(app.getProject());
        ArrayList<Command> commands = new ArrayList<>();
        ArrayList<String> problems = new ArrayList<>();
        boolean asked = false;

        for (String raw : reply.split("\\r?\\n")) {
            String line = raw.trim();
            if (line.startsWith("```")) {
                continue;                           // the AI sometimes puts the lines between ``` marks
            }
            if (line.startsWith("- ") || line.startsWith("* ")) {
                line = line.substring(2).trim();
            }
            if (line.isEmpty()) {
                continue;
            }
            if (line.toUpperCase().startsWith("ASK|")) {
                addLine("Bot: " + line.substring(4).trim());
                asked = true;
                continue;
            }
            try {
                commands.add(Command.fromLine(line, names));
            } catch (InvalidInputException e) {
                problems.add(e.getMessage());
            }
        }

        if (!commands.isEmpty()) {
            addLine("Bot: I understood:");
            for (int i = 0; i < commands.size(); i++) {
                addLine("   " + (i + 1) + ". " + commands.get(i).describe());
            }
        }
        for (String problem : problems) {
            addLine("Bot: I could not use this. " + problem);
        }
        if (!commands.isEmpty()) {
            pending = commands;
            confirmPanel.setVisible(true);
            addLine("Bot: Add these? (click Yes, or type yes / no)");
        } else if (!asked && problems.isEmpty()) {
            addLine("Bot: I did not find anything to add. Try to say it in another way.");
        }
    }

    // The user said yes: every command adds its item to the project
    private void confirm() {
        if (pending == null) {
            return;                                 // nothing waits for an answer
        }
        Project project = app.getProject();
        int done = 0;
        ArrayList<String> failed = new ArrayList<>();
        for (Command command : pending) {
            try {
                command.run(project);
                done++;
            } catch (InvalidInputException e) {
                failed.add(command.describe() + " -> " + e.getMessage());
            }
        }
        pending = null;
        confirmPanel.setVisible(false);
        addLine("Bot: Done. " + done + (done == 1 ? " item" : " items") + " added.");
        for (String f : failed) {
            addLine("Bot: I could not do: " + f);
        }
        addLine("");
        app.changed();                              // saves the CSV and updates all the tabs
    }

    private void cancel() {
        if (pending == null) {
            return;
        }
        pending = null;
        confirmPanel.setVisible(false);
        addLine("Bot: Okay, nothing was added.");
        addLine("");
    }
}
