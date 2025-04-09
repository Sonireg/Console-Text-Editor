package editor.Comand;

import java.util.List;

import editor.BasicEditor.EditorState;

public class InsertComand implements Comand {
    private final EditorState state;
    private final int[] start;
    private final String insertedContent;
    private final String[] lines;
    private int[] previousCursor;
    private int previousOffsetY;
    boolean wasExecuted = false;
    private int[] afterCursor;
    private int afterOffsetY;


    public InsertComand(EditorState state, String insertedContent, int[] position) {
        this.state = state;
        this.start = position.clone();
        this.insertedContent = insertedContent;
        this.lines = insertedContent.split("\\r?\\n|\\r", -1); // поддержка \n, \r и \r\n
        this.previousCursor = new  int[] {state.getCursorX(), state.getCursorY()};
        this.previousOffsetY = state.getOffsetY();
    }

    public InsertComand(EditorState state, String insertedContent, int[] start, int[] end) {
        this.state = state;
        this.insertedContent = insertedContent;
        this.start = start.clone(); // куда вставляется
        lines = insertedContent.split("\\r?\\n|\\r", -1); // поддержка \n, \r и \r\n
        this.previousCursor = new  int[] {state.getCursorX(), state.getCursorY()};
        this.previousOffsetY = state.getOffsetY();
    }

    @Override
    public void execute() {
        String[] lines = insertedContent.split("\n", -1);
        if (lines.length == 1) {
            state.getRawContent().get(start[0]).insert(start[1], insertedContent);
        } else {
            StringBuilder currentLine = state.getRawContent().get(start[0]);
            StringBuilder newFirst = new StringBuilder(currentLine.substring(0, start[1])).append(lines[0]);
            StringBuilder newLast = new StringBuilder(lines[lines.length - 1])
                .append(currentLine.substring(start[1]));
            state.getRawContent().set(start[0], newFirst);
            for (int i = 1; i < lines.length - 1; i++) {
                state.getRawContent().add(start[0] + i, new StringBuilder(lines[i]));
            }
            state.getRawContent().add(start[0] + lines.length - 1, newLast);
        }
        if (wasExecuted) {
            state.setCursorX(afterCursor[0]);
            state.setCursorY(afterCursor[1]);
            state.setOffsetY(afterOffsetY);
        }
        wasExecuted = true;
        state.updateContents();
    }

    @Override
    public void undo() {
        List<StringBuilder> content = state.getRawContent();

        if (lines.length == 1) {
            content.get(start[0]).delete(start[1], start[1] + insertedContent.length());
        } else {
            // Сохраняем суффикс из последней вставленной строки
            String suffix = content.get(start[0] + lines.length - 1).substring(lines[lines.length - 1].length());

            // Удаляем все добавленные строки, начиная с конца
            for (int i = lines.length - 1; i >= 1; i--) {
                content.remove(start[0] + i);
            }

            // Восстанавливаем первую строку
            StringBuilder firstLine = content.get(start[0]);
            firstLine.setLength(start[1]); // обрезаем до начала вставки
            firstLine.append(suffix); // добавляем сохранённый хвост
        }

        state.updateContents();
        state.setCursorX(previousCursor[0]);
        state.setCursorY(previousCursor[1]);
        state.setOffsetY(previousOffsetY);
    }

    @Override
    public void setAfterCoordAndOffsetY(int[] afterCursor, int afterOffsestY) {
        this.afterCursor = afterCursor;
        this.afterOffsetY = afterOffsestY;
    }
}
