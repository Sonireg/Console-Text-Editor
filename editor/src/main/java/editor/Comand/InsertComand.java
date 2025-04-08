package editor.Comand;

import editor.EditorState;

public class InsertComand implements Comand {
    protected EditorState state;
    protected int[] position;
    protected String insertedContent;

    public InsertComand(EditorState state, String insertedContent, int[] insertionPosition) {
        this.state = state;
        this.position = insertionPosition;
        this.insertedContent = insertedContent;
    }

    @Override
    public void execute() {
        state.getRawContent().get(position[0]).insert(position[1], insertedContent);
        state.updateContents();
    }

    @Override
    public void undo() {
        if (position[0] == 0 && position[1] == 0) {
            return;
        }
        if (position[1] == 0) {
            state.getRawContent().get(position[0] - 1).append(state.getRawContent().get(position[0]));
            state.getRawContent().remove(position[0]);
            state.updateContents();
            return;
        }
        state.getRawContent().get(position[0]).delete(position[1], insertedContent.length());
        state.updateContents();
    }
}
