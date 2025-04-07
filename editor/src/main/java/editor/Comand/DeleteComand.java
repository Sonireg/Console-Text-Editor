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
        state.getRawContent().get(position[0]).delete(position[1], position[1] + deletedContent.length());
        state.setRawContent(state.getRawContent());
    }

    @Override
    public void undo() {
        state.getRawContent().get(position[0]).insert(position[1], deletedContent);
        state.setRawContent(state.getRawContent());
    }
}
