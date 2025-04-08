package editor;

import java.util.List;

import editor.Comand.*;
import editor.TerminalSettings.Terminal;

public class StateModifier {
    private final EditorState state;
    private static final int ARROW_UP = 65;
    private static final int ARROW_DOWN = 66;
    private static final int ARROW_LEFT = 68;
    private static final int ARROW_RIGHT = 67;
    private static final int BACKSPACE = 127;
    private static final int UNDO = -1;
    private static final int REDO = 21;
    private static final int NEW_LINE = 13;
    

    ComandManager commandManager = new ComandManager();

    public StateModifier(EditorState state, Terminal terminal) {
        this.state = state;
    }

    public void handleCommand(int command) {
        switch (command) {
            case ARROW_UP -> moveUp();
            case ARROW_DOWN -> moveDown();
            case ARROW_LEFT -> moveLeft();
            case ARROW_RIGHT -> moveRight();
            case BACKSPACE -> deleteSymbol();
            case UNDO -> commandManager.undo();
            case REDO -> commandManager.redo();
            default -> { addSymbol(command); }
        }
    }

    private void moveUp() {
        int cursorY = state.getCursorY();
        if (cursorY > 0) state.setCursorY(cursorY - 1);
        anchorCursor();
    }

    private void moveDown() {
        int cursorY = state.getCursorY();
        if (cursorY + state.getOffsetY() >= state.getContent().size() - 1){
            return;
        }
        if (cursorY < state.getContent().size()) state.setCursorY(cursorY + 1);
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

    private void addSymbol(int pressedButton) {
        Comand newComand = new InsertComand(state, 
                                            String.valueOf((char) pressedButton), 
                                            state.getRawCoordinates());
        commandManager.execute(newComand);
        if (pressedButton != NEW_LINE) {
            moveRight();
        }
        else {
            moveDown();
            state.setCursorX(0);
        }
    }
    
    private void deleteSymbol() {
        int[] deletionPos = state.getRawCoordinates();
        if (deletionPos[0] == 0 && deletionPos[1] == 0) {
            return;
        }
        String deletedContent = "\n";
        if (deletionPos[1] != 0) {
            deletedContent = 
            String.valueOf(state.getRawContent().get(deletionPos[0]).charAt(deletionPos[1] - 1));
        }
        Comand newComand = new DeleteComand(state, 
                                            deletedContent, 
                                            deletionPos);
        
        if (deletedContent != "\n") {
            moveLeft();
            commandManager.execute(newComand);
            return;
        }
        
        
        state.setCursorX(state.getContent().get(state.getOffsetY()+state.getCursorY()).length());
        moveUp();
        commandManager.execute(newComand);
        
    }

    
}