package uniovi.assign.genetic.encoding;

import uniovi.assign.model.Assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

/**
 * Class in charge of encoding and decoding the individuals managed by the genetic algorithm
 */
public class EncoderImpl implements Encoder {


    List<Assignment> allAssignments;
    Map<Assignment, Integer> positions;


    public EncoderImpl(List<Assignment> allAssignments) {
        this.allAssignments = allAssignments;
        this.positions = IntStream.range(0, allAssignments.size())
                .boxed()
                .collect(toMap(allAssignments::get, i -> i));
    }


    /**
     * Encodes the list of assignment that represent an individual into an
     * array of integers value representing the position of the assignments in
     * the master list of asssignments.
     *
     * @param chromosomeDecoded assignment to be encoded
     * @return encoded assignment as an array of integers
     */
    @Override
    public int[] encode(List<Assignment> chromosomeDecoded) {
        int chromosomeEncoded[] = new int[chromosomeDecoded.size()];
        for (int i = 0; i < chromosomeDecoded.size(); i++) {
            Assignment assignment = chromosomeDecoded.get(i);
            int position = positions.get(assignment);
            chromosomeEncoded[i] = position;

        }
        return chromosomeEncoded;
    }

    /**
     * Decodes a chromosome representing an individual by an array of integers
     * into a list of assignations.
     *
     * @param chromosomeEncoded encoded individual represented as an array of integers.
     * @return list of assignments of the individual.
     */
    @Override
    public List<Assignment> decode(int[] chromosomeEncoded) {
        List<Assignment> chromosomeDecoded = new ArrayList<>();
        for (int i = 0; i < chromosomeEncoded.length; i++) {
            int position = chromosomeEncoded[i];
            chromosomeDecoded.add(allAssignments.get(position));
        }

        return chromosomeDecoded;
    }
}
