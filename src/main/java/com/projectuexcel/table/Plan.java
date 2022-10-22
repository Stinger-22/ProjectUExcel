package com.projectuexcel.table;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.*;

/**
 * Open existing teacher plan file. It can search for teacher in table and export chosen teacher plan into another file.
 */
public class Plan {
    private final File file;
    private final Workbook workbook;
    private final Sheet sheet;
    private final Row header;
    private List<Teacher> teacherTablePlacement;
    private final FormulaEvaluator evaluator;

    /**
     * Constructor for XLSFilePlan which opens file and initializes required fields.
     * @param path full path for file to open
     * @throws IOException is thrown when file is not found
     */
    public Plan(String path) throws IOException, InvalidFormatException {
        Workbook workbook1;
        this.file = new File(path);
        try {
            workbook1 = new HSSFWorkbook(new FileInputStream(this.file));
        }
        catch (OfficeXmlFileException exception) {
            workbook1 = new XSSFWorkbook(this.file);
        }
        this.workbook = workbook1;
        this.sheet = workbook.getSheetAt(0);
        this.header = this.sheet.getRow(0);
        this.teacherTablePlacement = null;
        this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    }

    public File getFile() {
        return file;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public Row getHeader() {
        return header;
    }

    public List<Teacher> getTeacherTablePlacement() {
        if (teacherTablePlacement == null) {
            teacherTablePlacement = findTeacherTablePlacement();
        }
        return teacherTablePlacement;
    }

    /**
     * Copy row from file into different file
     * @param source row to copy
     * @param destination destination row to paste source row
     * @param cellStyles styles of cells from source file
     */
    public void copyRow(Row source, Row destination, Map<CellStyle, CellStyle> cellStyles) {
        if (source == null) {
            return;
        }
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
                case FORMULA:   // copies only value of formula, not formula
                    evaluator.evaluateFormulaCell(cellSource);
                    cellDestination.setCellValue(cellSource.getNumericCellValue());
                    break;
            }
        }
    }

    /**
     * Copy cell styles from source file, so they can be used in new file
     * @param newWorkbook new file
     * @return map which key is style from source file and value is corresponding style from new file
     */
    public Map<CellStyle, CellStyle> copyCellStyles(Workbook newWorkbook) {
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

    private List<Teacher> findTeacherTablePlacement() {
        //TODO: make function to find semester column
        int semesterColumn = 2;
        Cell cell;
        Row row;
        List<Teacher> teacherPlacement = new ArrayList<>();
        Teacher teacher;
        int rows = sheet.getLastRowNum() - 1;
        int i = 1;
        do {
            row = sheet.getRow(i);
            cell = row.getCell(0);
            teacher = new Teacher(cell.getStringCellValue());

            if (row.getCell(semesterColumn).getNumericCellValue() == 1) {
                teacher.setRowFirstSemesterStart(i);
                while (cell != null && !cell.getStringCellValue().equals("")) {
                    i++;
                    cell = sheet.getRow(i).getCell(0);
                }
                teacher.setRowFirstSemesterEnd(i - 1);
                i++;
            }

            row = sheet.getRow(i);
            cell = row.getCell(0);
            if (row.getCell(semesterColumn).getNumericCellValue() == 2) {
                teacher.setRowSecondSemesterStart(i);
                while (cell != null && !cell.getStringCellValue().equals("")) {
                    i++;
                    cell = sheet.getRow(i).getCell(0);
                }
                teacher.setRowSecondSemesterEnd(i - 1);
                i += 2;
            }

            teacherPlacement.add(teacher);
        } while (i < rows);
        return teacherPlacement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plan plan = (Plan) o;
        return Objects.equals(file, plan.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
}