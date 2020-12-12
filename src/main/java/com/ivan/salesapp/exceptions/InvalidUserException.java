package com.ivan.salesapp.exceptions;

public class InvalidUserException extends Exception{
    private String sourceView;
    private String username;

    public InvalidUserException(String message, String username, String sourceView) {
        super(message);
        this.username = username;
        this.sourceView = sourceView;
    }

    public String getSourceView() {
        return sourceView;
    }

    public void setSourceView(String sourceView) {
        this.sourceView = sourceView;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
