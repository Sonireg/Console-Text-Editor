package editor.Viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import editor.BasicEditor.Control.NavigationHandler;
import editor.BasicEditor.Inputs.Keys;
import editor.TerminalSettings.Terminal;
import editor.TerminalSettings.WindowsTerminal;
import editor.BasicEditor.EditorState;


public class ViewCommandDispatcher {
    private final NavigationHandler navigationHandler;
    Terminal terminal = new WindowsTerminal();

    public ViewCommandDispatcher(EditorState state, Terminal terminal) {
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
            case Keys.FIND -> handleSearch();
            default -> {
                // В режиме только для просмотра все остальное игнорируем
            }
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