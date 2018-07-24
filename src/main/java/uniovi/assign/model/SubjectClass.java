package uniovi.assign.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Model class that represent one of the classes that are taught in a subject (theoretical, laboratory or seminar classes)
 */
public class SubjectClass {

    private Subject subject;
    private String subjectName;
    private List<Group> groups;

    /**
     * Default constructor for a subject class.
     *
     * @param subjectName name of the subject class.
     */
    public SubjectClass(String subjectName) {
        this.subjectName = subjectName;
        this.groups = new ArrayList<>();
    }

    public String getSubjectName() {
        return subjectName;
    }

    public List<Group> getGroups() {
        return groups;
    }


    public void addGroup(Group group) {
        this.groups.add(group);
    }

    /**
     * Calculates the difference between the group that has the lowest number of students and the one that has the highest.
     *
     * @return * Calculates the difference between the group that has the lowest number of students and the one that has the highest.
     */
    public int getMaximumDiferenceBetweenGroups() {
        int min = groups.parallelStream().min(Comparator.comparingInt(group -> group.getNumberOfStudents())).get().getNumberOfStudents();
        int max = groups.parallelStream().max(Comparator.comparingInt(group -> group.getNumberOfStudents())).get().getNumberOfStudents();
        return max - min;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Returns the total number of students in the subjectclass.
     *
     * @return total number of students in the subjectclass.
     */
    public int getTotalNumberOfStudents() {
        return groups.parallelStream().mapToInt(group -> group.getNumberOfStudents()).sum();
    }

    /**
     * Returns the variance between the number of students in the groups of the subjectclass.
     *
     * @return variance between the number of students in the groups of the subjectclass.
     */
    public double getGroupsVariance() {
        double totalNumberOfStudents = 0;
        for (Group group : groups) {
            totalNumberOfStudents += group.getNumberOfStudents();
        }
        double meanNumberOfStudents = totalNumberOfStudents / groups.size();

        double sum = 0;
        for (Group group : groups) {
            sum += Math.pow((group.getNumberOfStudents() - meanNumberOfStudents), 2);
        }


        return sum / groups.size();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof SubjectClass)) return false;
        SubjectClass otherSuject = (SubjectClass) other;
        return otherSuject.subjectName.equals(this.subjectName);
    }

}
