package uniovi.assign.genetic.fitnessfunction;

import uniovi.assign.genetic.population.Individual;

/**
 * Interface that must implement all the classes that represent alternatives for the fitness function
 */
public interface FitnessFunction {

    /**
     * Calculates the fitness of an individual
     *
     * @param individual individual we want to calculate the fitness from
     * @return a number representing the fitness of the individual (greater is better)
     */
    double calculateFitness(Individual individual);
}
