package uniovi.assign.genetic.population;

import uniovi.assign.model.Assignment;

import java.util.List;


/**
 * Class that represents an individual treated by the genetic algorithm.
 * It represents a solution to the problem.
 */
public class Individual {

    List<Assignment> assignments;
    List<Assignment> unsolvedAssignments;
    int totalDifferenceBetweenGroups = 0;
    int[] encoding;
    double fitness;
    private double maxDifferenceBetweenGroups;
    private double varianceBetweenDifferences;
    private double maxVariance;
    private double totalVariance;
    private double totalNumberOfFreeSlots;
    private double totalDaysWithLowNumOfClasses;


    /**
     * Default constructor for an individual.
     *
     * @param assignments list of assignments representing the individuals.
     */
    public Individual(List<Assignment> assignments) {
        this.assignments = assignments;
    }


    public List<Assignment> getAssignments() {
        return assignments;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public List<Assignment> getUnsolvedAssignments() {
        return unsolvedAssignments;
    }

    public void setUnsolvedAssignments(List<Assignment> unsolvedAssignments) {
        this.unsolvedAssignments = unsolvedAssignments;
    }

    public int[] getEncoding() {
        return encoding;
    }

    public void setEncoding(int[] encoding) {
        this.encoding = encoding;
    }

    public int getTotalDifferenceBetweenGroups() {
        return totalDifferenceBetweenGroups;
    }

    public void setTotalDifferenceBetweenGroups(int totalDifferenceBetweenGroups) {
        this.totalDifferenceBetweenGroups = totalDifferenceBetweenGroups;
    }

    public void setMaxDifferenceBetweenGroups(double maxDifferenceBetweenGroups) {
        this.maxDifferenceBetweenGroups = maxDifferenceBetweenGroups;
    }

    public double getMaxDifferenceBetweenGroups() {
        return maxDifferenceBetweenGroups;
    }


    public void setVarianceBetweenDifferences(double varianceBetweenDifferences) {
        this.varianceBetweenDifferences = varianceBetweenDifferences;
    }

    public double getVarianceBetweenDifferences() {
        return varianceBetweenDifferences;
    }

    public double getMaxVariance() {
        return maxVariance;
    }

    public void setMaxVariance(double maxVariance) {
        this.maxVariance = maxVariance;
    }

    public void setTotalVariance(double totalVariance) {
        this.totalVariance = totalVariance;
    }

    public double getTotalVariance() {
        return totalVariance;
    }

    public double getTotalNumberOfFreeSlots() {
        return totalNumberOfFreeSlots;
    }

    public void setTotalNumberOfFreeSlots(double totalNumberOfFreeSlots) {
        this.totalNumberOfFreeSlots = totalNumberOfFreeSlots;
    }

    public void setTotalDaysWithLowNumOfClasses(double totalDaysWithLowNumOfClasses) {
        this.totalDaysWithLowNumOfClasses = totalDaysWithLowNumOfClasses;
    }

    public double getTotalDaysWithLowNumOfClasses() {
        return totalDaysWithLowNumOfClasses;
    }

    public int getNumberOfCollisions() {
        return unsolvedAssignments.size();
    }
}
