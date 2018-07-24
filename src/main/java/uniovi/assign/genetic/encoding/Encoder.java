package uniovi.assign.genetic.encoding;

import uniovi.assign.model.Assignment;

import java.util.List;

/**
 * Interface that must extend the classes in charge of encoding and decoding the individuals managed by the genetic algorithm
 */
public interface Encoder {

    /**
     * Encodes the list of assignment that represent an individual into an
     * array of integers value representing the position of the assignments in
     * the master list of asssignments.
     *
     * @param assignment assignment to be encoded
     * @return encoded assignment as an array of integers
     */
    int[] encode(List<Assignment> assignment);

    /**
     * Decodes a chromosome representing an individual by an array of integers
     * into a list of assignations.
     *
     * @param chromosome encoded individual represented as an array of integers.
     * @return list of assignments of the individual.
     */
    List<Assignment> decode(int[] chromosome);

}
