package editor;

import java.util.List;

import editor.Comand.*;
import editor.TerminalSettings.Terminal;
import editor.ClipboardManager;

public class StateModifier {
    private final EditorState state;
    private static final int ARROW_UP = 65;
    private static final int ARROW_DOWN = 66;
    private static final int ARROW_LEFT = 68;
    private static final int ARROW_RIGHT = 67;
    private static final int BACKSPACE = 127;
    private static final int UNDO = -1; //ctrl+z
    private static final int REDO = 21; //ctrl+u
    private static final int SELECTION_UP = 1065; //shift+up
    private static final int SELECTION_DOWN = 1066;
    private static final int SELECTION_LEFT = 1068;
    private static final int SELECTION_RIGHT = 1067;
    private static final int COPY = 3;    //ctrl+c
    private static final int PASTE = 22; //ctrl+v
    private static final int CUT = 24; //ctrl+x


    ComandManager commandManager = new ComandManager();



    public StateModifier(EditorState state, Terminal terminal) {
        this.state = state;
    }

    public void handleCommand(int command) {
        switch (command) {
            case ARROW_UP -> {
                moveUp();
                clearSelection();
            }
            case ARROW_DOWN -> {
                moveDown();
                clearSelection();
            }
            case ARROW_LEFT -> {
                moveLeft();
                clearSelection();
            }
            case ARROW_RIGHT -> {
                moveRight();
                clearSelection();
            }
            case BACKSPACE -> deleteSelectionOrSymbol(); // NEW
            case UNDO -> {
                commandManager.undo();
                clearSelection();
                anchorCursor();
            }
            case REDO -> {
                commandManager.redo();
                clearSelection();
                anchorCursor();
            }
            case SELECTION_LEFT -> selectLeft();
            case SELECTION_RIGHT -> selectRight();
            case SELECTION_UP -> selectUp();
            case SELECTION_DOWN -> selectDown();
            case COPY -> ClipboardManager.copyToClipboard(state.getSelectedText()); // already fine
            case PASTE -> pasteClipboard(); // NEW
            case CUT -> {
                ClipboardManager.copyToClipboard(state.getSelectedText());
                deleteSelectionOrSymbol();
            }
            default -> {
                insertReplacingSelection((char) command); // NEW
                clearSelection();
            }
        }
    }
    
     
    private void moveUp() {
        int cursorY = state.getCursorY();
        if (cursorY > 0) state.setCursorY(cursorY - 1);
        anchorCursor();
    }

    private void moveDown() {
        int cursorY = state.getCursorY();
        if (cursorY < state.getContent().size() - 1) state.setCursorY(cursorY + 1);
        anchorCursor();
    }

    private void moveLeft() {
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

    private void moveRight() {
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

    private void anchorCursor() {
        int cursorY = state.getCursorY();
        int lineLength = (cursorY < state.getContent().size()) 
            ? state.getContent().get(cursorY).length() 
            : 0;
        if (state.getCursorX() > lineLength) {
            state.setCursorX(lineLength);
        }
    }
    
    private void deleteSymbol() {
        int[] pos = state.getRawCoordinates();
    
        if (pos[0] == 0 && pos[1] == 0) return;
    
        String deletedContent;
        int[] start;
        int[] end = pos.clone();
    
        if (pos[1] != 0) {
            // удаление символа внутри строки
            start = new int[] {pos[0], pos[1] - 1};
            deletedContent = String.valueOf(state.getRawContent().get(pos[0]).charAt(pos[1] - 1));
            moveLeft();
        } else {
            // удаление переноса строки
            int prevLine = pos[0] - 1;
            int prevLen = state.getRawContent().get(prevLine).length();
            start = new int[] {prevLine, prevLen};
            deletedContent = "\n";
            state.setCursorY(prevLine);
            state.setCursorX(prevLen);
        }
    
        Comand delete = new DeleteComand(state, deletedContent, start, end);
        commandManager.execute(delete);
        delete.setAfterCoordAndOffsetY(new  int[] {state.getCursorX(), state.getCursorY()}, 
            state.getOffsetY());
    }

    private void clearSelection() {
        state.setSelectionStart(state.getCursorX(), state.getCursorY());
        state.setSelectionEnd(state.getCursorX(), state.getCursorY());
    }


    private void selectLeft() {
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
    

    private void selectRight() {
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
    

    private void selectUp() {
        ensureSelectionStarted();
        int cursorY = state.getCursorY();
        if (cursorY > 0) {
            state.setCursorY(cursorY - 1);
        }
        anchorCursor();
        state.setSelectionEnd(state.getCursorX(), state.getCursorY());
    }
    

    private void selectDown() {
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

    private void deleteSelectionOrSymbol() {
        if (state.hasSelection()) {
            int[] start = state.getSelectionStartCoordinates();
            int[] end = state.getSelectionEndCoordinates();
            String deletedText = state.getSelectedText();
    
            Comand delete = new DeleteComand(state, deletedText, start, end);
            commandManager.execute(delete);
    
            state.setCursorX(start[1]);
            state.setCursorY(start[0]);
            clearSelection();
            delete.setAfterCoordAndOffsetY(new  int[] {state.getCursorX(), state.getCursorY()}, 
            state.getOffsetY());
        } else {
            deleteSymbol();
        }
    }
    
    private void insertReplacingSelection(char ch) {
        if (state.hasSelection()) {
            deleteSelectionOrSymbol();
        }
        Comand insert = new InsertComand(state, String.valueOf(ch), state.getRawCoordinates());
        commandManager.execute(insert);
        if (ch == '\n') {
            moveDown();
            state.setCursorX(0);
        } else {
            moveRight();
        }
        insert.setAfterCoordAndOffsetY(new  int[] {state.getCursorX(), state.getCursorY()}, 
        state.getOffsetY());
    }
    
    private void pasteClipboard() {
        String pasteText = ClipboardManager.pasteFromClipboard();
        if (pasteText == null || pasteText.isEmpty()) return;
    
        if (state.hasSelection()) {
            deleteSelectionOrSymbol();
        }
    
        Comand insert = new InsertComand(state, pasteText, state.getRawCoordinates());
        commandManager.execute(insert);

        int lines = pasteText.split("\n", -1).length - 1;
        if (lines == 0) {
            state.setCursorX(state.getCursorX() + pasteText.length());
        } else {
            String[] split = pasteText.split("\n", -1);
            state.setCursorY(state.getCursorY() + lines);
            state.setCursorX(split[split.length - 1].length());
        }
        insert.setAfterCoordAndOffsetY(new  int[] {state.getCursorX(), state.getCursorY()}, 
        state.getOffsetY());
        clearSelection();
    }
}