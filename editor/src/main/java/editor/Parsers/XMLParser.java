package editor.Parsers;

import java.util.ArrayList;
import java.util.List;

public class XMLParser extends Parser {
    @Override
    public List<StringBuilder> parse(List<StringBuilder> input, int maxLength) {
        StringBuilder rawXml = new StringBuilder();
        for (StringBuilder sb : input) {
            rawXml.append(sb);
        }

        List<StringBuilder> formatted = new ArrayList<>();
        int indent = 0;
        int i = 0;

        while (i < rawXml.length()) {
            if (rawXml.charAt(i) == '<') {
                int tagEnd = rawXml.indexOf(">", i);
                if (tagEnd == -1) break;

                String tagContent = rawXml.substring(i, tagEnd + 1).trim();

                if (tagContent.startsWith("</")) {
                    indent--;
                }

                formatted.add(new StringBuilder(" ".repeat(indent * 4) + tagContent));

                if (!tagContent.startsWith("</") && !tagContent.endsWith("/>")) {
                    indent++;
                }

                i = tagEnd + 1;
            } else {
                int nextTag = rawXml.indexOf("<", i);
                if (nextTag == -1) nextTag = rawXml.length();

                String text = rawXml.substring(i, nextTag).trim();
                if (!text.isEmpty()) {
                    formatted.add(new StringBuilder(" ".repeat(indent * 4) + text));
                }

                i = nextTag;
            }
        }

        return cutLines(formatted, maxLength);
    }
}