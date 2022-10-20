package com.projectuexcel.table;

import java.util.Objects;

public class Teacher {
    private String code;
    private int rowFirstSemesterStart;
    private int rowFirstSemesterEnd;
    private int rowSecondSemesterStart;
    private int rowSecondSemesterEnd;

    public Teacher(String code) {
        this.code = code;
        this.rowFirstSemesterStart = -1;
        this.rowFirstSemesterEnd = -1;
        this.rowSecondSemesterStart = -1;
        this.rowSecondSemesterEnd = -1;
    }

    public Teacher(String code, int rowFirstSemesterStart, int rowFirstSemesterEnd, int rowSecondSemesterStart, int rowSecondSemesterEnd) {
        this.code = code;
        this.rowFirstSemesterStart = rowFirstSemesterStart;
        this.rowFirstSemesterEnd = rowFirstSemesterEnd;
        this.rowSecondSemesterStart = rowSecondSemesterStart;
        this.rowSecondSemesterEnd = rowSecondSemesterEnd;
    }

    public String getCode() {
        return code;
    }

    public int getRowFirstSemesterStart() {
        return rowFirstSemesterStart;
    }

    public int getRowFirstSemesterEnd() {
        return rowFirstSemesterEnd;
    }

    public int getRowSecondSemesterStart() {
        return rowSecondSemesterStart;
    }

    public int getRowSecondSemesterEnd() {
        return rowSecondSemesterEnd;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRowFirstSemesterStart(int rowFirstSemesterStart) {
        if (rowFirstSemesterStart < -1) {
            throw new IllegalArgumentException();
        }
        this.rowFirstSemesterStart = rowFirstSemesterStart;
    }

    public void setRowFirstSemesterEnd(int rowFirstSemesterEnd) {
        if (rowFirstSemesterEnd < -1) {
            throw new IllegalArgumentException();
        }
        this.rowFirstSemesterEnd = rowFirstSemesterEnd;
    }

    public void setRowSecondSemesterStart(int rowSecondSemesterStart) {
        if (rowSecondSemesterStart < -1) {
            throw new IllegalArgumentException();
        }
        this.rowSecondSemesterStart = rowSecondSemesterStart;
    }

    public void setRowSecondSemesterEnd(int rowSecondSemesterEnd) {
        if (rowSecondSemesterEnd < -1) {
            throw new IllegalArgumentException();
        }
        this.rowSecondSemesterEnd = rowSecondSemesterEnd;
    }

    /**
     * Get number of first row where there is info about teacher and subject
     * @return number of first row where there is info about teacher and subject
     */
    public int getFirstRow() {
        if (this.rowFirstSemesterStart != -1) {
            return this.rowFirstSemesterStart;
        }
        else if (this.rowSecondSemesterStart != -1) {
            return this.rowSecondSemesterStart;
        }
        else {
            throw new IllegalStateException();
        }
    }

    /**
     * Get number of last row where there is info about teacher and subject
     * @return number of last row where there is info about teacher and subject
     */
    public int getLastRow() {
        if (this.rowSecondSemesterEnd != -1) {
            return this.rowSecondSemesterEnd;
        }
        else if (this.rowFirstSemesterEnd != -1) {
            return this.rowFirstSemesterEnd;
        }
        else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "code='" + code + '\'' +
                ", rowFirstSemesterStart=" + rowFirstSemesterStart +
                ", rowFirstSemesterEnd=" + rowFirstSemesterEnd +
                ", rowSecondSemesterStart=" + rowSecondSemesterStart +
                ", rowSecondSemesterEnd=" + rowSecondSemesterEnd +
                '}';
    }
}
