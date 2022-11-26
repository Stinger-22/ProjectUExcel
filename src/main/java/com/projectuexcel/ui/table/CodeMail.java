package com.projectuexcel.ui.table;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeMail codeMail = (CodeMail) o;
        return Objects.equals(code, codeMail.code) && Objects.equals(email, codeMail.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, email);
    }
}
