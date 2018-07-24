package uniovi.assign.parser;

import com.poiji.annotation.ExcelCell;
import uniovi.assign.model.Subject;

/**
 * Class that represents a row from the excel file containing the student enrollments.
 */
public class StudentListRow {

    @ExcelCell(0)
    private String id;

    @ExcelCell(1)
    private String firstSurname;

    @ExcelCell(2)
    private String secondSurname;

    @ExcelCell(3)
    private String name;

    @ExcelCell(4)
    private int numberOfSubjects;

    @ExcelCell(5)
    private String subjectInEnglish;

    @ExcelCell(6)
    private String subjectName;

    @ExcelCell(7)
    private String course;

    @ExcelCell(8)
    private String semester;

    public String getId() {
        return id;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public String getSecondSurname() {
        return secondSurname;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfSubjects() {
        return numberOfSubjects;
    }

    public String getSubjectInEnglish() {
        return subjectInEnglish;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getCourse() {
        return course;
    }

    public String getSemester() {
        return semester;
    }

    public String getSubjectId() {
        String ret = Subject.subjectCodes.get(subjectName);
        if (subjectInEnglish.equals(Subject.ENGLISH)) {
            ret += ".I";
        }
        return ret;
    }
}
