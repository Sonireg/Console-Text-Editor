package editor.Comand;

import editor.EditorState;

public class InsertComand implements Comand {
    private EditorState state;
    private int[] position;
    String insertedContent;

    public InsertComand(EditorState state, String insertedContent, int[] insertionPosition) {
        this.state = state;
        this.position = insertionPosition;
        this.insertedContent = insertedContent;
    }

    @Override
    public void execute() {
        state.getRawContent().get(position[0]).insert(position[1], insertedContent);
        state.setRawContent(state.getRawContent());
    }

    @Override
    public void undo() {
        state.getRawContent().get(position[0]).delete(position[1], insertedContent.length());
        state.setRawContent(state.getRawContent());
    }
}
