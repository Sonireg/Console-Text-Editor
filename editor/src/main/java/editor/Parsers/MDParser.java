package editor.Parsers;

import java.util.ArrayList;
import java.util.List;


public class MDParser extends Parser {

    @Override
    public List<StringBuilder> parse(List<StringBuilder> rawContent, int maxLength) {
        List<StringBuilder> result = new ArrayList<>();
        result = cutLines(result, maxLength);
        for (StringBuilder rawLine : rawContent) {
            String line = rawLine.toString();
            // Тройные стили: жирный + подчёркнутый + курсив
            line = line.replaceAll("\\*\\*\\*__([^_]+?)__\\*\\*\\*", "\u001B[1m\u001B[3m\u001B[4m$1\u001B[0m");
            line = line.replaceAll("__\\*\\*\\*([^*]+?)\\*\\*\\*__", "\u001B[4m\u001B[1m\u001B[3m$1\u001B[0m");
            line = line.replaceAll("\\*__\\*\\*([^*]+?)\\*\\*__\\*", "\u001B[3m\u001B[4m\u001B[1m$1\u001B[0m");
            // Двойные комбинации (жирный+курсив, жирный+подчёркнутый, курсив+подчёркнутый)
            line = line.replaceAll("\\*\\*__([^_]+?)__\\*\\*", "\u001B[1m\u001B[4m$1\u001B[0m");
            line = line.replaceAll("__\\*\\*([^*]+?)\\*\\*__", "\u001B[4m\u001B[1m$1\u001B[0m");
            line = line.replaceAll("\\*\\*\\*([^*]+?)\\*\\*\\*", "\u001B[1m\u001B[3m$1\u001B[0m");
            line = line.replaceAll("\\*__([^_]+?)__\\*", "\u001B[3m\u001B[4m$1\u001B[0m");
            line = line.replaceAll("__\\*([^*]+?)\\*__", "\u001B[4m\u001B[3m$1\u001B[0m");
            // Одиночные стили
            line = line.replaceAll("\\*\\*([^*]+?)\\*\\*", "\u001B[1m$1\u001B[0m");
            line = line.replaceAll("\\*([^*]+?)\\*", "\u001B[3m$1\u001B[0m");
            line = line.replaceAll("__([^_]+?)__", "\u001B[4m$1\u001B[0m");
    
            result.add(new StringBuilder(line));
        }
        
        return result;
    }
    
}