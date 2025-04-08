package editor.Comand;

import editor.EditorState;

public class DeleteComand implements Comand {
    private EditorState state;
    private int[] position;
    String deletedContent;

    public DeleteComand(EditorState state, String deletedContent, int[] deletionPosition) {
        this.state = state;
        this.position = deletionPosition;
        this.deletedContent = deletedContent;
    }


    @Override
    public void execute() {
        if (position[0] == 0 && position[1] == 0) {
            return;
        }
        if (position[1] == 0) {
            state.getRawContent().get(position[0] - 1).append(state.getRawContent().get(position[0]));
            state.getRawContent().remove(position[0]);
            state.updateContents();
            return;
        }
        state.getRawContent().get(position[0]).delete(position[1] - deletedContent.length(), position[1]);
        state.updateContents();
    }

    @Override
    public void undo() {
        state.getRawContent().get(position[0]).insert(position[1], deletedContent);
        state.updateContents();
    }
}
