package editor.BasicEditor.Saving;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class HistoryManager {
    private static final String HISTORY_DIR = "History";

    public static void addSnapshot(String fileName, List<StringBuilder> content) {
        try {
            Files.createDirectories(Paths.get(HISTORY_DIR));
            String historyFilePath = getHistoryFilePath(fileName);

            Map<Integer, String> history = readHistory(historyFilePath);
            int nextVersion = history.size() + 1;

            StringBuilder snapshot = new StringBuilder();
            for (StringBuilder line : content) {
                snapshot.append(line).append("\n");
            }

            history.put(nextVersion, snapshot.toString());

            try (Writer writer = new FileWriter(historyFilePath)) {
                new Gson().toJson(history, writer);
            }

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении истории: " + e.getMessage());
        }
    }

    private static Map<Integer, String> readHistory(String path) {
        try (Reader reader = new FileReader(path)) {
            Type type = new TypeToken<Map<Integer, String>>() {}.getType();
            Map<Integer, String> history = new Gson().fromJson(reader, type);
            return history != null ? history : new TreeMap<>();
        } catch (IOException e) {
            return new TreeMap<>();
        }
    }

    private static String getHistoryFilePath(String fileName) {
        String baseName = Paths.get(fileName).getFileName().toString();
        return HISTORY_DIR + File.separator + baseName + "HISTORY.json";
    }

    public static int getSnapshotCount(String fileName) {
        String historyPath = getHistoryFilePath(fileName);
        return readHistory(historyPath).size();
    }
    
    public static String getSnapshot(String fileName, int version) {
        String historyPath = getHistoryFilePath(fileName);
        Map<Integer, String> history = readHistory(historyPath);
        return history.getOrDefault(version, null);
    }
}
