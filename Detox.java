import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Detox {

    public class focusSession {
        String duration;
        LocalDateTime timeStamp;

        focusSession(String duration, LocalDateTime timeStamp) {
            this.duration = duration;
            this.timeStamp = timeStamp;
        }
    }

    public void startFocusTimer(int time, Scanner sc) {
        System.out.println("Lock your phone!");
        System.out.println("Focus mode ON for "+time);
        long mili = time * 60000L;
        try {
            Thread.sleep(mili);
        } catch(InterruptedException e) {
            System.out.println("Focus mode interrupted");
            sc.close();
            return;
        }

        System.out.println("Yayyy! You did it. You focused for "+ time + " mins");
        System.out.println("So What's Your mood today? Write your thoughts down!");
        String mood = sc.nextLine();
        try {
            FileWriter writer = new FileWriter("Focus.txt", true);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter form = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
            writer.write(now.format(form) + " Focused for "+ time+ " mins" + " - "+ mood + System.lineSeparator());
            writer.flush();
            writer.close();
        } catch(IOException e) {
            System.out.println("Error saving the session"+e.getMessage());
        }
    }

    public void viewHistory() {
        File file = new File("Focus.txt");
        if(!file.exists()) {
            System.out.println("No focus sessions found yet. Start one today!");
            return;
        }
        try(Scanner reader = new Scanner(file)) {
            System.out.println("\n Past Focus Sessions:\n");

            int count = 0;
            while(reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if(!line.isEmpty()) {
                    count++;
                    System.out.println(count + ". "+ line);
                }
            }
            System.out.println("You have completed "+count+ " sessions so far!");
            if(count ==1) {
                System.out.println("No sessions recorded yet!");
            }
        } catch(IOException e) {
            System.out.println("Error reading session history");
        }
    }
    public void motivationalQuotes() {
        try {
            // Create HTTP client
            HttpClient client = HttpClient.newHttpClient();

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://zenquotes.io/api/random"))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the response
            String json = response.body();

            String quote = json.split("\"q\":\"")[1].split("\",")[0];
            String author = json.split("\"a\":\"")[1].split("\"")[0];

            System.out.println("\"" + quote + "\"");
            System.out.println(" " + author);

        } catch (Exception e) {
            System.out.println("Failed to fetch quote.");
            e.printStackTrace();
        }
    }

    // public void updateStreak() {

    // }

    public void showStreak() {
        File streakfile = new File("streaks.txt");
        List<LocalDate> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try{
            if(streakfile.exists()) {
                Scanner reader = new Scanner(streakfile);
                while(reader.hasNextLine()) {
                    String line = reader.nextLine().trim();
                    if(!line.isEmpty()) {
                        dateList.add(LocalDate.parse(line, formatter));
                    }
                }
                reader.close();
            }
            Collections.sort(dateList,Collections.reverseOrder());
            int streak = 0;
            LocalDate today = LocalDate.now();
            for(LocalDate date : dateList) {
                if(date.equals(today.minusDays(streak))) {
                    streak++;
                } else {
                    break;
                }
            }
            System.out.println("Current Streak : "+streak+" day(s)");
        } catch (Exception e) {
            System.err.println("Error calculating streak: "+ e.getMessage());
        }
    }

    public void updateStreak() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String todayStr = today.format(formatter);
        File streakFile = new File("streaks.txt");

        Set<String> streakDates = new HashSet<>();
        try {
            if (streakFile.exists()) {
                Scanner reader = new Scanner(streakFile);
                while (reader.hasNextLine()) {
                    String line = reader.nextLine().trim();
                    if (!line.isEmpty()) {
                        streakDates.add(line);
                    }
                }
                reader.close();
            }

            if (!streakDates.contains(todayStr)) {
                FileWriter writer = new FileWriter(streakFile, true);
                writer.write(todayStr + System.lineSeparator());
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error updating streak: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Detox d = new Detox();
        int choice;
        do {
            System.out.println("\nWelcome to Digital Detox CLI");
            System.out.println("1. Start focus timer");
            System.out.println("2. View focus history");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3) : ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("How many minutes do you want to focus? (1 - 1440) : ");
                    int time = sc.nextInt();
                    sc.nextLine();
                    d.startFocusTimer(time, sc);
                    d.motivationalQuotes();
                    d.updateStreak();
                    d.showStreak();
                }
                case 2 -> d.viewHistory();
                case 3 -> System.out.println("Stay mindful, see you soon!");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 3);
        sc.close();
    }
}