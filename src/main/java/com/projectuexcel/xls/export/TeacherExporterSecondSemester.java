package com.projectuexcel.xls.export;

import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.CodeNotFoundException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

public class TeacherExporterSecondSemester extends TeacherExporter {

    public TeacherExporterSecondSemester(XLSFilePlan plan) {
        super(plan);
    }

    @Override
    public int findFirstRow(String code) throws CodeNotFoundException {
        int firstRow = plan.searchTeacher(code);
        Sheet sheet = plan.getSheet();
        Cell cell;
        do {
            firstRow++;
            cell = sheet.getRow(firstRow).getCell(0);
        } while (cell != null);
        return firstRow + 1;
    }

    @Override
    public int findLastRow(int firstRow, String code) {
        return plan.findLast(firstRow, code);
    }
}
