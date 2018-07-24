package uniovi.assign.genetic.selection;

import uniovi.assign.genetic.population.Pair;
import uniovi.assign.genetic.population.Population;

import java.util.List;

/**
 * Interface that must implement all the classes representing selection operators.
 */
public interface Selection {
    /**
     * Generate a list of pairs to cross from the individuals that compose the population.
     *
     * @param population population from which we want to generate the pairs.
     * @return list of pairs to be crossed.
     */
    List<Pair> selectPairs(Population population);
}
