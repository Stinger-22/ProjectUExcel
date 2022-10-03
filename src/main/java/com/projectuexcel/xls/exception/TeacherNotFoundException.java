package com.projectuexcel.xls.exception;

public class TeacherNotFoundException extends Exception {
    private static final String DESCRIPTION = "Teacher was not found with given initials";
    private String initials;

    public TeacherNotFoundException(String initials) {
        this.initials = initials;
    }

    public TeacherNotFoundException(String initials, String msg) {
        super(msg);
        this.initials = initials;
    }

    @Override
    public String toString() {
        return "TeacherNotFoundException{" + "initials='" + initials + '\'' + "} " + super.toString();
    }

    @Override
    public String getMessage() {
        return DESCRIPTION;
    }
}
