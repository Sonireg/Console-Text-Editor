package editor.Converters;

import java.util.List;

public class TxtToMdConverter implements FormatConverter {
    @Override
    public List<StringBuilder> convert(List<StringBuilder> input) {
        return input; 
    }
}