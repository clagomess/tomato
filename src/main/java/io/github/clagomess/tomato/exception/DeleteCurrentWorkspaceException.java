package io.github.clagomess.tomato.exception;

public class DeleteCurrentWorkspaceException extends TomatoException {
    public DeleteCurrentWorkspaceException() {
        super("Is not possible to delete current workspace");
    }
}
