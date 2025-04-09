package editor.BasicEditor;

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
    private String filePath;
    private int selectionStartX = -1;
    private int selectionStartY = -1;
    private int selectionEndX = -1;
    private int selectionEndY = -1;

    
    public List<StringBuilder> getContent() { return content; }
    public void setContent(List<StringBuilder> content) { this.content = content; }


    public List<StringBuilder> getRawContent() { return rawContent; }
    public void setRawContent(List<StringBuilder> content) { 
        this.rawContent = content; 
        this.content = cutLines(content);
    }

    public void updateContents() {
        List<StringBuilder> newContent = new ArrayList<>();
    
        for (StringBuilder line : rawContent) {
            String[] parts = line.toString().split("\r", -1);
            for (String part : parts) {
                newContent.add(new StringBuilder(part));
            }
        }
        this.rawContent = newContent;
    
        this.content = cutLines(newContent);
    }

    public int getCursorX() { return cursorX; }
    public void setCursorX(int cursorX) { this.cursorX = cursorX; }

    public int getCursorY() { return cursorY; }
    public void setCursorY(int cursorY) { this.cursorY = cursorY; }

    public int getOffsetY() { return offsetY; }
    public void setOffsetY(int offsetY) { this.offsetY = offsetY; }

    public int getOffsetX() { return offsetX; }

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public int getColumns() { return columns; }
    public void setColumns(int columns) { this.columns = columns; }

    public Terminal getTerminal() { return terminal; }
    public void setTerminal(Terminal terminal) { this.terminal = terminal; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getSelectionStartX() { return selectionStartX; }
    public int getSelectionStartY() { return selectionStartY; }

    public int getSelectionEndX() { return selectionEndX; }
    public int getSelectionEndY() { return selectionEndY; }

    public int[] getSelectionStartCoordinates() {
        if (selectionStartY < selectionEndY || 
            (selectionStartY == selectionEndY && selectionStartX <= selectionEndX)) {
            return new int[] {selectionStartY, selectionStartX};
        } else {
            return new int[] {selectionEndY, selectionEndX};
        }
    }
    
    public int[] getSelectionEndCoordinates() {
        if (selectionStartY < selectionEndY || 
            (selectionStartY == selectionEndY && selectionStartX <= selectionEndX)) {
            return new int[] {selectionEndY, selectionEndX};
        } else {
            return new int[] {selectionStartY, selectionStartX};
        }
    }

    public void setSelectionStart(int x, int y) {
        this.selectionStartX = x;
        this.selectionStartY = y;
    }

    public void setSelectionEnd(int x, int y) {
        this.selectionEndX = x;
        this.selectionEndY = y;
    }

    // Метод для получения выделенного текста
    public String getSelectedText() {
        if (selectionStartX == -1 || selectionStartY == -1 || selectionEndX == -1 || selectionEndY == -1) {
            return "";
        }
    
        int startX = selectionStartX;
        int startY = selectionStartY;
        int endX = selectionEndX;
        int endY = selectionEndY;
    
        // Упорядочим координаты, если нужно
        if (startY > endY || (startY == endY && startX > endX)) {
            int tmpX = startX;
            int tmpY = startY;
            startX = endX;
            startY = endY;
            endX = tmpX;
            endY = tmpY;
        }
    
        StringBuilder selectedText = new StringBuilder();
        
        for (int i = startY; i <= endY; i++) {
            StringBuilder line = rawContent.get(i);
            int start = (i == startY) ? startX : 0;
            int end = (i == endY) ? endX : line.length();
    
            selectedText.append(line.substring(start, end));
            if (i < endY) {
                selectedText.append("\n");
            }
        }
    
        return selectedText.toString();
    }

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
            
            // Если строка пуста, она все равно должна быть обработана
            if (rawLine.length() == 0) {
                wrappedLineCount++;
                if (cursorY < wrappedLineCount) {
                    return i;
                }
                continue;
            }
            
            // Количество обернутых строк для текущей строки
            int wrappedLinesForThisRawLine = (int) Math.ceil((double) rawLine.length() / (columns - 3));
            
            if (cursorY < wrappedLineCount + wrappedLinesForThisRawLine) {
                return i;
            }
            wrappedLineCount += wrappedLinesForThisRawLine;
        }
        return rawContent.size() - 1; // В случае, если ничего не найдено, возвращаем последнюю строку
    }
    
    private int getCurrentRawPosition() {
        int wrappedLineCount = 0;
        for (int i = 0; i < rawContent.size(); i++) {
            StringBuilder rawLine = rawContent.get(i);
    
            // Если строка пуста, увеличиваем wrappedLineCount, но не считаем позицию
            if (rawLine.length() == 0) {
                wrappedLineCount++;
                if (cursorY < wrappedLineCount) {
                    return 0; // Для пустой строки возвращаем 0, так как позиция в пустой строке всегда 0
                }
                continue;
            }
    
            // Количество обернутых строк для текущей строки
            int wrappedLinesForThisRawLine = (int) Math.ceil((double) rawLine.length() / (columns - 3));
            
            if (cursorY < wrappedLineCount + wrappedLinesForThisRawLine) {
                // Определяем позицию на текущей строке
                int segment = cursorY - wrappedLineCount;
                return (segment * (columns - 3)) + cursorX;
            }
            wrappedLineCount += wrappedLinesForThisRawLine;
        }
        return rawContent.get(rawContent.size() - 1).length(); // Возвращаем длину последней строки, если курсор в последней строке
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

    public boolean hasSelection() {
        return selectionStartX != selectionEndX || selectionStartY != selectionEndY;
    }
    
}