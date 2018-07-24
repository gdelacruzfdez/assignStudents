package uniovi.assign.genetic.crossover;

import uniovi.assign.genetic.population.Pair;

import java.util.List;

/**
 * Interface that Crossover Operators must implement
 */
public interface Crossover {

    /**
     * Performs crossover operator over a pair of individuals
     *
     * @param pair pair of individuals to cross
     * @return list containing two new invividuals encoded
     */
    List<int[]> crossover(Pair pair);
}
