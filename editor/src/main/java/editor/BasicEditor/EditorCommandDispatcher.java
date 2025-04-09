package editor.BasicEditor;

public class EditorCommandDispatcher {
    private final ContentEditor editingHandler;
    private final NavigationHandler navigationHandler;

    private static final int ARROW_UP = 65;
    private static final int ARROW_DOWN = 66;
    private static final int ARROW_LEFT = 68;
    private static final int ARROW_RIGHT = 67;
    private static final int BACKSPACE = 127;
    private static final int UNDO = -1;
    private static final int REDO = 21;
    private static final int SELECTION_UP = 1065;
    private static final int SELECTION_DOWN = 1066;
    private static final int SELECTION_LEFT = 1068;
    private static final int SELECTION_RIGHT = 1067;
    private static final int COPY = 3;
    private static final int PASTE = 22;
    private static final int CUT = 24;

    public EditorCommandDispatcher(EditorState state) {
        this.editingHandler = new ContentEditor(state);
        this.navigationHandler = new NavigationHandler(state);
    }

    public void handleCommand(int command) {
        switch (command) {
            case ARROW_UP -> navigationHandler.moveUp();
            case ARROW_DOWN -> navigationHandler.moveDown();
            case ARROW_LEFT -> navigationHandler.moveLeft();
            case ARROW_RIGHT -> navigationHandler.moveRight();
            case SELECTION_LEFT -> navigationHandler.selectLeft();
            case SELECTION_RIGHT -> navigationHandler.selectRight();
            case SELECTION_UP -> navigationHandler.selectUp();
            case SELECTION_DOWN -> navigationHandler.selectDown();
            case BACKSPACE -> editingHandler.deleteSelectionOrSymbol();
            case UNDO -> editingHandler.undo();
            case REDO -> editingHandler.redo();
            case COPY -> editingHandler.copy();
            case PASTE -> editingHandler.pasteClipboard();
            case CUT -> editingHandler.cut();
            default -> editingHandler.insertReplacingSelection((char) command);
        }
    }
}