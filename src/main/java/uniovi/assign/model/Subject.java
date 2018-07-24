package uniovi.assign.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Model class that represents one subject of the school.
 */
public class Subject {

    public static Map<String, String> subjectCodes;

    public static final String ENGLISH = "S";
    public static final String SPANISH = "N";

    String subjectId;
    String subjectName;
    String course;
    String semester;
    String language;

    List<SubjectClass> subjectClasses = new ArrayList<>();

    /**
     * Default constructor for subject objects.
     *
     * @param subjectName name of the subject.
     * @param course      course of the subject.
     * @param semester    semester in which the subject is taught.
     * @param language    language in which the subject is taught.
     */
    public Subject(String subjectName, String course, String semester, String language) {
        this.subjectName = subjectName;
        this.course = course;
        this.semester = semester;
        this.subjectId = subjectCodes.get(subjectName);
        this.language = language;
        if (language.equals(ENGLISH)) {
            this.subjectId += ".I";
        }
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getCourse() {
        return course;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void addSubjectClass(SubjectClass subjectClass) {
        this.subjectClasses.add(subjectClass);
    }

    public List<SubjectClass> getSubjectClasses() {
        return subjectClasses;
    }
}
