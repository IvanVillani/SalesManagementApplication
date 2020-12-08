package com.ivan.salesapp.exceptions;

public class NoResellerDiscountsException extends Exception{
    private String sourceView;

    public NoResellerDiscountsException(String message, String sourceView) {
        super(message);
        this.sourceView = sourceView;
    }

    public String getSourceView() {
        return sourceView;
    }

    public void setSourceView(String sourceView) {
        this.sourceView = sourceView;
    }
}
