package editor.BasicEditor.Control;

import java.util.List;

import editor.BasicEditor.EditorState;

public class CursorHelper {
    private final EditorState state;

    public CursorHelper(EditorState state) {
        this.state = state;
    }

    public void moveUp() {
        int cursorY = state.getCursorY();
        if (cursorY > 0) state.setCursorY(cursorY - 1);
        anchorCursor();
    }

    public void moveDown() {
        int cursorY = state.getCursorY();
        if (cursorY < state.getContent().size() - 1) state.setCursorY(cursorY + 1);
        anchorCursor();
    }

    public void moveLeft() {
        int cursorX = state.getCursorX();
        int cursorY = state.getCursorY();
        if (cursorX > 0) {
            state.setCursorX(cursorX - 1);
        } else if (cursorY > 0) {
            state.setCursorY(cursorY - 1);
            state.setCursorX(state.getContent().get(cursorY - 1).length());
        }
        anchorCursor();
    }

    public void moveRight() {
        List<StringBuilder> content = state.getContent();
        int cursorX = state.getCursorX();
        int cursorY = state.getCursorY();
        StringBuilder line = cursorY < content.size() ? content.get(cursorY) : null;

        if (line != null && cursorX < line.length()) {
            state.setCursorX(cursorX + 1);
        } else if (cursorY < content.size()) {
            state.setCursorY(cursorY + 1);
            state.setCursorX(0);
        }
        anchorCursor();
    }

    public void anchorCursor() {
        int cursorY = state.getCursorY();
        int lineLength = (cursorY < state.getContent().size()) 
            ? state.getContent().get(cursorY).length() 
            : 0;
        if (state.getCursorX() > lineLength) {
            state.setCursorX(lineLength);
        }
    }

    public void clearSelection() {
        state.setSelectionStart(state.getCursorX(), state.getCursorY());
        state.setSelectionEnd(state.getCursorX(), state.getCursorY());
    }
}