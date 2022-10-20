package com.projectuexcel.table.export;

import com.projectuexcel.table.Plan;
import com.projectuexcel.table.Teacher;

public class TeacherExporterSecondSemester extends TeacherExporter {

    public TeacherExporterSecondSemester(Plan plan) {
        super(plan);
    }

    @Override
    public int findFirstRow(Teacher teacher) {
        return teacher.getRowSecondSemesterStart();
    }

    @Override
    public int findLastRow(Teacher teacher) {
        return teacher.getRowSecondSemesterEnd() + 1;
    }
}
