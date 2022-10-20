package com.projectuexcel.table.export;

import com.projectuexcel.table.Plan;
import com.projectuexcel.table.Teacher;

public class TeacherExporterYear extends TeacherExporter {

    public TeacherExporterYear(Plan plan) {
        super(plan);
    }

    @Override
    public int findFirstRow(Teacher teacher) {
        return teacher.getFirstRow();
    }

    @Override
    public int findLastRow(Teacher teacher) {
        return teacher.getLastRow() + 2;
    }
}
