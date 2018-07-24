package uniovi.assign.genetic.selection;

import uniovi.assign.genetic.population.Individual;
import uniovi.assign.genetic.population.Pair;
import uniovi.assign.genetic.population.Population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that selects random pairs of individuals for crossover
 */
public class RandomPairsSelection implements Selection {
    /**
     * Generate randoms pairs from the population. All the individuals
     * would be crossed with another one from the population.
     *
     * @param population population from which we want to generate the pairs.
     * @return list of pairs to be crossed.
     */
    @Override
    public List<Pair> selectPairs(Population population) {
        List<Individual> individuals = population.getIndividuals();

        List<Individual> randomized = new ArrayList<>(individuals);
        Collections.shuffle(randomized);
        List<Pair> pairs = new ArrayList<>();

        for (int i = 0; i < randomized.size(); i += 2) {
            Pair pair = new Pair(randomized.get(i), randomized.get(i + 1));
            pairs.add(pair);
        }

        return pairs;
    }
}
