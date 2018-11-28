package controller;

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
//import java.nio.file.Files;
//import java.nio.file.Paths;

public class Initializer {
    public static final File settingsFile = new File("settings.json");

    public static JSONObject settings = new JSONObject();

    public static final String COUNT_FROM_ALL_KEY        = "countFromAll";
    public static final String CHOOSE_FROM_1ST_ROUND_KEY = "chooseHall2MemberFrom1stRound";

    public void initialize () {
        if (!settingsFile.exists()) {
            /* the following are default values  */
            settings.put(COUNT_FROM_ALL_KEY, false);
            settings.put(CHOOSE_FROM_1ST_ROUND_KEY, true);

            try (FileWriter writer = new FileWriter(settingsFile)) {
                // String JSONContent = new String(Files.readAllBytes(Paths.get("settings.json")));
                writer.write(settings.toString());
                writer.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
        /* if the JSON file exists read it's content and initialize the settings */
        String JSONFileContent = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {
            JSONFileContent = reader.readLine();
        } catch (IOException e) { e.printStackTrace(); }
        settings = new JSONObject(JSONFileContent);
    }
}
