package editor.Parsers;

import java.util.List;
import java.util.ArrayList;

public class RTFParser extends Parser {
    @Override
    public List<StringBuilder> parse(List<StringBuilder> rawContent, int maxLength) {
        List<StringBuilder> result = new ArrayList<>();
        List<StringBuilder> promResult = cutLines(rawContent, maxLength);
        for (StringBuilder rawLine : promResult) {
            String line = rawLine.toString();
            // Тройные стили: жирный + курсив + подчёркнутый (порядок неважен)
            line = line.replaceAll("<b1><i1><u1>(.+?)<u0><i0><b0>", "\u001B[1m\u001B[3m\u001B[4m$1\u001B[0m");
            line = line.replaceAll("<u1><b1><i1>(.+?)<i0><b0><u0>", "\u001B[4m\u001B[1m\u001B[3m$1\u001B[0m");
            // Добавь другие комбинации, если нужно учитывать любой порядок (см. ниже)

            // Двойные стили
            line = line.replaceAll("<b1><i1>(.+?)<i0><b0>", "\u001B[1m\u001B[3m$1\u001B[0m");
            line = line.replaceAll("<i1><b1>(.+?)<b0><i0>", "\u001B[3m\u001B[1m$1\u001B[0m");

            line = line.replaceAll("<b1><u1>(.+?)<u0><b0>", "\u001B[1m\u001B[4m$1\u001B[0m");
            line = line.replaceAll("<u1><b1>(.+?)<b0><u0>", "\u001B[4m\u001B[1m$1\u001B[0m");

            line = line.replaceAll("<i1><u1>(.+?)<u0><i0>", "\u001B[3m\u001B[4m$1\u001B[0m");
            line = line.replaceAll("<u1><i1>(.+?)<i0><u0>", "\u001B[4m\u001B[3m$1\u001B[0m");

            // Одиночные стили
            line = line.replaceAll("<b1>(.+?)<b0>", "\u001B[1m$1\u001B[0m");
            line = line.replaceAll("<i1>(.+?)<i0>", "\u001B[3m$1\u001B[0m");
            line = line.replaceAll("<u1>(.+?)<u0>", "\u001B[4m$1\u001B[0m");
    
            result.add(new StringBuilder(line));
        }
        
        return result;
    }
}
    