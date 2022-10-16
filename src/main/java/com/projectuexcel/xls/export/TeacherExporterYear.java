package com.projectuexcel.xls.export;

import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.CodeNotFoundException;

public class TeacherExporterYear extends TeacherExporter {

    public TeacherExporterYear(XLSFilePlan plan) {
        super(plan);
    }

    @Override
    public int findFirstRow(String code) throws CodeNotFoundException {
        return plan.searchTeacher(code);
    }

    @Override
    public int findLastRow(int firstRow, String code) {
        return plan.findLast(firstRow, code);
    }
}