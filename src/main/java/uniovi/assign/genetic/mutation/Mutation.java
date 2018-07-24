package uniovi.assign.genetic.mutation;

/**
 * Interface that must be implemented by all the classes that represent mutation operators for the Genetic Algorithm.
 */
public interface Mutation {

    /**
     * Mutates the chromosome.
     *
     * @param chromosome chromosome to mutate.
     * @return mutated chromosome.
     */
    int[] mutate(int[] chromosome);
}
