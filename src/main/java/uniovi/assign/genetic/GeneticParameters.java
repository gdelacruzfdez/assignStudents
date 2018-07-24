package uniovi.assign.genetic;

/**
 * Class that encapsulate the different parameters for the execution of the genetic algorith,
 * It includes default recommended values, but can be modified by the user.
 * The default values are the following:
 * <p>
 * numberOfGenerations = 1500
 * crossoverProb = 0.95
 * mutationProb = 0.05
 * populationSize = 200
 * semester = S1
 */
public class GeneticParameters {

    int numberOfGenerations = 1500;
    double crossoverProb = 0.95;
    double mutationProb = 0.05;
    int populationSize = 200;
    String semester = "S1";


    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }

    public void setNumberOfGenerations(int numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public double getCrossoverProb() {
        return crossoverProb;
    }

    public void setCrossoverProb(double crossoverProb) {
        this.crossoverProb = crossoverProb;
    }

    public double getMutationProb() {
        return mutationProb;
    }

    public void setMutationProb(double mutationProb) {
        this.mutationProb = mutationProb;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        if (populationSize % 2 != 0) {
            populationSize++;
        }
        this.populationSize = populationSize;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        if (semester.equals("S1") || semester.equals("S2")) {
            this.semester = semester;
        }
    }
}
