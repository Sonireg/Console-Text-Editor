package editor.Parsers;

import java.util.ArrayList;
import java.util.List;

public abstract class Parser {
    public abstract List<StringBuilder> parse(List<StringBuilder> rawContent, int maxLength);

    protected List<StringBuilder> cutLines(List<StringBuilder> lines, int maxLength) {
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
