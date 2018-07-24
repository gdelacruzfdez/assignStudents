package uniovi.assign.model;

import java.util.HashMap;
import java.util.List;

/**
 * Class that represents the complete planification of the school. It contains the information
 * about the students and its enrollments and all the subjects with the information about the
 * subject classes and groups.
 */
public class Planification {
    List<Student> students;
    HashMap<String, Subject> subjects;

    /**
     * Default constructor for a planification object.
     *
     * @param students students that have to be assigned a group for each subject.
     * @param subjects subjects that are taught in the school.
     */
    public Planification(List<Student> students, HashMap<String, Subject> subjects) {
        this.students = students;
        this.subjects = subjects;
    }

    public List<Student> getStudents() {
        return students;
    }

    public HashMap<String, Subject> getSubjects() {
        return subjects;
    }

    public Subject getSubject(String subjectID) {
        return subjects.get(subjectID);
    }


    /**
     * Assign to each subject the subjectclasses parsed by the CSV that contains the school timetable.
     *
     * @param subjectClasses list of subjectclasses parsed from the CSV that contains the school timetable.
     */
    public void assignClassesToSubjects(List<SubjectClass> subjectClasses) {
        for (SubjectClass subjectClass : subjectClasses) {
            String[] splittedName = subjectClass.getSubjectName().split("\\.");
            String subjectID = splittedName[0];
            if (splittedName.length > 2 && splittedName[2].contains("I")) {
                subjectID += ".I";
            }
            Subject subject = getSubject(subjectID);
            if (subject != null) {
                subject.addSubjectClass(subjectClass);
                subjectClass.setSubject(subject);
            }
        }
    }

}
