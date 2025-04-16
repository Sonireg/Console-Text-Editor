import org.junit.jupiter.api.Test;

import editor.BasicEditor.Saving.HistoryManager;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class HistoryManagerTest {
    @Test
    void testHistorySnapshot() {
        String fileName = "test.txt";
        List<StringBuilder> content = List.of(new StringBuilder("Test content"));
        
        HistoryManager.addSnapshot(fileName, content);
        int count = HistoryManager.getSnapshotCount(fileName);
        
        assertTrue(count > 0);
        assertNotNull(HistoryManager.getSnapshot(fileName, 1));
    }
}