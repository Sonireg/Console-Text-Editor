package editor.User.Subscriprions;
import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class SubscriptionManager {
    private static final String FILE = "subscriptions.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, List<String>> subscriptions;

    public SubscriptionManager() {
        this.subscriptions = loadSubscriptions();
    }

    public List<String> getSubscribers(String fileName) {
        return subscriptions.getOrDefault(fileName, new ArrayList<>());
    }

    public List<String> getSubscriptions(String username) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : subscriptions.entrySet()) {
            if (entry.getValue().contains(username)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public void subscribe(String username, String fileName) {
        subscriptions.computeIfAbsent(fileName, k -> new ArrayList<>()).add(username);
        save();
    }

    public void unsubscribe(String username, String fileName) {
        subscriptions.getOrDefault(fileName, new ArrayList<>()).remove(username);
        save();
    }

    private Map<String, List<String>> loadSubscriptions() {
        try {
            if (!Files.exists(Path.of(FILE))) return new HashMap<>();
            String json = Files.readString(Path.of(FILE));
            Type type = new com.google.gson.reflect.TypeToken<Map<String, List<String>>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    private void save() {
        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(subscriptions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
