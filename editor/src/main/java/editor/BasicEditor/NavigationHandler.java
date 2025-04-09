package editor.BasicEditor;

import java.util.List;

public class NavigationHandler {
    private final EditorState state;
    CursorHelper helper;

    public NavigationHandler(EditorState state) {
        this.state = state;
        this.helper = new CursorHelper(state);
    }

    public void moveUp() {
        helper.moveUp();
        helper.clearSelection();
    }

    public void moveDown() {
        helper.moveDown();
        helper.clearSelection();
    }

    public void moveLeft() {
        helper.moveLeft();
        helper.clearSelection();
    }

    public void moveRight() {
        helper.moveRight();
        helper.clearSelection();
    }


    public void selectLeft() {
        ensureSelectionStarted();
        int cursorX = state.getCursorX();
        int cursorY = state.getCursorY();
        if (cursorX > 0) {
            state.setCursorX(cursorX - 1);
        } else if (cursorY > 0) {
            state.setCursorY(cursorY - 1);
            state.setCursorX(state.getContent().get(state.getCursorY()).length());
        }
        anchorCursor();
        state.setSelectionEnd(state.getCursorX(), state.getCursorY());
    }
    

    public void selectRight() {
        ensureSelectionStarted();
        List<StringBuilder> content = state.getContent();
        int cursorX = state.getCursorX();
        int cursorY = state.getCursorY();
        StringBuilder line = cursorY < content.size() ? content.get(cursorY) : null;
    
        if (line != null && cursorX < line.length()) {
            state.setCursorX(cursorX + 1);
        } else if (cursorY < content.size() - 1) {
            state.setCursorY(cursorY + 1);
            state.setCursorX(0);
        }
        anchorCursor();
        state.setSelectionEnd(state.getCursorX(), state.getCursorY());
    }
    

    public void selectUp() {
        ensureSelectionStarted();
        int cursorY = state.getCursorY();
        if (cursorY > 0) {
            state.setCursorY(cursorY - 1);
        }
        anchorCursor();
        state.setSelectionEnd(state.getCursorX(), state.getCursorY());
    }
    

    public void selectDown() {
        ensureSelectionStarted();
        int cursorY = state.getCursorY();
        if (cursorY < state.getContent().size() - 1) {
            state.setCursorY(cursorY + 1);
        }
        anchorCursor();
        state.setSelectionEnd(state.getCursorX(), state.getCursorY());
    }


    private void ensureSelectionStarted() {
        if (state.getSelectionStartX() == state.getSelectionEndX() &&
            state.getSelectionStartY() == state.getSelectionEndY()) {
            state.setSelectionStart(state.getCursorX(), state.getCursorY());
        }
    }

    private void anchorCursor() {
        helper.anchorCursor();
    }
}