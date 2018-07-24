package uniovi.assign.model;

/**
 * Model class for representing an assignment of a group for a student in a determined subject class
 */
public class Assignment {
    Student student;
    SubjectClass subjectClass;
    Group group;

    /**
     * Default constructor for an assignment.
     *
     * @param student      student to whom it is necessary to make the assigment.
     * @param subjectClass subject class in which the student must be assigned a group.
     */
    public Assignment(Student student, SubjectClass subjectClass) {
        this.student = student;
        this.subjectClass = subjectClass;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Student getStudent() {
        return student;
    }

    public SubjectClass getSubjectClass() {
        return subjectClass;
    }

    public Group getGroup() {
        return group;
    }
}
