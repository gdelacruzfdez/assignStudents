package uniovi.assign.genetic.fitnessfunction;

import uniovi.assign.genetic.population.Individual;
import uniovi.assign.greedy.GreedyAlgorithm;

/**
 * Default fitness function used by the Genetic Algorithm
 */
public class FitnessFunctionImpl implements FitnessFunction {
    /**
     * Calculates the fitness of an individual taking into account the
     * number of unsolved assignations, the total variance, the max
     * difference between groups, the total number of free slots
     * and the number of days with less than two hours of lessons.
     * All the factors are weighted.
     *
     * @param individual individual we want to calculate the fitness from.
     * @return a number representing the fitness of the individual (greater is better).
     */
    @Override
    public double calculateFitness(Individual individual) {
        GreedyAlgorithm greedyAlgorithm = GreedyAlgorithm.getInstance();
        greedyAlgorithm.execute(individual);
        return 100 * individual.getUnsolvedAssignments().size() + 1 * individual.getTotalVariance() + individual.getMaxDifferenceBetweenGroups() * 10 + individual.getTotalNumberOfFreeSlots() * 0.08 + individual.getTotalDaysWithLowNumOfClasses() * 0.2;
    }
}
