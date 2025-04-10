package editor.BasicEditor.Saving;


public class SaveOptions {
    public enum Format {
        TXT, MD, RTF, JSON, XML
    }

    private String fileName;
    private String targetFileName;
    private Format targetFormat;

    public SaveOptions(String fileName, Format targetFormat, String targetFileName) {
        this.fileName = fileName;
        this.targetFormat = targetFormat;
        this.targetFileName = targetFileName;
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
}