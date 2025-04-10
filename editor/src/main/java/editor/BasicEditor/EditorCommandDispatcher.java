package editor.BasicEditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import editor.BasicEditor.Control.ContentEditor;
import editor.BasicEditor.Control.NavigationHandler;
import editor.BasicEditor.Inputs.Keys;
import editor.TerminalSettings.Terminal;

public class EditorCommandDispatcher {
    private final ContentEditor editingHandler;
    private final NavigationHandler navigationHandler;
    Terminal terminal;

    public EditorCommandDispatcher(EditorState state, Terminal terminal) {
        this.editingHandler = new ContentEditor(state);
        this.navigationHandler = new NavigationHandler(state);
        this.terminal = terminal;
    }

    public void handleCommand(int command) {
        switch (command) {
            case Keys.ARROW_UP -> navigationHandler.moveUp();
            case Keys.ARROW_DOWN -> navigationHandler.moveDown();
            case Keys.ARROW_LEFT -> navigationHandler.moveLeft();
            case Keys.ARROW_RIGHT -> navigationHandler.moveRight();
            case Keys.SELECTION_LEFT -> navigationHandler.selectLeft();
            case Keys.SELECTION_RIGHT -> navigationHandler.selectRight();
            case Keys.SELECTION_UP -> navigationHandler.selectUp();
            case Keys.SELECTION_DOWN -> navigationHandler.selectDown();
            case Keys.BACKSPACE -> editingHandler.deleteSelectionOrSymbol();
            case Keys.UNDO -> editingHandler.undo();
            case Keys.REDO -> editingHandler.redo();
            case Keys.COPY -> editingHandler.copy();
            case Keys.PASTE -> editingHandler.pasteClipboard();
            case Keys.CUT -> editingHandler.cut();
            case Keys.FIND -> handleSearch();
            default -> editingHandler.insertReplacingSelection((char) command);
        }
    }

    private void handleSearch() {
        try {
            terminal.disableRawMode();
            System.out.print("\033[2J\033[H");
            System.out.println("Enter search text (or press Enter to cancel):");

            // Считываем ввод с консоли
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String searchText = reader.readLine();

            // Включаем обратно режим rawMode
            terminal.enableRawMode();

            if (searchText != null && !searchText.isEmpty()) {
                // Если текст для поиска введен, выполняем поиск
                navigationHandler.search(searchText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}