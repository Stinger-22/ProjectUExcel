package com.projectuexcel.table.export;

import com.projectuexcel.table.Teacher;

public interface Exporter {
    /**
     * Export part of excel (.xls or .xlsx) file into new file
     * @param teacher teacher whose plan should be exported
     * @param path path to new file
     */
    void export(Teacher teacher, String path);
}
