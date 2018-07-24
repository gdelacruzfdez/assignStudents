package uniovi.assign.model;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private String groupId;
    private int maxCapacity = 15;
    private List<uniovi.assign.model.Class> classes = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private SubjectClass subjectClass;

    public Group(String groupId, List<uniovi.assign.model.Class> classes) {
        this.groupId = groupId;
        this.classes = classes;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public SubjectClass getSubjectClass() {
        return subjectClass;
    }

    public void setSubjectClass(SubjectClass subjectClass) {
        this.subjectClass = subjectClass;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public boolean collide(Group group) {
        for (uniovi.assign.model.Class lecture : classes) {
            for (uniovi.assign.model.Class lecture1 : group.getClasses()) {
                if (lecture.getStartDate().before(lecture1.getEndDate()) && lecture.getEndDate().after(lecture1.getStartDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSameGroup(Group group) {
        return groupId.equals(group.groupId);
        //return startDate.equals(group.startDate) && endDate.equals(group.endDate);
    }


    public boolean isFull() {
        return students.size() >= maxCapacity;
    }

    public int getNumberOfStudents() {
        return students.size();
    }

    public void addStudent(Student newStudent) {
        this.students.add(newStudent);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
    }

    public void resetStudents() {
        this.students = new ArrayList<>();
    }


    public List<uniovi.assign.model.Class> getClasses() {
        return classes;
    }

    public void setClasses(List<uniovi.assign.model.Class> classes) {
        this.classes = classes;
    }
}
