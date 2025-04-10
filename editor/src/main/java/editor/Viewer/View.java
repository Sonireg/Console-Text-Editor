package editor.Viewer;

import java.util.List;

import editor.BasicEditor.EditorState;
import editor.TerminalSettings.Terminal;
import editor.TerminalSettings.TerminalSettings;

public class View {
    private final EditorState state;

    public View(EditorState state, Terminal terminal) {
        this.state = state;
    }

    public void refreshScreen() {
        adjustScroll();
        StringBuilder builder = new StringBuilder();
        moveCursorToTopLeft(builder);
        drawContent(builder);
        drawStatusBar(builder);
        drawCursor(builder);
        System.out.print(builder);
    }

    private void adjustScroll() {
        int cursorY = state.getCursorY();
        int rows = state.getRows();
        int offsetY = state.getOffsetY();

        if (cursorY >= rows + offsetY) {
            state.setOffsetY(cursorY - rows + 1);
        } else if (cursorY < offsetY) {
            state.setOffsetY(cursorY);
        }
    }

    private void moveCursorToTopLeft(StringBuilder builder) {
        builder.append("\033[H");
    }

    private void drawCursor(StringBuilder builder) {
        int cursorY = state.getCursorY() - state.getOffsetY() + 1;
        int cursorX = state.getCursorX() + 1;
        builder.append(String.format("\033[%d;%dH", cursorY, cursorX));
    }

    private void drawStatusBar(StringBuilder builder) {
        String statusMessage = String.format("Rows: %d X: %d Y: %d Offset Y: %d",
                state.getRows(), state.getCursorX(), state.getCursorY(), state.getOffsetY());
        int columns = state.getColumns();
        builder.append("\033[7m")
                .append(statusMessage)
                .append(" ".repeat(Math.max(0, columns - statusMessage.length())))
                .append("\033[0m");
    }

    private void drawContent(StringBuilder builder) {
        List<StringBuilder> content = state.getContent();
        int offsetY = state.getOffsetY();
        int offsetX = state.getOffsetX();
        int rows = state.getRows();
        int columns = state.getColumns();

        int startX = state.getSelectionStartX();
        int startY = state.getSelectionStartY();
        int endX = state.getSelectionEndX();
        int endY = state.getSelectionEndY();

        // Нормализуем порядок
        if (startY > endY || (startY == endY && startX > endX)) {
            int tmpX = startX, tmpY = startY;
            startX = endX;
            startY = endY;
            endX = tmpX;
            endY = tmpY;
        }
        builder.append(TerminalSettings.GetInstance().getTheme().apply());
        for (int i = 0; i < rows; i++) {
            int fileI = offsetY + i;
            if (fileI >= content.size()) {
                builder.append("~");
            } else {
                StringBuilder line = content.get(fileI);
                int lineLength = line.length();
                int from = offsetX;
                int to = Math.min(columns + offsetX, lineLength);

                for (int j = from; j < to; j++) {
                    boolean selected = false;

                    if (fileI > startY && fileI < endY) {
                        selected = true;
                    } else if (fileI == startY && fileI == endY) {
                        selected = (j >= startX && j < endX);
                    } else if (fileI == startY) {
                        selected = (j >= startX);
                    } else if (fileI == endY) {
                        selected = (j < endX);
                    }
                    
                    if (selected) builder.append("\033[7m");
                    builder.append(line.charAt(j));
                    if (selected) builder.append("\033[0m");
                }
            }
            builder.append("\033[K\r\n");
            
        }
    }
}