package editor.BasicEditor.Control;


import editor.BasicEditor.EditorState;
import editor.BasicEditor.Inputs.ClipboardManager;
import editor.Comand.*;

public class ContentEditor {
    private final EditorState state;
    private final ComandManager commandManager = new ComandManager();
    private final CursorHelper helper;

    public ContentEditor(EditorState state) {
        this.state = state;
        this.helper = new CursorHelper(state);
    }

    public void undo() {
        commandManager.undo();
        helper.clearSelection();
        helper.anchorCursor();
    }

    public void redo() {
        commandManager.redo();
        helper.clearSelection();
        helper.anchorCursor();
    }

    public void deleteSelectionOrSymbol() {
        if (state.hasSelection()) {
            int[] start = state.getSelectionStartCoordinates();
            int[] end = state.getSelectionEndCoordinates();
            String deletedText = state.getSelectedText();
    
            Comand delete = new DeleteComand(state, deletedText, start, end);
            commandManager.execute(delete);
    
            state.setCursorX(start[1]);
            state.setCursorY(start[0]);
            helper.clearSelection();
            delete.setAfterCoordAndOffsetY(new  int[] {state.getCursorX(), state.getCursorY()}, 
            state.getOffsetY());
        } else {
            deleteSymbol();
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
            helper.moveLeft();
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


    public void insertReplacingSelection(char ch) {
        if (state.hasSelection()) {
            deleteSelectionOrSymbol();
        }
        Comand insert = new InsertComand(state, String.valueOf(ch), state.getRawCoordinates());
        commandManager.execute(insert);
        if (ch == '\n') {
            helper.moveDown();
            state.setCursorX(0);
        } else {
            helper.moveRight();
        }
        insert.setAfterCoordAndOffsetY(new  int[] {state.getCursorX(), state.getCursorY()}, 
        state.getOffsetY());
    }


    public void pasteClipboard() {
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
        helper.clearSelection();
    }
    public void copy() {
        ClipboardManager.copyToClipboard(state.getSelectedText());
    }

    public void cut() {
        ClipboardManager.copyToClipboard(state.getSelectedText());
        deleteSelectionOrSymbol();
    }
}