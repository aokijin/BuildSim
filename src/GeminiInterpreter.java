import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;

// Abstraction: an Interpreter is anything that turns a sentence of the user into command lines.
// GeminiInterpreter is one kind (another one could be added later without changing the Assistant tab).
interface Interpreter {
    // userText: what the user typed.  projectInfo: the names and dates of the open project.
    // Gives back the answer as text: one command line for each thing to do.
    String interpret(String userText, String projectInfo) throws IOException;
}

/**
 * Sends the user's sentence to Google Gemini and gets back command lines like ADD_WORKER|Ana|600.
 * It uses only what comes with Java (HttpClient). There is no extra library.
 */
class GeminiInterpreter implements Interpreter {

    private String apiKey;
    private String model;        // for example gemini-flash-lite-latest
    private String baseUrl;      // https://generativelanguage.googleapis.com/v1beta

    public GeminiInterpreter(String apiKey, String model, String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
    }

    // What we tell Gemini to do. The answer must be easy to read for our program.
    private static String instructions(String projectInfo) {
        return "You are the data-entry assistant of a construction project program called BuildSim.\n"
                + "The user types a short message. Turn it into commands for the program.\n"
                + "\n"
                + "Reply with ONLY command lines, one command on each line. No other text, no markdown, no numbering.\n"
                + "The | sign separates the parts. The commands are:\n"
                + "ADD_WORKER|name|dailyWage\n"
                + "ADD_MACHINE|name|ratePerDay\n"
                + "ADD_MATERIAL|name|quantity|priceEach|inStock\n"
                + "ADD_SHIPMENT|materialName|quantity|expectedDate|status\n"
                + "ADD_ABSENCE|workerName|date\n"
                + "ADD_PROBLEM|type|date|detail|daysLost\n"
                + "ADD_TASK|name|startDate|endDate|progress\n"
                + "ASK|a short question\n"
                + "\n"
                + "Rules:\n"
                + "- Dates are written yyyy-mm-dd. If the user leaves out the month or the year, use the month and the year of the project start date.\n"
                + "- Numbers are plain numbers: 600, not PHP 600 and not 600.00 pesos.\n"
                + "- status is Pending, Delivered or Delayed.\n"
                + "- type is Weather, Machinery or Shipment delay. For Weather the detail is - (a dash). "
                + "For Machinery the detail is the machine name. For Shipment delay the detail is the material name.\n"
                + "- progress is from 0 to 100 (use 0 if the user does not say). daysLost is a whole number (use 1 if the user does not say).\n"
                + "- When the user means a worker, machine or material that is in the lists below, write its name exactly as in the list.\n"
                + "- Never invent a number or a date. If a wage, a price, a quantity or a date is missing, use ASK to ask for it.\n"
                + "- If the message is not about adding data, reply with: ASK|I can only add workers, machines, materials, shipments, absences, problems and tasks.\n"
                + "\n"
                + projectInfo;
    }

    public String interpret(String userText, String projectInfo) throws IOException {
        String body = "{"
                + "\"system_instruction\":{\"parts\":[{\"text\":" + quote(instructions(projectInfo)) + "}]},"
                + "\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":" + quote(userText) + "}]}],"
                + "\"generationConfig\":{\"temperature\":0}"
                + "}";

        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/models/" + model + ":generateContent"))
                .timeout(Duration.ofSeconds(40))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response;
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("The request was stopped.");
        } catch (IOException e) {
            throw new IOException("Could not reach Gemini. Check the internet connection. (" + e.getMessage() + ")");
        }

        int code = response.statusCode();
        String answer = response.body();
        if (code == 200) {
            String text = String.join("\n", valuesOf(answer, "text"));
            if (text.isBlank()) {
                throw new IOException("Gemini did not send any text. Try to say it in another way.");
            }
            return text;
        }

        // something went wrong: tell the user in simple words
        ArrayList<String> messages = valuesOf(answer, "message");
        String detail = messages.isEmpty() ? "" : " (" + messages.get(0) + ")";
        if (code == 429) {
            throw new IOException("Gemini says the free limit was reached (too many requests). Wait a minute and try again." + detail);
        } else if (code == 401 || code == 403) {
            throw new IOException("Gemini did not accept the API key (code " + code + "). Check the key." + detail);
        } else if (code == 404) {
            throw new IOException("Gemini does not know the model '" + model + "'. Change the model name (see the README)." + detail);
        } else if (code == 400) {
            throw new IOException("Gemini did not accept the request (code 400). The API key may be wrong." + detail);
        }
        throw new IOException("Gemini is not working right now (code " + code + "). Try again later." + detail);
    }

    // ----------------------- small JSON helpers -----------------------

    // Puts a text between quotes for JSON, and writes the special characters in the JSON way
    static String quote(String text) {
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') {
                out.append("\\\"");
            } else if (c == '\\') {
                out.append("\\\\");
            } else if (c == '\n') {
                out.append("\\n");
            } else if (c == '\r') {
                out.append("\\r");
            } else if (c == '\t') {
                out.append("\\t");
            } else if (c < 0x20) {
                out.append(String.format("\\u%04x", (int) c));
            } else {
                out.append(c);
            }
        }
        return out.append('"').toString();
    }

    // Finds every text value of a key in the JSON, for example every "text": "..." in the answer
    static ArrayList<String> valuesOf(String json, String key) {
        ArrayList<String> found = new ArrayList<>();
        int i = 0;
        while (i < json.length()) {
            if (json.charAt(i) != '"') {
                i++;
                continue;
            }
            int[] end = new int[1];
            String token = readString(json, i, end);          // a text between quotes
            i = end[0];

            // a text that is followed by a colon is a key
            int j = skipSpaces(json, i);
            if (j < json.length() && json.charAt(j) == ':' && token.equals(key)) {
                j = skipSpaces(json, j + 1);
                if (j < json.length() && json.charAt(j) == '"') {
                    found.add(readString(json, j, end));
                    i = end[0];
                }
            }
        }
        return found;
    }

    private static int skipSpaces(String json, int i) {
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        return i;
    }

    // Reads the JSON text that starts with the quote at json.charAt(start).
    // It gives back the text, and end[0] is the place after the closing quote.
    private static String readString(String json, int start, int[] end) {
        StringBuilder text = new StringBuilder();
        int i = start + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '"') {
                end[0] = i + 1;
                return text.toString();
            }
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                if (next == 'n') {
                    text.append('\n');
                } else if (next == 't') {
                    text.append('\t');
                } else if (next == 'r') {
                    text.append('\r');
                } else if (next == 'u' && i + 5 < json.length()) {
                    text.append((char) Integer.parseInt(json.substring(i + 2, i + 6), 16));
                    i += 4;
                } else {
                    text.append(next);                 // \" or \\ or \/
                }
                i += 2;
            } else {
                text.append(c);
                i++;
            }
        }
        end[0] = json.length();
        return text.toString();
    }
}
