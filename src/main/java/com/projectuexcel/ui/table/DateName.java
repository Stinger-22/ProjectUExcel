package com.projectuexcel.ui.table;

public class DateName {
    private String date;
    private String name;

    public DateName(String date, String name) {
        this.date = date;
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DateName{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
