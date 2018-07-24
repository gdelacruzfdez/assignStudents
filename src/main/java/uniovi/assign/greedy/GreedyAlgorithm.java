package uniovi.assign.greedy;

import uniovi.assign.genetic.population.Individual;
import uniovi.assign.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singletone that contains the greedy algorithm for making the assignments of students to class groups.
 */
public class GreedyAlgorithm {

    private static GreedyAlgorithm instance;

    CollisionMatrix collisionMatrix;
    Map<Group, List<Group>> assigmentPreferences;
    List<Group> allGroups;
    Planification planification;

    private Map<Student, Integer> numberOfCollisions = new HashMap<>();

    /**
     * Returns the greedy algorithm instance.
     *
     * @return greedy algorithm instance.
     */
    public static GreedyAlgorithm getInstance() {
        if (instance == null) {
            instance = new GreedyAlgorithm();
        }
        return instance;
    }

    /**
     * Initialize the information needed by the greedy algorithm in order to execute
     *
     * @param collisionMatrix      collision matrix that contains the information about
     *                             the collisions between class groups.
     * @param allGroups            list containing all the groups that exist in the planification
     * @param assigmentPreferences assignment preferences determined by the user
     * @param planification        planification of the school
     */
    public void initialize(CollisionMatrix collisionMatrix, List<Group> allGroups, Map<Group, List<Group>> assigmentPreferences, Planification planification) {
        this.collisionMatrix = collisionMatrix;
        this.assigmentPreferences = assigmentPreferences;
        this.allGroups = allGroups;
        this.planification = planification;
    }

    /**
     * Private constructor for making the class a singletone
     */
    private GreedyAlgorithm() {

    }

    /**
     * Executes the greedy algorithm over an individual. It makes all the assigments in the order
     * that is specified by the individual. The algorithm tries to assign the class group with
     * lower number of students that does not collide with the current assignments of the student.
     * <p>
     * If it is not possible to make the assignment, a repairing process takes place trying to change
     * some assignments of the student in order to allow the new assignment.
     * When a group is assigned, the algorithms also tries to assign the set of preferences that the user
     * has introduced for the assigned group, if they exists.
     *
     * @param individual individual to be evaluated by the greedy algorithm
     *                   and whose assignments have to be made.
     * @return the individual with the list of assignments solved, the list of unsolved assignments
     * and some statistics about the quality of the generated solution.
     */
    public Individual execute(Individual individual) {
        List<Assignment> unsolvedAssignments = new ArrayList<>();
        List<Assignment> assignments = individual.getAssignments();

        resetAssignments(assignments);
        preprocessing(assignments);

        for (Assignment assignment : assignments) {
            if (assignment.getGroup() == null) {
                List<Assignment> collisions = new ArrayList<>();
                Group selectedGroup = getBestGroup(assignment, collisions);
                if (selectedGroup != null) {
                    assignment.setGroup(selectedGroup);
                    selectedGroup.addStudent(assignment.getStudent());
                    //ASSIGN PREFERENCES
                    assignPreferences(assignment, selectedGroup);
                } else {
                    // CANNOT ASSIGN, SOLVE COLLISIONS
                    repairing(unsolvedAssignments, assignment, collisions);
                }
            }
        }
        individual.setUnsolvedAssignments(unsolvedAssignments);
        for (Assignment unsolved : assignments) {
            if (numberOfCollisions.containsKey(unsolved.getStudent())) {
                numberOfCollisions.put(unsolved.getStudent(), numberOfCollisions.get(unsolved.getStudent()) + 1);
            } else {
                numberOfCollisions.put(unsolved.getStudent(), 1);
            }
        }

        calculateStatistics(individual);

        return individual;
    }

    /**
     * Reloads the assignments to reevaluate them.
     *
     * @param assignments to be reloaded.
     */
    private void resetAssignments(List<Assignment> assignments) {
        // Reset assignation groups and number of students in group
        assignments.parallelStream().forEach(assignation -> assignation.setGroup(null));
        allGroups.parallelStream().forEach(group -> group.resetStudents());
    }

