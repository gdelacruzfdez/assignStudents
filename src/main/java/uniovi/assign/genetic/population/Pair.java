package uniovi.assign.genetic.population;

/**
 * Class representing a pair of individuals to be crossed by the
 * crossover operator
 */
public class Pair {
    Individual parent1;
    Individual parent2;

    /**
     * Default constructor for a pair
     *
     * @param parent1 parent1 to be crossed
     * @param parent2 parent2 to be crossed
     */
    public Pair(Individual parent1, Individual parent2) {
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public Individual getParent1() {
        return parent1;
    }

    public Individual getParent2() {
        return parent2;
    }
}
