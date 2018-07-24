package uniovi.assign.genetic.population;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents a population of individuals in the genetic algorithm
 */
public class Population {

    List<Individual> individuals;
    private double meanFitness;
    private double meanNumberOfCollisions;
    private double meanTotalVariance;
    private double meanMaxDifferenceBetweenGroups;
    private double meanSumOfDifferencesBetweenGroups;
    private double meanTotalNumberOfFreeSlots;
    private double meanTotalDaysWithLessThan2Hours;

    /**
     * Default constructor for a population of individuals. It calculates the mean values of the quality factors
     * of the population.
     *
     * @param individuals list of individuals that will compose the population.
     */
    public Population(List<Individual> individuals) {
        this.individuals = individuals.parallelStream().sorted(Comparator.comparingDouble(Individual::getFitness)).collect(Collectors.toList());
        this.meanFitness = individuals.parallelStream().mapToDouble(individual -> individual.getFitness()).average().getAsDouble();
        this.meanNumberOfCollisions = individuals.parallelStream().mapToDouble(individual -> individual.getNumberOfCollisions()).average().getAsDouble();
        this.meanTotalVariance = individuals.parallelStream().mapToDouble(individual -> individual.getTotalVariance()).average().getAsDouble();
        this.meanMaxDifferenceBetweenGroups = individuals.parallelStream().mapToDouble(individual -> individual.getMaxDifferenceBetweenGroups()).average().getAsDouble();
        this.meanSumOfDifferencesBetweenGroups = individuals.parallelStream().mapToDouble(individual -> individual.getTotalDifferenceBetweenGroups()).average().getAsDouble();
        this.meanTotalNumberOfFreeSlots = individuals.parallelStream().mapToDouble(individual -> individual.getTotalNumberOfFreeSlots()).average().getAsDouble();
        this.meanTotalDaysWithLessThan2Hours = individuals.parallelStream().mapToDouble(individual -> individual.getTotalDaysWithLowNumOfClasses()).average().getAsDouble();
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public double getBestFitness() {
        return individuals.get(0).getFitness();
    }

    public int getPopulationSize() {
        return individuals.size();
    }

    public double getMeanFitness() {
        return meanFitness;
    }

    public double getMeanNumberOfCollisions() {
        return meanNumberOfCollisions;
    }

    public double getMeanMaxDifferenceBetweenGroups() {
        return meanMaxDifferenceBetweenGroups;
    }

    public double getMeanSumOfDifferencesBetweenGroups() {
        return meanSumOfDifferencesBetweenGroups;
    }

    public double getMeanTotalNumberOfFreeSlots() {
        return meanTotalNumberOfFreeSlots;
    }

    public double getMeanTotalDaysWithLessThan2Hours() {
        return meanTotalDaysWithLessThan2Hours;
    }

    public double getMeanTotalVariance() {
        return meanTotalVariance;
    }
}
