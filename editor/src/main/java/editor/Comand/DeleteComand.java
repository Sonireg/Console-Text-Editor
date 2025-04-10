package editor.Comand;

import java.util.List;

import editor.BasicEditor.EditorState;

public class DeleteComand implements Comand {
    private EditorState state;
    private int[] start;
    private int[] end;
    private String deletedContent;
    private int[] previousCursor;
    private int previousOffsetY;
    boolean wasExecuted = false;
    private int[] afterCursor;
    private int afterOffsetY;

    public DeleteComand(EditorState state, String deletedContent, int[] position) {
        this(state, deletedContent, position, position);
        this.previousCursor = new  int[] {state.getCursorX() + 1, state.getCursorY()};
        this.previousOffsetY = state.getOffsetY();
    }

    public DeleteComand(EditorState state, String deletedContent, int[] start, int[] end) {        this.state = state;
        this.start = start;
        this.end = end;
        this.deletedContent = deletedContent;
        this.previousCursor = new  int[] {state.getCursorX() + 1, state.getCursorY()};
        this.previousOffsetY = state.getOffsetY();
    }

    @Override
    public void execute() {
        List<StringBuilder> content = state.getRawContent();

        if (start[0] == end[0]) {
            if (start[1] < end[1]) {
                content.get(start[0]).delete(start[1], end[1]);
            } else if (start[1] > 0) {
                content.get(start[0]).delete(start[1] - deletedContent.length(), start[1]);
            }
        } else {
            // многострочный удалённый диапазон (в том числе перенос строки)
            StringBuilder firstLine = content.get(start[0]);
            StringBuilder lastLine = content.get(end[0]);

            String prefix = firstLine.substring(0, start[1]);
            String suffix = lastLine.substring(end[1]);

            for (int i = end[0]; i > start[0]; i--) {
                content.remove(i);
            }

            content.get(start[0]).setLength(0);
            content.get(start[0]).append(prefix).append(suffix);
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
        if (start[0] == end[0]) {
            content.get(start[0]).insert(start[1], deletedContent);
        } else {
            String[] lines = deletedContent.split("\n", -1);
            StringBuilder first = content.get(start[0]);
            StringBuilder last = new StringBuilder(first.substring(start[1]));
            first.setLength(start[1]);
            for (int i = 1; i < lines.length; i++) {
                content.add(start[0] + i, new StringBuilder(lines[i]));
            }
            content.get(start[0]).append(lines[0]);
            content.get(start[0] + lines.length - 1).append(last);
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