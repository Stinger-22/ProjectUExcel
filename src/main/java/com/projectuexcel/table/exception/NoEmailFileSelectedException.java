package com.projectuexcel.table.exception;

public class NoEmailFileSelectedException extends Exception {
    private static final String DESCRIPTION = "User didn't select emails file";

    public NoEmailFileSelectedException() {
    }

    public NoEmailFileSelectedException(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "NoEmailFileSelectedException{} " + super.toString();
    }

    @Override
    public String getMessage() {
            return DESCRIPTION;
    }
}
