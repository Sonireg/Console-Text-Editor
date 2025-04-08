package editor.Comand;


public interface Comand {
    void execute();
    void undo();
    void setAfterCoordAndOffsetY(int[] afterCursor, int afterOffsestY);
}
