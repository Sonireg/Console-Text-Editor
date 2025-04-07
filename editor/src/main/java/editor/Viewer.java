package editor;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

import editor.TerminalSettings.Terminal;
import editor.TerminalSettings.TerminalSize;
import editor.TerminalSettings.WindowsTerminal;

public class Viewer {
    public static void main(String[] args) throws IOException {
        EditorState state = new EditorState();
        Terminal terminal = new WindowsTerminal();
        InputHandler inputHandler = new InputHandler();
        StateModifier stateModifier = new StateModifier(state, terminal); 
        state.setTerminal(terminal);
        terminal.enableRawMode();
        initEditor(state);

        View view = new View(state, terminal);
        handleNewFile(state, terminal, view);

        while (true) {
            view.refreshScreen();
            int command = inputHandler.handleInput();
            if (command == 14) {
                handleNewFile(state, terminal, view);
                continue;
            }
            stateModifier.handleCommand(command);
        }
    }

    private static void handleNewFile(EditorState state, Terminal terminal, View view) throws IOException {
        // Очищаем буфер ввода перед отключением raw mode
        while(System.in.available() > 0) {
            System.in.read();
        }
        
        // Выходим из raw mode для нормального ввода
        terminal.disableRawMode();
        
        // Очищаем экран и запрашиваем путь
        System.out.print("\033[2J\033[H");
        System.out.println("Enter file path (or press Enter to cancel):");
        
        // Читаем ввод пользователя с помощью System.console()
        String filePath = null;
        Console console = System.console();
        if (console != null) {
            filePath = console.readLine().trim();
        } else {
            // Fallback для IDE, где System.console() возвращает null
            Scanner scanner = new Scanner(System.in);
            filePath = scanner.nextLine().trim();
        }
        
        // Очищаем буфер ввода перед возвратом в raw mode
        while(System.in.available() > 0) {
            System.in.read();
        }
        
        // Возвращаем raw mode
        terminal.enableRawMode();
        
        // Если пользователь ввел путь
        if (filePath != null && !filePath.isEmpty()) {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            openFile(state, filePath);
        }
        
        // Обновляем размер терминала
        initEditor(state);
    }

    private static void openFile(EditorState state, String inpath) {
        Path path = Path.of(inpath);
        if (Files.isRegularFile(path)) {
            try (Stream<String> stream = Files.lines(path)) {
                state.setRawContent(stream.map(StringBuilder::new).toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initEditor(EditorState state) {
        TerminalSize size = state.getTerminal().getTerminalSize();
        state.setColumns(size.width());
        state.setRows(size.height() - 1);
    }
}