package com.projectuexcel.table.export;

import com.projectuexcel.table.Plan;
import com.projectuexcel.table.Teacher;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public abstract class TeacherExporter implements Exporter {
    Plan plan;

    public TeacherExporter(Plan plan) {
        this.plan = plan;
    }

    public void export(Teacher teacher, String path) {
        Workbook exportWorkbook;
        if (plan.getWorkbook() instanceof XSSFWorkbook) {
            exportWorkbook = new XSSFWorkbook();
        }
        else if (plan.getWorkbook() instanceof HSSFWorkbook) {
            exportWorkbook = new HSSFWorkbook();
        }
        else {
            throw new IllegalStateException();
        }

        Sheet originSheet = plan.getSheet();
        exportWorkbook.createSheet(originSheet.getSheetName());
        Sheet exportSheet = exportWorkbook.getSheetAt(0);

        for (int i = 0; i < plan.getHeader().getLastCellNum(); i++) {
            exportSheet.setColumnWidth(i, originSheet.getColumnWidth(i));
        }

        Map<CellStyle, CellStyle> cellStyles = plan.copyCellStyles(exportWorkbook);

        Row newRow = exportSheet.createRow(0);

        plan.copyRow(plan.getHeader(), newRow, cellStyles);
        int rowDestinationIndex = 1;
        for (int i = findFirstRow(teacher); i <= findLastRow(teacher); i++) {
            newRow = exportSheet.createRow(rowDestinationIndex);
            plan.copyRow(plan.getSheet().getRow(i), newRow, cellStyles);
            rowDestinationIndex++;
        }
        write(exportWorkbook, path);
    }

    public abstract int findFirstRow(Teacher teacher);
    public abstract int findLastRow(Teacher teacher);

    private void write(Workbook exportWorkbook, String path) {
        try (OutputStream fileOut = new FileOutputStream(path)) {
            exportWorkbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}