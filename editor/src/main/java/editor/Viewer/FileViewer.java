package editor.Viewer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import editor.TerminalSettings.Terminal;
import editor.TerminalSettings.TerminalSettings;
import editor.TerminalSettings.TerminalSize;
import editor.TerminalSettings.WindowsTerminal;
import editor.BasicEditor.EditorState;
import editor.BasicEditor.Inputs.RawInputHandler;
import editor.Parsers.Parser;
import editor.BasicEditor.Inputs.Keys;


public class FileViewer {
    
    public static void viewCycle(String filePath, Parser parser) throws IOException {
        EditorState state = new EditorState();
        Terminal terminal = new WindowsTerminal();
        TerminalSettings settings = TerminalSettings.GetInstance();
        state.setMaxLength(terminal.getTerminalSize().width() - 5);
        terminal.enableRawMode();

        state.setParser(parser);
        openFile(state, filePath);
        initEditor(state, terminal);

        View view = new View(state, terminal);
        RawInputHandler inputHandler = new RawInputHandler();
        ViewCommandDispatcher dispatcher = new ViewCommandDispatcher(state, terminal);

        boolean fileIsOpened = true;
        while (fileIsOpened) {
            view.refreshScreen();
            int command = inputHandler.handleInput();

            switch (command) {
                case Keys.EXIT -> {
                    exit();
                    fileIsOpened = false;
                    terminal.disableRawMode();
                }
                case Keys.THEMECHANGE -> settings.toggleTheme();
                default -> dispatcher.handleCommand(command);
            }
        }
    }

    private static void openFile(EditorState state, String inpath) {
        Path path = Path.of(inpath);
        if (Files.isRegularFile(path)) {
            try (Stream<String> stream = Files.lines(path)) {
                state.setRawContent(new ArrayList<>(stream.map(StringBuilder::new).toList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initEditor(EditorState state, Terminal terminal) {
        TerminalSize size = terminal.getTerminalSize();
        state.setColumns(size.width());
        state.setRows(size.height() - 1);
    }

    private static void exit() {
        System.out.print("\033[2J\033[H");
    }
}