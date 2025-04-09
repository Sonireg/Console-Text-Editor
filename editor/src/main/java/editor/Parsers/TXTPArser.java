package editor.Parsers;

import java.util.ArrayList;
import java.util.List;

public class TXTPArser extends Parser {

    @Override
    public List<StringBuilder> parse(List<StringBuilder> rawContent, int maxLength) {
        List<StringBuilder> result = new ArrayList<>();
        result = cutLines(rawContent, maxLength);
        return result;
    }
}
