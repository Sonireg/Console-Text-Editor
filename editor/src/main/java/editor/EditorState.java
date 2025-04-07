package editor;

import java.util.ArrayList;
import java.util.List;

import editor.TerminalSettings.Terminal;

public class EditorState {
    private List<StringBuilder> content = List.of();
    private List<StringBuilder> rawContent = List.of();
    private int cursorX = 0;
    private int cursorY = 0;
    private int offsetY = 0;
    private int offsetX = 0;
    private int rows = 10;
    private int columns = 10;
    private Terminal terminal;

    public List<StringBuilder> getContent() { return content; }
    public void setContent(List<StringBuilder> content) { this.content = content; }


    public List<StringBuilder> getRawContent() { return rawContent; }
    public void setRawContent(List<StringBuilder> content) { 
        this.rawContent = content; 
        this.content = cutLines(content);
    }

    public int getCursorX() { return cursorX; }
    public void setCursorX(int cursorX) { this.cursorX = cursorX; }

    public int getCursorY() { return cursorY; }
    public void setCursorY(int cursorY) { this.cursorY = cursorY; }

    public int getOffsetY() { return offsetY; }
    public void setOffsetY(int offsetY) { this.offsetY = offsetY; }

    public int getOffsetX() { return offsetX; }
    public void setOffsetX(int offsetX) { this.offsetX = offsetX; }

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public int getColumns() { return columns; }
    public void setColumns(int columns) { this.columns = columns; }

    public Terminal getTerminal() { return terminal; }
    public void setTerminal(Terminal terminal) { this.terminal = terminal; }

    

    /**
     * Converts wrapped coordinates (cursorY) to raw coordinates
     * @return array where [0] is raw line index, [1] is position in raw line
     */
    public int[] getRawCoordinates() {
        int rawLine = getCurrentRawLine();
        int rawPos = getCurrentRawPosition();
        return new int[]{rawLine, rawPos};
    }

    private int getCurrentRawLine() {
        int wrappedLineCount = 0;
        for (int i = 0; i < rawContent.size(); i++) {
            StringBuilder rawLine = rawContent.get(i);
            int wrappedLinesForThisRawLine = (int) Math.ceil((double) rawLine.length() / (columns - 3));
            
            if (cursorY < wrappedLineCount + wrappedLinesForThisRawLine) {
                return i;
            }
            wrappedLineCount += wrappedLinesForThisRawLine;
        }
        return rawContent.size() - 1;
    }

    /**
     * Finds the position in the raw line that corresponds to the current cursor position
     * @return character position in the original raw line
     */
    private int getCurrentRawPosition() {
        int wrappedLineCount = 0;
        for (int i = 0; i < rawContent.size(); i++) {
            StringBuilder rawLine = rawContent.get(i);
            int wrappedLinesForThisRawLine = (int) Math.ceil((double) rawLine.length() / (columns - 3));
            
            if (cursorY < wrappedLineCount + wrappedLinesForThisRawLine) {
                // Calculate which segment of the wrapped line we're in
                int segment = cursorY - wrappedLineCount;
                return (segment * (columns - 3)) + cursorX;
            }
            wrappedLineCount += wrappedLinesForThisRawLine;
        }
        return rawContent.get(rawContent.size() - 1).length();
    }

    private List<StringBuilder> cutLines(List<StringBuilder> lines) {
        int maxLength = terminal.getTerminalSize().width() - 3;
        List<StringBuilder> result = new ArrayList<>();
        
        for (StringBuilder line : lines) {
            if (line.length() <= maxLength) {
                result.add(line);
                continue;
            }
            
            int start = 0;
            while (start < line.length()) {
                int end = Math.min(start + maxLength, line.length());
                result.add(new StringBuilder(line.substring(start, end)));
                start = end;
            }
        }
        
        return result;
    }
}