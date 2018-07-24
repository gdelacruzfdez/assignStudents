package uniovi.assign.genetic.crossover;

import uniovi.assign.genetic.population.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Performs Order crossover operator over a pair of individuals
 */
public class OXCrossover implements Crossover {

    /**
     * Performs Order Crossover (OX) operator over a pair of individuals
     *
     * @param pair pair of individuals to cross
     * @return list containing two new invividuals encoded
     */
    @Override
    public List<int[]> crossover(Pair pair) {
        int parent1[] = pair.getParent1().getEncoding();
        int parent2[] = pair.getParent2().getEncoding();
        List<int[]> descendants = new ArrayList<>();

        int startingPoint = ThreadLocalRandom.current().nextInt(0, (int) (parent1.length * 0.8));
        int endPoint = ThreadLocalRandom.current().nextInt(startingPoint, parent1.length);

        int descendant1[] = getIndividual(parent1, parent2, startingPoint, endPoint);
        int descendant2[] = getIndividual(parent2, parent1, startingPoint, endPoint);
        descendants.add(descendant1);
        descendants.add(descendant2);

        return descendants;
    }

    /**
     * Generate a new individual crossing the parents at the points passed as parameters
     *
     * @param parent1       parent1 to cross
     * @param parent2       parent2 to cross
     * @param startingPoint start point in which the individuals are going to be crossed.
     * @param endPoint      end point in which the individuals are going to be crossed.
     * @return a new individual encoded generated from the encoding of the parents.
     */
    private int[] getIndividual(int[] parent1, int[] parent2, int startingPoint, int endPoint) {
        Set<Integer> parent1Genes = new HashSet<>();
        int descendant[] = new int[parent1.length];

        for (int i = startingPoint; i <= endPoint; i++) {
            descendant[i] = parent1[i];
            parent1Genes.add(parent1[i]);
        }

        int positionParent2 = 0;

        for (int i = 0; i < startingPoint; i++) {

            while (parent1Genes.contains(parent2[positionParent2])) {
                if (positionParent2 + 1 < parent2.length) {
                    positionParent2++;
                } else {
                    break;
                }
            }

            descendant[i] = parent2[positionParent2];
            positionParent2++;
        }

        for (int i = endPoint + 1; i < descendant.length; i++) {

            while (parent1Genes.contains(parent2[positionParent2])) {
                if (positionParent2 + 1 < parent2.length) {
                    positionParent2++;
                } else {
                    break;
                }
            }

            descendant[i] = parent2[positionParent2];
            positionParent2++;
        }

        return descendant;
    }
}
