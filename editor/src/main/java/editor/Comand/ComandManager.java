package editor.Comand;

import java.util.*;

public class ComandManager {
    private final Stack<Comand> undoStack = new Stack<>();
    private final Stack<Comand> redoStack = new Stack<>();

    public void execute(Comand cmd) {
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Comand cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Comand cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        }
    }
}