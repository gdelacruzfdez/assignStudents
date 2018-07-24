package uniovi.assign.genetic.mutation;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Class that is in charge of mutate individuals
 */
public class MutationImpl implements Mutation {
    /**
     * Mutate an individual by swaping between 0% and 5% of the positions
     * in the integer array representing the individual.
     *
     * @param chromosome chromosome to mutate.
     * @return mutated chromosome.
     */
    @Override
    public int[] mutate(int[] chromosome) {
        int numberOfMutations = ThreadLocalRandom.current().nextInt(0, (int) (chromosome.length * 0.05));
        for (int i = 0; i < numberOfMutations; i++) {
            int positionGene1 = ThreadLocalRandom.current().nextInt(0, chromosome.length);
            int positionGene2 = ThreadLocalRandom.current().nextInt(0, chromosome.length);
            while (positionGene1 == positionGene2) {
                positionGene2 = ThreadLocalRandom.current().nextInt(0, chromosome.length);
            }
            int valueGene2 = chromosome[positionGene2];
            chromosome[positionGene2] = chromosome[positionGene1];
            chromosome[positionGene1] = valueGene2;
        }
        return chromosome;
    }
}
