package editor.Converters;

import java.util.List;
import java.util.stream.Collectors;

public class MdToRtfConverter implements FormatConverter {
    @Override
    public List<StringBuilder> convert(List<StringBuilder> input) {
        return input.stream()
                .map(line -> new StringBuilder(
                        line.toString()
                            .replaceAll("\\*\\*(.*?)\\*\\*", "<b1>$1<b0>")
                            .replaceAll("\\*(.*?)\\*", "<si1>$1<i0>")
                ))
                .collect(Collectors.toList());
    }
}