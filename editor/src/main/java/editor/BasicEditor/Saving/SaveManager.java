package editor.BasicEditor.Saving;

import editor.Converters.*;
import editor.User.Subscriprions.*;

import editor.BasicEditor.EditorState;
import editor.BasicEditor.Saving.SaveStrategy.*;
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
                System.out.println("There is no converter from " + sourceFormat + " to " + targetFormat);
                return;
            }
        }
        StorageStrategy strategy = new LocalStorageStrategy();
        try 
        {
            strategy = options.getStorageType() == SaveOptions.StorageType.CLOUD
            ? new CloudStorageStrategy()
            : new LocalStorageStrategy();
        }
        catch (Exception e) 
        { 
            System.out.println("Unable to save to cloud, saving to local storage");
        }
         

        strategy.save(options.targetFileName(), content);

        HistoryManager.addSnapshot(options.targetFileName(), content);

        NotificationManager notificationManager = new NotificationManager(
            new SubscriptionManager(), new NotificationStorage()
        );

        notificationManager.notifySubscribers(options.targetFileName(),
                                              HistoryManager.getSnapshotCount(options.targetFileName()));
    }

    private SaveOptions.Format getFormatFromExtension(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        return extensionToFormat.getOrDefault(ext, SaveOptions.Format.TXT);
    }

    private FormatConverter getConverter(SaveOptions.Format from, SaveOptions.Format to) {
        return converters.get(new FromToKey(from, to));
    }
}
