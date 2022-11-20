package com.projectuexcel.ui.table;

public class CodeMail {
    private String code;
    private String email;

    public CodeMail(String code, String email) {
        this.code = code;
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "CodeMail{" +
                "code='" + code + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
