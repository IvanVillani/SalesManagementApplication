package com.ivan.salesapp.exceptions;

public class InvalidCategoryException extends Exception{
    private String sourceView;
    private String id;
    private String name;

    public InvalidCategoryException(String message, String sourceView, String id, String name) {
        super(message);
        this.sourceView = sourceView;
        this.id = id;
        this.name = name;
    }

    public String getSourceView() {
        return sourceView;
    }

    public void setSourceView(String sourceView) {
        this.sourceView = sourceView;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
