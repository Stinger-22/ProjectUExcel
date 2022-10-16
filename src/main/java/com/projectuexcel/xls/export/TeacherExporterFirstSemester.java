package com.projectuexcel.xls.export;

import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.CodeNotFoundException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

public class TeacherExporterFirstSemester extends TeacherExporter {

    public TeacherExporterFirstSemester(XLSFilePlan plan) {
        super(plan);
    }

    @Override
    public int findFirstRow(String code) throws CodeNotFoundException {
        return plan.searchTeacher(code);
    }

    @Override
    public int findLastRow(int firstRow, String code) {
        Sheet sheet = plan.getSheet();
        Cell cell;
        do {
            firstRow++;
            cell = sheet.getRow(firstRow).getCell(0);
        } while (cell != null);
        return firstRow;
    }
}
