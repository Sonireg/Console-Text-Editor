package editor.BasicEditor;

import java.util.ArrayList;
import java.util.List;

import editor.Parsers.*;

public class EditorState {
    private List<StringBuilder> content = List.of();
    private List<StringBuilder> rawContent = List.of();
    private int cursorX = 0;
    private int cursorY = 0;
    private int offsetY = 0;
    private int offsetX = 0;
    private int rows = 10;
    private int columns = 10;
    private int maxLength = 10;
    private String filePath;
    private int selectionStartX = -1;
    private int selectionStartY = -1;
    private int selectionEndX = -1;
    private int selectionEndY = -1;
    Parser parser = new TXTPArser();
    
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public List<StringBuilder> getContent() { return content; }
    public void setContent(List<StringBuilder> content) { this.content = content; }


    public List<StringBuilder> getRawContent() { return rawContent; }
    public void setRawContent(List<StringBuilder> content) { 
        this.rawContent = content; 
        this.content = parser.parse(rawContent, maxLength);
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
    
        this.content = parser.parse(rawContent, maxLength);
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

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getSelectionStartX() { return selectionStartX; }
    public int getSelectionStartY() { return selectionStartY; }

    public int getSelectionEndX() { return selectionEndX; }
    public int getSelectionEndY() { return selectionEndY; }

    public int getMaxLength() { return maxLength; }
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }

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
    
        int[] startContentCoords = new int[]{selectionStartY, selectionStartX};
        int[] endContentCoords = new int[]{selectionEndY, selectionEndX};
    
        // Упорядочим координаты, если нужно
        if (selectionStartY > selectionEndY || (selectionStartY == selectionEndY && selectionStartX > selectionEndX)) {
            startContentCoords = new int[]{selectionEndY, selectionEndX};
            endContentCoords = new int[]{selectionStartY, selectionStartX};
        }
    
        int[] startRawCoords = convertToRawCoordinates(startContentCoords);
        int[] endRawCoords = convertToRawCoordinates(endContentCoords);
    
        int startLine = startRawCoords[0];
        int startX = startRawCoords[1];
        int endLine = endRawCoords[0];
        int endX = endRawCoords[1];
    
        StringBuilder selectedText = new StringBuilder();
    
        for (int i = startLine; i <= endLine; i++) {
            StringBuilder line = rawContent.get(i);
            int from = (i == startLine) ? startX : 0;
            int to = (i == endLine) ? endX : line.length();
    
            selectedText.append(line.substring(from, Math.min(to, line.length())));
            if (i < endLine) {
                selectedText.append("\n");
            }
        }
    
        return selectedText.toString();
    }

    public int[] convertToRawCoordinates(int[] coords) {
        int wrappedLineCount = 0;
        int line = coords[0];
        int position = coords[1];
    
        // Пройдем по строкам в rawContent
        for (int i = 0; i < getRawContent().size(); i++) {
            StringBuilder rawLine = getRawContent().get(i);
            
            if (rawLine.length() == 0) {
                wrappedLineCount++;
                if (line < wrappedLineCount) {
                    return new int[] {i, 0};  // Если пустая строка, возвращаем позицию 0
                }
                continue;
            }
    
            // Для каждой строки rawContent вычисляем количество обернутых строк
            int wrappedLinesForThisRawLine = (int) Math.ceil((double) rawLine.length() / getMaxLength());
            
            if (line < wrappedLineCount + wrappedLinesForThisRawLine) {
                int segment = line - wrappedLineCount;
                return new int[] {i, segment * getMaxLength() + position};
            }
            wrappedLineCount += wrappedLinesForThisRawLine;
        }
        
        // Если ничего не найдено, возвращаем последние координаты
        return new int[] {getRawContent().size() - 1, 
                          getRawContent().get(getRawContent().size() - 1).length()};
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
            int wrappedLinesForThisRawLine = (int) Math.ceil((double) rawLine.length() / (maxLength));
            
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
            int wrappedLinesForThisRawLine = (int) Math.ceil((double) rawLine.length() / (maxLength));
            
            if (cursorY < wrappedLineCount + wrappedLinesForThisRawLine) {
                // Определяем позицию на текущей строке
                int segment = cursorY - wrappedLineCount;
                return (segment * (maxLength)) + cursorX;
            }
            wrappedLineCount += wrappedLinesForThisRawLine;
        }
        return rawContent.get(rawContent.size() - 1).length(); // Возвращаем длину последней строки, если курсор в последней строке
    }
    

    

    public boolean hasSelection() {
        return selectionStartX != selectionEndX || selectionStartY != selectionEndY;
    }
    
}