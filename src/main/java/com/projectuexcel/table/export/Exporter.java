package com.projectuexcel.table.export;

import com.projectuexcel.table.Teacher;

public interface Exporter {
    /**
     * Export part of Excel (.xls or .xlsx) file into new file
     * @param teacher teacher whose plan should be exported
     * @param path path to new file
     */
    boolean export(Teacher teacher, String path);
}
