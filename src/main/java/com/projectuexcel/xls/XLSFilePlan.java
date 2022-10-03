package com.projectuexcel.xls;

import com.projectuexcel.xls.exception.TeacherNotFoundException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Open existing teacher plan file. It can search for teacher in table and export chosen teacher plan into another file.
 */
public class XLSFilePlan {
    private File file;
    private FileInputStream fileInputStream;
    private Workbook workbook;
    private Sheet sheet;

    /**
     * Constructor for XLSFilePlan which opens file and initializes required fields.
     * @param path path for file to open
     * @throws IOException is thrown when file is not found
     */
    public XLSFilePlan(String path) throws IOException {
        this.file = new File(path);
        this.fileInputStream = new FileInputStream(this.file);
        this.workbook = new HSSFWorkbook(fileInputStream);
        this.sheet = workbook.getSheetAt(0);
    }

    /**
     * Find first occurrence of teacher in table
     * @param initials initials of teacher
     * @return row index of first teacher occurance in file
     * @throws TeacherNotFoundException is thrown when teacher is not found
     */
    public int searchTeacher(String initials) throws TeacherNotFoundException {    // exception or -1 ?
        int firstRow, lastRow;
        firstRow = 1;
        lastRow = sheet.getLastRowNum();

        int lowRow = firstRow, highRow = lastRow, mid;
        int comparison;
        Cell cell;
        while (lowRow < highRow) {
            mid = (highRow + lowRow) / 2;
            cell = sheet.getRow(mid).getCell(0);

            if (cell == null) {
                mid--;
                cell = sheet.getRow(mid).getCell(0);
            }

            comparison = cell.getStringCellValue().compareTo(initials);

            if (comparison > 0) {
                highRow = mid - 1;
            }
            else if (comparison < 0) {
                lowRow = mid + 1;
            }
            else {
                return findFirst(mid, initials);
            }
        }
        throw new TeacherNotFoundException(initials);
    }

    /**
     * Find first occurrence of teacher with given index of n occurrence
     * @param index index of row with teacher
     * @param initials initials of teacher
     * @return first row with teacher
     */
    private int findFirst(int index, String initials) {
        Cell cell;
        do {
            index--;
            cell = sheet.getRow(index).getCell(0);
        } while (cell == null || cell.getStringCellValue().equals(initials));

        return index + 1;
    }

    /**
     * Find last occurrence of teacher with given index of n occurrence
     * @param index index of row with teacher
     * @param initials initials of teacher
     * @return last row with teacher
     */
    private int findLast(int index, String initials) {
        Cell cell;
        do {
            index++;
            cell = sheet.getRow(index).getCell(0);
        } while (cell == null || cell.getStringCellValue().equals(initials));

        return index;
    }

    /**
     * Export chosen teacher plan into his own file
     * @param initials initials of teacher looked for
     * @param path path to new file
     * @throws TeacherNotFoundException is thrown when teacher is not found in table
     */
    public void exportTeacherPlan(String initials, String path) throws TeacherNotFoundException {
        int firstRow = searchTeacher(initials);
        int lastRow = findLast(firstRow, initials);

        Workbook exportWorkbook = new HSSFWorkbook();
        FormulaEvaluator evaluator = exportWorkbook.getCreationHelper().createFormulaEvaluator();

        exportWorkbook.createSheet();
        Row header = this.sheet.getRow(0);
        Sheet exportSheet = exportWorkbook.getSheetAt(0);
        for (int i = 0; i < header.getLastCellNum(); i++) {
            exportSheet.setColumnWidth(i, this.sheet.getColumnWidth(i));
        }
        Map<CellStyle, CellStyle> cellStyles = copyCellStyles(exportWorkbook);

        Row newRow = exportSheet.createRow(0);
        copyRow(header, newRow, cellStyles, evaluator);

        int rowIndex = 1;
        for (int i = firstRow; i <= lastRow; i++) {
            newRow = exportSheet.createRow(rowIndex);
            copyRow(this.sheet.getRow(i), newRow, cellStyles, evaluator);
            rowIndex++;
        }

        try (OutputStream fileOut = new FileOutputStream(path)) {
            exportWorkbook.write(fileOut);
        } catch (IOException e) {
            System.out.println("Can't create file");
            e.printStackTrace();
        }
    }

    /**
     * Copy row from file into different file
     * @param source row to copy
     * @param destination destination row to paste source row
     * @param cellStyles styles of cells from source file
     * @param evaluator evaluator instance of destination file to evaluate formulas if any
     */
    private void copyRow(Row source, Row destination, Map<CellStyle, CellStyle> cellStyles, FormulaEvaluator evaluator) {
        Cell cellSource, cellDestination;
        for (int i = 0; i < source.getLastCellNum(); i++) {
            cellSource = source.getCell(i);
            if (cellSource == null) {
                continue;
            }
            cellDestination = destination.createCell(i);
            cellDestination.setCellStyle(cellStyles.get(cellSource.getCellStyle()));

            switch (cellSource.getCellType()) {
                case STRING:
                    cellDestination.setCellValue(cellSource.getStringCellValue());
                    break;
                case NUMERIC:
                    cellDestination.setCellValue(cellSource.getNumericCellValue());
                    break;
                case FORMULA:
                    String newFormula = buildFormula(cellSource.getCellFormula(), destination.getRowNum() - 1);
                    cellDestination.setCellFormula(newFormula);
                    evaluator.evaluateFormulaCell(cellDestination);
                    break;
            }
        }
    }

    /**
     * Fix formula row indexes so it is evaluated properly
     * @param oldFormula formula from source file
     * @param indexLastRow index of last row where teacher has
     * @return fixed formula which can be used in new file
     */
    private String buildFormula(String oldFormula, int indexLastRow) {
        String[] parseFormula = oldFormula.split(",");
        char column = parseFormula[1].charAt(0);
        return parseFormula[0] + ',' + column + 2 + ':' + column + indexLastRow + ')';   // or stringbuilder?
    }

    /**
     * Copy cell styles from source file, so they can be used in new file
     * @param newWorkbook new file
     * @return map which key is style from source file and value is corresponding style from new file
     */
    private Map<CellStyle, CellStyle> copyCellStyles(Workbook newWorkbook) {
        Map<CellStyle, CellStyle> cellStyles = new HashMap<>();
        int n = workbook.getNumCellStyles();
        CellStyle sourceStyle, style;
        for (int i = 0; i < n; i++) {
            sourceStyle = workbook.getCellStyleAt(i);
            style = newWorkbook.createCellStyle();
            style.cloneStyleFrom(sourceStyle);
            cellStyles.put(sourceStyle, style);
        }
        return cellStyles;
    }
}
