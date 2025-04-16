package editor.BasicEditor.Saving;

public class SaveOptions {
    public enum Format {
        TXT, MD, RTF, JSON, XML
    }

    public enum StorageType {
        LOCAL, CLOUD
    }

    private String fileName;
    private String targetFileName;
    private Format targetFormat;
    private StorageType storageType;

    public SaveOptions(String fileName, Format targetFormat, String targetFileName, StorageType storageType) {
        this.fileName = fileName;
        this.targetFormat = targetFormat;
        this.targetFileName = targetFileName;
        this.storageType = storageType;
    }

    public String getFileName() {
        return fileName;
    }

    public String targetFileName() {
        return targetFileName;
    }

    public Format getTargetFormat() {
        return targetFormat;
    }

    public StorageType getStorageType() {
        return storageType;
    }
}