    /**
     * Method that is in charge of assigning the user preferences taking into account
     * an assigned group
     *
     * @param assignment    assignment that has been done.
     * @param selectedGroup group that has been selected for the assignment.
     */
    private void assignPreferences(Assignment assignment, Group selectedGroup) {
        List<Group> preferences = assigmentPreferences.get(selectedGroup);
        if (preferences != null) {
            for (Group groupPreference : preferences) {
                for (Assignment studentAssignment : assignment.getStudent().getAssignments()) {
                    if (studentAssignment.getSubjectClass().equals(groupPreference.getSubjectClass()) && studentAssignment.getGroup() == null) {
                        Assignment assignmentCollided = getAssignationWithCollision(assignment.getStudent(), groupPreference);
                        if (assignmentCollided == null) {
                            studentAssignment.setGroup(groupPreference);
                            groupPreference.addStudent(studentAssignment.getStudent());
                        }
                    }
                }
            }
        }
    }

    /**
     * Repairing process for the greedy algorithm. When an assignment can be made because in collides
     * with other already evaluated student assignments, the repairing process try to change the conflictive assignments
     * to other groups until it is possible to make the first assifnment.
     *
     * @param unsolvedAssignments list of assignments that could not be solved until the moment.
     * @param assignment          assignment that the algorithm could not made because there were collisions with other assignments.
     * @param collisions          already evaluated assignments which whom the assignment the algorithm was trying to make collides.
     */
    private void repairing(List<Assignment> unsolvedAssignments, Assignment assignment, List<Assignment> collisions) {
        boolean solvedCollision = false;
        for (Assignment assignmentCollided : collisions) {
            Group newGroup = getBestGroup(assignmentCollided, null);
            if (newGroup != null) {
                assignmentCollided.getGroup().removeStudent(assignmentCollided.getStudent());
                assignmentCollided.setGroup(newGroup);
                newGroup.addStudent(assignmentCollided.getStudent());

                // CHECK IF WE CAN ASSIGN NOW THE NEW ASSIGNATION
                Group newAssignmentGroup = getBestGroup(assignment, null);
                if (newAssignmentGroup != null) {
                    assignment.setGroup(newAssignmentGroup);
                    newAssignmentGroup.addStudent(assignment.getStudent());
                    solvedCollision = true;
                    break;
                }
            }
        }
        if (!solvedCollision) {
            unsolvedAssignments.add(assignment);
        }
    }


