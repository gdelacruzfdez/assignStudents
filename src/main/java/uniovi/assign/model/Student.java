package uniovi.assign.model;

import de.vandermeer.asciitable.AsciiTable;
import uniovi.assign.parser.StudentListRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class that represents a student that is enrolled in the school.
 */
public class Student {

    String id;
    String firstSurname;
    String secondSurname;
    String name;
    int numberOfSubjects;
    List<Subject> subjects;
    List<Assignment> assignments;


    /**
     * Default constructor for an student. It receives a StudentListRow object
     * and take all the information from it.
     *
     * @param studentListRow student list row from which initialize the student object.
     */
    public Student(StudentListRow studentListRow) {
        this.id = studentListRow.getId();
        this.firstSurname = studentListRow.getFirstSurname();
        this.secondSurname = studentListRow.getSecondSurname();
        this.name = studentListRow.getName();
        this.numberOfSubjects = studentListRow.getNumberOfSubjects();
        this.subjects = new ArrayList<>();
        this.assignments = new ArrayList<>();
    }

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

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    public void addAssignation(Assignment assignment) {
        this.assignments.add(assignment);
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    /**
     * Prints the timetable of the student into console.
     */
    public void printTimetable() {
        String result = getStudentTimetable();
        System.out.println(result);
    }

    /**
     * Generates an ascii table containing the timetable of the student.
     *
     * @return an string that represents an ascii table containing the timetable of the student.
     */
    public String getStudentTimetable() {
        List<List<String>> rows = new ArrayList<>();
        for (int hour = 9; hour < 21; hour++) {
            for (int minutes = 0; minutes < 60; minutes += 30) {
                List<String> row = new ArrayList<>();
                row.add(hour + ":" + minutes);
                rows.add(row);
                for (int day = 0; day < 7; day++) {
                    SimplifiedDate date = new SimplifiedDate(day, hour, minutes);
                    Group group = getClassAtDate(date);
                    if (group != null) {
                        row.add(group.getGroupId());
                    } else {
                        row.add("");
                    }
                }

            }
        }
        AsciiTable table = new AsciiTable();
        List<String> firstRow = new ArrayList<>();
        for (int i = 0; i < rows.get(0).size() - 1; i++) {
            firstRow.add(null);
        }
        firstRow.add("TIMETABLE OF ID: " + getId() + " | " + getName() + " " + getFirstSurname() + " " + getSecondSurname());
        table.addRule();
        table.addRow(firstRow);
        table.addRule();
        for (List<String> row : rows) {
            table.addRow(row);
            table.addRule();
        }

        return table.render();
    }

    /**
     * Returns the class that the student has at an specific moment.
     *
     * @param date date in which we want to know what class has the student at that time.
     * @return group where the student has class at that time, null if the student does not have class at that moment.
     */
    private Group getClassAtDate(SimplifiedDate date) {
        for (Assignment assignment : assignments) {
            Group group = assignment.getGroup();
            if (group != null) {
                for (Class cl : group.getClasses()) {
                    if ((cl.getStartDate().equals(date) || cl.getStartDate().before(date)) && cl.getEndDate().after(date)) {
                        return group;
                    }
                }

            }
        }
        return null;
    }

    /**
     * Calculates the total number of half an hour free slots that the student have during all the week
     * and the total number of days in which the student has only two hours of lessons or less.
     *
     * @return an array containing in index 0 the total number of free slots and in index 1 the total number of days
     * with two hours of lessons or less.
     */
    public int[] calculateNumberOfFreeHoursAndDaysWithLowNumOfClasses() {
        int[] results = new int[2];
        boolean[][] timeTable = generateBooleanWeekTimetable();
        int totalFreeSlots = 0;
        int daysWithLowClasses = 0;
        for (int day = 0; day < 7; day++) {
            int accumulator = 0;
            int totalClassesInDay = 0;
            boolean hasClass = false;
            for (int slot = 0; slot < 48; slot++) {
                if (timeTable[slot][day] && hasClass) {
                    totalFreeSlots += accumulator;
                    totalClassesInDay++;
                    accumulator = 0;
                } else if (!timeTable[slot][day] && hasClass) {
                    accumulator++;
                } else if (timeTable[slot][day]) {
                    hasClass = true;
                    totalClassesInDay++;
                }
            }
            if (totalClassesInDay <= 4 && totalClassesInDay != 0) {
                daysWithLowClasses++;
            }
        }
        results[0] = totalFreeSlots;
        results[1] = daysWithLowClasses;
        return results;
    }


    /**
     * Generate a 48x7 matrix with boolean values in which they are represented the 48 slots of half an hour that a day has
     * and the s7 days of the week. It generates a matrix indicating in which intervals the student has lessons.
     *
     * @return a 48x7 matrix filled with boolean values in which true means that the student has class in that timeslot and false
     * meand that he has not class in that timeslot.
     */
    private boolean[][] generateBooleanWeekTimetable() {
        boolean[][] timetable = new boolean[48][7];
        for (Assignment assignment : assignments) {
            if (assignment.getGroup() != null) {
                for (Class cl : assignment.getGroup().getClasses()) {
                    SimplifiedDate startDate = cl.getStartDate();
                    int classDuration = cl.getClassDurationInMinutes();
                    int startPoint = startDate.hour * 2 + (startDate.minutes / 30);
                    int endPoint = startPoint + (classDuration / 30);
                    for (int i = startPoint; i < endPoint; i++) {
                        timetable[i][startDate.dayOfTheWeek] = true;
                    }
                }
            }
        }
        return timetable;
    }

    /**
     * Returns the name of the student with surnames.
     *
     * @return the complete name of the student.
     */
    public String getCompleteName() {
        return name + " " + firstSurname + " " + secondSurname;
    }

    /**
     * Overridden equals method to compare students by their id
     *
     * @param other student to know if it is the same as another or not.
     * @return true if objects are the same, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Student)) return false;
        Student otherStudent = (Student) other;
        return otherStudent.id.equals(this.id);
    }
}
