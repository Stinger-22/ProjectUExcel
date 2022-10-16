package com.projectuexcel.xls.export;

import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.CodeNotFoundException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public abstract class TeacherExporter implements Exporter {
    XLSFilePlan plan;

    public TeacherExporter(XLSFilePlan plan) {
        this.plan = plan;
    }

    public void export(String code, String path) throws CodeNotFoundException {
        int firstRow = findFirstRow(code);
        int lastRow = findLastRow(firstRow, code);

        Workbook exportWorkbook = new HSSFWorkbook();
        FormulaEvaluator evaluator = exportWorkbook.getCreationHelper().createFormulaEvaluator();

        exportWorkbook.createSheet();

        Sheet exportSheet = exportWorkbook.getSheetAt(0);
        for (int i = 0; i < plan.getHeader().getLastCellNum(); i++) {
            exportSheet.setColumnWidth(i, plan.getSheet().getColumnWidth(i));
        }

        Map<CellStyle, CellStyle> cellStyles = plan.copyCellStyles(exportWorkbook);

        Row newRow = exportSheet.createRow(0);
        plan.copyRow(plan.getHeader(), newRow, cellStyles, evaluator);

        int rowDestinationIndex = 1;
        for (int i = firstRow; i <= lastRow; i++) {
            newRow = exportSheet.createRow(rowDestinationIndex);
            plan.copyRow(plan.getSheet().getRow(i), newRow, cellStyles, evaluator);
            rowDestinationIndex++;
        }

        write(exportWorkbook, path);
    }

    public abstract int findFirstRow(String code) throws CodeNotFoundException;
    public abstract int findLastRow(int firstRow, String code);

    private void write(Workbook exportWorkbook, String path) {
        try (OutputStream fileOut = new FileOutputStream(path)) {
            exportWorkbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}