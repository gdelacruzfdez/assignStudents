package uniovi.assign.genetic.fitnessfunction;

import uniovi.assign.genetic.population.Individual;
import uniovi.assign.greedy.GreedyAlgorithm;

/**
 * Fitness function implementation based in Individuals variance between group
 * number of students differences.
 */
public class VarianceDifferenceFF implements FitnessFunction {
    /**
     * Calculates the fitness of an individual taking into account the
     * group number of students differences and the number of unsolved assignations.
     *
     * @param individual individual we want to calculate the fitness from.
     * @return a number representing the fitness of the individual (greater is better).
     */
    @Override
    public double calculateFitness(Individual individual) {
        GreedyAlgorithm greedyAlgorithm = GreedyAlgorithm.getInstance();
        greedyAlgorithm.execute(individual);
        return 100 * individual.getUnsolvedAssignments().size() + 1 * individual.getVarianceBetweenDifferences();
    }
}
