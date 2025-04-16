package editor.User.Subscriprions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class NotificationStorage {
    private static final String FOLDER = "notifications/";
    private final Gson gson = new Gson();

    public NotificationStorage() {
        try {
            Files.createDirectories(Path.of(FOLDER));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNotification(String username, String message) throws IOException {
        List<String> notifications = loadNotifications(username);
        notifications.add(message);
        saveNotifications(username, notifications);
    }

    public List<String> loadNotifications(String username) throws IOException {
        Path path = Path.of(FOLDER + username + ".json");
        if (!Files.exists(path)) return new ArrayList<>();
        String json = Files.readString(path);
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    public void clearNotifications(String username) throws IOException {
        Files.deleteIfExists(Path.of(FOLDER + username + ".json"));
    }

    private void saveNotifications(String username, List<String> notifications) throws IOException {
        try (Writer writer = new FileWriter(FOLDER + username + ".json")) {
            gson.toJson(notifications, writer);
        }
    }
}

