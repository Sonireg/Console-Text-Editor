package editor.BasicEditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import com.google.gson.*;

public class FileManager {
    private static final String PERMISSIONS_PATH = "permissions.json";

    public static boolean deleteFile(String fileName) {
        boolean deleted = false;

        // Удаление файла с диска
        File file = new File(fileName);
        if (file.exists()) {
            deleted = file.delete();
            if (!deleted) {
                System.out.println("Unable to delete file: " + fileName);
                return false;
            }
        } else {
            System.out.println("File not found: " + fileName);
        }

        // Удаление из permissions.json
        try {
            Path permissionsPath = Paths.get(PERMISSIONS_PATH);
            if (!Files.exists(permissionsPath)) return deleted;

            String json = Files.readString(permissionsPath, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            if (root.has(fileName)) {
                root.remove(fileName);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Files.writeString(permissionsPath, gson.toJson(root), StandardCharsets.UTF_8);
                System.out.println("Note about file deleted from permissions.json.");
            }

        } catch (IOException e) {
            System.out.println("Error while updating permissions.json");
            e.printStackTrace();
        }

        return deleted;
    }
}

