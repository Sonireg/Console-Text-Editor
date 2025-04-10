package editor.Converters;

import java.util.List;
import java.util.stream.Collectors;

public class RtfToMdConverter implements FormatConverter {
    @Override
    public List<StringBuilder> convert(List<StringBuilder> input) {
        return input.stream()
                .map(line -> new StringBuilder(
                        line.toString()
                            .replaceAll("<b1>(.*?)<b0>", "**$1**")
                            .replaceAll("<i1>(.*?)<i0>", "*$1*")
                ))
                .collect(Collectors.toList());
    }
}