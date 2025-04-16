package editor.BasicEditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import editor.BasicEditor.Inputs.Keys;
import editor.BasicEditor.Inputs.RawInputHandler;
import editor.BasicEditor.Saving.SaveManager;
import editor.BasicEditor.Saving.SaveOptions;
import editor.Parsers.TXTPArser;
import editor.TerminalSettings.Terminal;
import editor.TerminalSettings.TerminalSize;
import editor.TerminalSettings.WindowsTerminal;
import editor.Viewer.View;
import editor.TerminalSettings.TerminalSettings;

public class EditorViewer {
    public static void viewCycle(String filePath) throws IOException {
        EditorState state = new EditorState();
        Terminal terminal = new WindowsTerminal();
        TerminalSettings settings = TerminalSettings.GetInstance();
        RawInputHandler inputHandler = new RawInputHandler();
        EditorCommandDispatcher dispatcher = new EditorCommandDispatcher(state, terminal);

        state.setParser(new TXTPArser());
        state.setFilePath(filePath);
        state.setMaxLength(terminal.getTerminalSize().width() - 5);
        terminal.enableRawMode();
        openFile(state, filePath);
        initEditor(state, terminal);

        View view = new View(state, terminal);

        Boolean fileIsOpened = true;
        while (fileIsOpened) {
            view.refreshScreen();
            int command = inputHandler.handleInput();
            switch (command) {
                case Keys.EXIT -> {
                    exit();
                    fileIsOpened = false;
                    terminal.disableRawMode();
                }
                case Keys.SAVEFILE -> saveFile(state);
                case Keys.THEMECHANGE -> settings.toggleTheme();
                default -> dispatcher.handleCommand(command);
            }
            // System.out.println(command);
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

    private static void saveFile(EditorState state) throws IOException {
        SaveOptions options = promptSaveOptions(new WindowsTerminal(), state.getFilePath());
        if (options == null) return;

        SaveManager saveManager = new SaveManager();
        saveManager.save(state, options);
    }

    private static SaveOptions promptSaveOptions(Terminal terminal, String currentFilePath) throws IOException {
        System.out.print("\033[2J\033[H");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        terminal.disableRawMode();
    
        System.out.println("Введите название нового файла: ");
        String newName = reader.readLine().trim();
    
        System.out.println("Выберите формат сохранения (txt, md, rtf, json, xml): ");
        String formatStr = reader.readLine().trim().toUpperCase();
    
        System.out.println("Куда сохранить файл? Введите 'local' или 'cloud': ");
        String storageStr = reader.readLine().trim().toUpperCase();
    
        terminal.enableRawMode();
    
        SaveOptions.Format format;
        try {
            format = SaveOptions.Format.valueOf(formatStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный формат. Сохранение отменено.");
            return null;
        }
    
        SaveOptions.StorageType storageType;
        try {
            storageType = SaveOptions.StorageType.valueOf(storageStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный тип хранилища. Сохранение отменено.");
            return null;
        }
    
        return new SaveOptions(currentFilePath, format, newName, storageType);
    }
}