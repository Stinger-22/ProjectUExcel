package com.projectuexcel.table.exception;

public class CodeNotFoundException extends Exception {
    private static final String DESCRIPTION = "Teacher was not found with given code";
    private String code;

    public CodeNotFoundException(String code) {
        this.code = code;
    }

    public CodeNotFoundException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    @Override
    public String toString() {
        return "CodeNotFoundException{" + "code='" + code + '\'' + "} " + super.toString();
    }

    @Override
    public String getMessage() {
        return DESCRIPTION;
    }
}