    /**
     * Calculate some statistics about the quality of the individual evaluated. The calculated
     * statistics are the following:
     * - Total number of days with two hours or less of lessons.
     * - Total number of half an hour free slots between lessons.
     * - Total variance between the students of all the subjects.
     * - Total max variance between all the subjects.
     * - Maximum difference between the group with the highest number of students
     * and the one with the lower of a subject class.
     * - Total difference between the group with the highest number of students
     * and the one with the lower of all subject classes.
     * - Total variance between the differences between the group with the highest number of students
     * and the one with the lower of all subject classes.
     *
     * @param individual individual from which calculate the statistics.
     */
    private void calculateStatistics(Individual individual) {
        int totalDifferenceBetweeenGroups = 0;
        int totalNumberOfSubjectClasses = 0;
        for (Subject subject : planification.getSubjects().values()) {
            for (SubjectClass subjectClass : subject.getSubjectClasses()) {
                totalNumberOfSubjectClasses++;
                totalDifferenceBetweeenGroups += subjectClass.getMaximumDiferenceBetweenGroups();
            }
        }

        double sum = 0;
        double meanDifference = totalDifferenceBetweeenGroups / totalNumberOfSubjectClasses;
        for (Subject subject : planification.getSubjects().values()) {
            for (SubjectClass subjectClass : subject.getSubjectClasses()) {
                sum += Math.pow((subjectClass.getMaximumDiferenceBetweenGroups() - meanDifference), 2);
            }
        }

        double variance = sum / (totalNumberOfSubjectClasses - 1);
        double maximumDifference = 0;
        double maxVariance = 0;
        double totalVariance = 0;
        for (Subject subject : planification.getSubjects().values()) {
            for (SubjectClass subjectClass : subject.getSubjectClasses()) {
                if (subjectClass.getMaximumDiferenceBetweenGroups() > maximumDifference) {
                    maximumDifference = subjectClass.getMaximumDiferenceBetweenGroups();
                }
                if (subjectClass.getGroupsVariance() > maxVariance) {
                    maxVariance = subjectClass.getGroupsVariance();
                }
                totalVariance += subjectClass.getGroupsVariance();
            }
        }

        int totalFreeSlots = 0;
        int totalDaysWithLoNumOfClasses = 0;
        for (Student student : planification.getStudents()) {
            int[] numberOfFreeHoursAndDaysWithLowClasses = student.calculateNumberOfFreeHoursAndDaysWithLowNumOfClasses();
            totalFreeSlots += numberOfFreeHoursAndDaysWithLowClasses[0];
            totalDaysWithLoNumOfClasses += numberOfFreeHoursAndDaysWithLowClasses[1];
        }
        individual.setTotalDaysWithLowNumOfClasses(totalDaysWithLoNumOfClasses);
        individual.setTotalNumberOfFreeSlots(totalFreeSlots);
        individual.setTotalVariance(totalVariance);
        individual.setMaxVariance(maxVariance);
        individual.setMaxDifferenceBetweenGroups(maximumDifference);
        individual.setTotalDifferenceBetweenGroups(totalDifferenceBetweeenGroups);
        individual.setVarianceBetweenDifferences(variance);
    }

    /**
     * Preprocessing of assignments. Assigns directly those assignments that
     * only have one group that can be assigned.
     *
     * @param assignments list of all assignments to be processed.
     */
    private void preprocessing(List<Assignment> assignments) {
        for (Assignment assignment : assignments) {
            // Assign subjects that only have one group
            if (assignment.getSubjectClass().getGroups().size() == 1) {
                Group selectedGroup = assignment.getSubjectClass().getGroups().get(0);
                assignment.setGroup(selectedGroup);
                selectedGroup.addStudent(assignment.getStudent());
            }
        }
    }


    /**
     * Returns the best group for an assignment. Returns the group that has the lowest number of students
     * that does not collide with any of the current assignments of the student.
     *
     * @param assignment assignment for which we want to find the best group.
     * @param collisions list in which in case the assignment cannot be done the already assigned assignments that collide with the
     *                   new assignment will be stored.
     * @return the best group for the evaluated assignment.
     */
    private Group getBestGroup(Assignment assignment, List<Assignment> collisions) {
        int minNumberOfStudents = Integer.MAX_VALUE;
        Group selectedGroup = null;
        for (Group group : assignment.getSubjectClass().getGroups()) {
            if (minNumberOfStudents > group.getNumberOfStudents()) {
                Assignment assignmentCollided = getAssignationWithCollision(assignment.getStudent(), group);
                if (assignmentCollided == null) {
                    selectedGroup = group;
                    minNumberOfStudents = group.getNumberOfStudents();
                } else {
                    if (collisions != null) {
                        collisions.add(assignmentCollided);
                    }
                }
            }
        }
        return selectedGroup;
    }

    /**
     * Returns the assignment of a student that collide with an specific group
     *
     * @param student student from which we want to know the assignment that collides with the group passed as parameter.
     * @param group   group from which we want to know the student assingment that collides with it.
     * @return assignment of the student passed as parameter that collides with the group passed as parameter.
     */
    private Assignment getAssignationWithCollision(Student student, Group group) {
        List<Group> collidedGroups = collisionMatrix.getCollidedGroups(group);
        for (Assignment studentAssignment : student.getAssignments()) {
            if (collidedGroups.contains(studentAssignment.getGroup())) {
                return studentAssignment;
            }
        }
        return null;
    }
}
