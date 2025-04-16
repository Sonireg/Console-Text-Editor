package editor.BasicEditor.Saving.SaveStrategy;

import java.io.*;
import java.util.List;

public class LocalStorageStrategy implements StorageStrategy {
    @Override
    public void save(String fileName, List<StringBuilder> content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (StringBuilder line : content) {
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }
}
