package editor.BasicEditor.Saving.SaveStrategy;

import java.io.IOException;
import java.util.List;

public interface StorageStrategy {
    void save(String fileName, List<StringBuilder> content) throws IOException;
}
