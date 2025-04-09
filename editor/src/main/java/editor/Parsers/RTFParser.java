package editor.Parsers;

import java.util.List;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class RTFParser extends Parser {
    private static final Pattern BOLD_ON = Pattern.compile("\\\\b1");
    private static final Pattern BOLD_OFF = Pattern.compile("\\\\b0");
    private static final Pattern ITALIC_ON = Pattern.compile("\\\\i1");
    private static final Pattern ITALIC_OFF = Pattern.compile("\\\\i0");
    private static final Pattern UNDERLINE_ON = Pattern.compile("\\\\ul1");
    private static final Pattern UNDERLINE_OFF = Pattern.compile("\\\\ul0");

    @Override
    public List<StringBuilder> parse(List<StringBuilder> rawContent, int maxLength) {
        List<StringBuilder> result = new ArrayList<>();
        result = cutLines(result, maxLength);
        for (StringBuilder line : rawContent) {
            String text = line.toString();

            // Применяем регулярные выражения для замены
            text = replaceBold(text);
            text = replaceItalic(text);
            text = replaceUnderline(text);

            // Добавляем в список результат
            result.add(new StringBuilder(text));
        }
        

        return result;
    }

    private String replaceBold(String text) {
        text = BOLD_ON.matcher(text).replaceAll("\033[1m");
        text = BOLD_OFF.matcher(text).replaceAll("\033[22m");
        return text;
    }

    private String replaceItalic(String text) {
        text = ITALIC_ON.matcher(text).replaceAll("\033[3m");
        text = ITALIC_OFF.matcher(text).replaceAll("\033[23m");
        return text;
    }

    private String replaceUnderline(String text) {
        text = UNDERLINE_ON.matcher(text).replaceAll("\033[4m");
        text = UNDERLINE_OFF.matcher(text).replaceAll("\033[24m");
        return text;
    }
}
    