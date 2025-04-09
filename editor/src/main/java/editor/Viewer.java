package editor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import editor.BasicEditor.EditorCommandDispatcher;
import editor.BasicEditor.EditorState;
import editor.TerminalSettings.Terminal;
import editor.TerminalSettings.TerminalSize;
import editor.TerminalSettings.WindowsTerminal;
import editor.TerminalSettings.TerminalSettings;

import editor.BasicEditor.Keys;

public class Viewer {
    


    public static void main(String[] args) throws IOException {
        EditorState state = new EditorState();
        Terminal terminal = new WindowsTerminal();
        TerminalSettings settings = TerminalSettings.GetInstance();
        InputHandler inputHandler = new InputHandler();

        EditorCommandDispatcher dispatcher = new EditorCommandDispatcher(state, terminal);

        state.setMaxLength(terminal.getTerminalSize().width() - 5);
        terminal.enableRawMode();
        initEditor(state, terminal);

        View view = new View(state, terminal);
        handleNewFile(state, terminal, view);

        while (true) {
            view.refreshScreen();
            
            int command = inputHandler.handleInput();
            switch (command) {
                case Keys.EXIT -> exit();
                case Keys.OPENFILE -> handleNewFile(state, terminal, view);
                case Keys.SAVEFILE -> saveFile(state);
                case Keys.THEMECHANGE -> settings.toggleTheme();
                default -> dispatcher.handleCommand(command);
            }
            // System.out.println(command);
        }
    }

    private static void handleNewFile(EditorState state, Terminal terminal, View view) throws IOException {
        terminal.disableRawMode();
        System.out.print("\033[2J\033[H");
        System.out.println("Enter file path (or press Enter to cancel):");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String filePath = reader.readLine();
        terminal.enableRawMode();
        if (filePath != null && !filePath.isEmpty()) {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            openFile(state, filePath);
        }
        initEditor(state, terminal);
        state.setFilePath(filePath);
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

    private static void saveFile(EditorState state) throws IOException {
        File file = new File(state.getFilePath());
        
        // Проверяем, существует ли файл и если нет, создаем его
        if (!file.exists()) {
            file.createNewFile();
        }

        // Используем BufferedWriter для записи в файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (StringBuilder line : state.getRawContent()) {
                writer.write(line.toString()); // Записываем строку
                writer.newLine(); // Переход на новую строку
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
        System.exit(0);
    }
}