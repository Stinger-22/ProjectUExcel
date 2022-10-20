package com.projectuexcel.table.export;

import com.projectuexcel.table.Plan;
import com.projectuexcel.table.Teacher;

public class TeacherExporterFirstSemester extends TeacherExporter {

    public TeacherExporterFirstSemester(Plan plan) {
        super(plan);
    }

    @Override
    public int findFirstRow(Teacher teacher) {
        return teacher.getRowFirstSemesterStart();
    }

    @Override
    public int findLastRow(Teacher teacher) {
        return teacher.getRowFirstSemesterEnd() + 1;
    }
}
