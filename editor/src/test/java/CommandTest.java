
import editor.BasicEditor.EditorState;
import editor.Comand.DeleteComand;
import editor.Comand.InsertComand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class CommandTest {
    
    private EditorState state;

    @BeforeEach
    void setup() {
        state = new EditorState();
        state.setRawContent(new java.util.ArrayList<>(List.of(
            new StringBuilder("Line 1"),
            new StringBuilder("Line 2")
        )));
    }
    
    @Test
    void testInsertCommand() {
        InsertComand cmd = new InsertComand(state, "X", new int[]{0, 3});
        cmd.execute();
        
        assertEquals("LinXe 1", state.getRawContent().get(0).toString());
        
        cmd.undo();
        assertEquals("Line 1", state.getRawContent().get(0).toString());
    }
    
    @Test
    void testDeleteCommand() {
        state.setCursorX(3);
        state.setCursorY(0);
        
        DeleteComand cmd = new DeleteComand(state, "e", new int[]{0, 3}, new int[]{0, 4});
        cmd.execute();
        
        assertEquals("Lin 1", state.getRawContent().get(0).toString());
        
        cmd.undo();
        assertEquals("Line 1", state.getRawContent().get(0).toString());
    }
}