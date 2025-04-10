package editor.BasicEditor.Saving;

import editor.Converters.*;
import editor.BasicEditor.EditorState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class SaveManager {
    private static final Map<String, SaveOptions.Format> extensionToFormat = Map.of(
        "txt", SaveOptions.Format.TXT,
        "md", SaveOptions.Format.MD,
        "rtf", SaveOptions.Format.RTF,
        "json", SaveOptions.Format.JSON,
        "xml", SaveOptions.Format.XML
    );

    private final Map<FromToKey, FormatConverter> converters = Map.of(
        new FromToKey(SaveOptions.Format.MD, SaveOptions.Format.RTF), new MdToRtfConverter(),
        new FromToKey(SaveOptions.Format.RTF, SaveOptions.Format.MD), new RtfToMdConverter(),
        new FromToKey(SaveOptions.Format.TXT, SaveOptions.Format.MD), new TxtToMdConverter(),
        new FromToKey(SaveOptions.Format.TXT, SaveOptions.Format.RTF), new TxtToMdConverter(),
        new FromToKey(SaveOptions.Format.MD, SaveOptions.Format.TXT), new TxtToMdConverter(),
        new FromToKey(SaveOptions.Format.RTF, SaveOptions.Format.TXT), new TxtToMdConverter(),
        new FromToKey(SaveOptions.Format.JSON, SaveOptions.Format.XML), new JsonToXmlConverter(),
        new FromToKey(SaveOptions.Format.XML, SaveOptions.Format.JSON), new XmlToJsonConverter()
    );

    public void save(EditorState state, SaveOptions options) throws IOException {
        SaveOptions.Format sourceFormat = getFormatFromExtension(state.getFilePath());
        SaveOptions.Format targetFormat = options.getTargetFormat();
        List<StringBuilder> content = state.getRawContent();

        if (sourceFormat != targetFormat) {
            FormatConverter converter = getConverter(sourceFormat, targetFormat);
            if (converter != null) {
                content = converter.convert(content);
            } else {
                System.out.println("Нет конвертера из " + sourceFormat + " в " + targetFormat);
                return;
            }
        }

        writeToFile(content, options.targetFileName());
    }

    private SaveOptions.Format getFormatFromExtension(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        return extensionToFormat.getOrDefault(ext, SaveOptions.Format.TXT);
    }

    private FormatConverter getConverter(SaveOptions.Format from, SaveOptions.Format to) {
        return converters.get(new FromToKey(from, to));
    }

    private void writeToFile(List<StringBuilder> content, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (StringBuilder line : content) {
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }
}
