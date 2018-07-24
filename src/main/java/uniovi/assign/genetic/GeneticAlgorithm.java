package uniovi.assign.genetic;

import uniovi.assign.genetic.crossover.Crossover;
import uniovi.assign.genetic.crossover.OXCrossover;
import uniovi.assign.genetic.encoding.Encoder;
import uniovi.assign.genetic.encoding.EncoderImpl;
import uniovi.assign.genetic.fitnessfunction.FitnessFunction;
import uniovi.assign.genetic.fitnessfunction.FitnessFunctionImpl;
import uniovi.assign.genetic.mutation.Mutation;
import uniovi.assign.genetic.mutation.MutationImpl;
import uniovi.assign.genetic.population.Individual;
import uniovi.assign.genetic.population.Pair;
import uniovi.assign.genetic.population.Population;
import uniovi.assign.genetic.selection.RandomPairsSelection;
import uniovi.assign.genetic.selection.Selection;
import uniovi.assign.model.*;
import uniovi.assign.reports.Reporter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Class in charge of executing the genetic algorithm that generates
 * solutions seeds that are evaluated by the greedy algorithm
 */
public class GeneticAlgorithm {

    private final Planification planification;
    private List<Assignment> allAssignments;
    private Encoder encoder;
    private Population population;
    private Crossover crossover;
    private Mutation mutation;
    private FitnessFunction fitnessFunction;
    private Selection selection;
    private Random random = new Random();
    private Reporter reporter;
    private GeneticParameters geneticParameters;
    private AtomicBoolean generateResults;

    /**
     * Default constructor for the genetic algorithm. Initializes
     * the different operators that compose the algorithm.
     *
     * @param planification     planification to be evaluated
     * @param geneticParameters
     * @param generateResults
     */
    public GeneticAlgorithm(Planification planification, GeneticParameters geneticParameters, AtomicBoolean generateResults) {
        this.planification = planification;
        this.geneticParameters = geneticParameters;
        this.allAssignments = generateAllAssignments();
        this.encoder = new EncoderImpl(allAssignments);
        this.crossover = new OXCrossover();
        this.mutation = new MutationImpl();
        this.fitnessFunction = new FitnessFunctionImpl();
        this.selection = new RandomPairsSelection();
        this.reporter = new Reporter(planification);
        this.generateResults = generateResults;
    }

    /**
     * Launches the execution of the genetic algorithm. It initializes
     * the initial population and generate a new population each generation
     * until the maximum number of generations has been reached.
     * It also calls the reporter in each generation and at the end of the execution
     * in order to give feedback to the user about the status of the algorithm.
     */
    public void execute() {
        this.reporter.initializeCharts();
        long startTime = System.currentTimeMillis();
        createInitialPopulation();
        //Calculate fitness of all individuals in the initial population
        for (int i = 0; i < geneticParameters.getNumberOfGenerations(); i++) {

            List<Individual> newPopulation = new ArrayList<>();

            List<Pair> pairs = selection.selectPairs(population);
            for (Pair pair : pairs) {
                List<Individual> parentsAndDescendants = new ArrayList<>();
                parentsAndDescendants.add(pair.getParent1());
                parentsAndDescendants.add(pair.getParent2());
                // CROSSOVER
                if (random.nextFloat() <= geneticParameters.getCrossoverProb()) {
                    List<int[]> descendants = crossover.crossover(pair);
                    for (int[] descendant : descendants) {

                        Individual descendantDecoded = new Individual(encoder.decode(descendant));
                        // Mutation
                        if (random.nextFloat() <= geneticParameters.getMutationProb()) {
                            mutation.mutate(descendant);
                        }
                        descendantDecoded.setEncoding(descendant);
                        descendantDecoded.setFitness(fitnessFunction.calculateFitness(descendantDecoded));
                        parentsAndDescendants.add(descendantDecoded);
                    }
                }

                // Select the best two individuals between parents and descendants
                List<Individual> sortedByFitness = parentsAndDescendants.parallelStream().sorted(Comparator.comparingDouble(Individual::getFitness)).collect(Collectors.toList());
                for (int j = 0; j < 2; j++) {
                    Individual selectedIndividual = sortedByFitness.get(j);
                    newPopulation.add(selectedIndividual);
                }
            }
            this.population = new Population(newPopulation);
            this.reporter.updateCharts(population, i);

            // Print statistics each 20 Generations
            if (i % 20 == 0) {
                reporter.printStatsTable(this.population, i);
            }

            if (this.generateResults.get()) {
                reporter.generateAlgorithmResults(population, geneticParameters);
                this.generateResults.set(false);
            }

        }
        long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("EXECUTION TIME:" + executionTime);
        reporter.generateAlgorithmResults(population, geneticParameters);
        System.out.println("FINISHED");
    }


    /**
     * Method that generates a complete new population of random individuals.
     */
    private void createInitialPopulation() {
        List<Individual> individuals = new ArrayList<>();
        for (int i = 0; i < geneticParameters.getPopulationSize(); i++) {
            individuals.add(generateIndividual());
        }
        individuals.stream().forEach(individual -> individual.setFitness(fitnessFunction.calculateFitness(individual)));
        this.population = new Population(individuals);
    }

    /**
     * Method that generates a new random individual.
     *
     * @return new random individual
     */
    public Individual generateIndividual() {
        List<Assignment> randomAssignment = new ArrayList<>(allAssignments);
        Collections.shuffle(randomAssignment);
        Individual newIndividual = new Individual(randomAssignment);
        newIndividual.setEncoding(encoder.encode(newIndividual.getAssignments()));
        return newIndividual;
    }

    /**
     * Method that generates the complete list of assignments that the algorithm must do
     * from the information of the planification.
     *
     * @return the complete list of assignments that must be done by the algorithm.
     */
    private List<Assignment> generateAllAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        for (Student student : planification.getStudents()) {
            for (Subject subject : student.getSubjects()) {
                for (SubjectClass subjectClass : subject.getSubjectClasses()) {
                    Assignment assignment = new Assignment(student, subjectClass);
                    assignments.add(assignment);
                    student.addAssignation(assignment);
                }

            }
        }
        return assignments;
    }

}
