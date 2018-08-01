package uniovi.assign.reports;

import de.vandermeer.asciitable.AsciiTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import uniovi.assign.genetic.GeneticParameters;
import uniovi.assign.genetic.population.Individual;
import uniovi.assign.genetic.population.Population;
import uniovi.assign.greedy.GreedyAlgorithm;
import uniovi.assign.model.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class in charge of generating real time reports about the status of the algorithm
 * during its execution and at the end of the execution.
 */
public class Reporter {

    Planification planification;

    private List<Double> generationNumber = new ArrayList<>();
    private List<Double> bestFitnessList = new ArrayList<>();
    private List<Double> meanFitnessList = new ArrayList<>();
    private List<Double> meanCollisionsList = new ArrayList<>();
    private List<Double> meanVarianceList = new ArrayList<>();
    private List<Double> maxDifferenceInGroup = new ArrayList<>();
    private List<Double> meanMaxDifferenceBetweenGroupsList = new ArrayList<>();
    private List<Double> meanSumOfDifferencesBetweenGroupsList = new ArrayList<>();
    private List<Double> meanTotalNumberOfFreeSlotsList = new ArrayList<>();
    private List<Double> meantotalDaysWithLessThan2HoursList = new ArrayList<>();
    private ExcelReporter excelReporter;

    XYChart fitnessChart = null;
    SwingWrapper<XYChart> fitnessChartWrapper = null;
    XYChart collisionsChart = null;
    SwingWrapper<XYChart> collisionsChartWrapper = null;
    XYChart variancesChart = null;
    SwingWrapper<XYChart> variancesChartWrapper = null;


    /**
     * Default constructor for reporter objects
     *
     * @param planification planification to be reported.
     */
    public Reporter(Planification planification) {

        this.planification = planification;
        this.excelReporter = new ExcelReporter(planification);
    }

    /**
     * Prints an ascii table in console showing statistics of the population passed as parameter.
     *
     * @param population from which we want to generate the stats table.
     * @param generation number of the generation in which the population was generated.
     */
    public void printStatsTable(Population population, int generation) {
        Individual bestIndividual = population.getIndividuals().get(0);
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Generation", generation);
        table.addRule();
        table.addRow("Best fitness", population.getBestFitness());
        table.addRule();
        table.addRow("Average fitness", population.getMeanFitness());
        table.addRule();
        table.addRow("Mean number of collisions", population.getMeanNumberOfCollisions());
        table.addRule();
        table.addRow("Mean max difference between groups", population.getMeanMaxDifferenceBetweenGroups());
        table.addRule();
        table.addRow("Mean sum of difference between groups", population.getMeanSumOfDifferencesBetweenGroups());
        table.addRule();
        table.addRow("Mean total number of free slots", population.getMeanTotalNumberOfFreeSlots());
        table.addRule();
        table.addRow("Mean total days with 2 hours or less", population.getMeanTotalDaysWithLessThan2Hours());
        table.addRule();
        table.addRule();
        table.addRow(null, "BEST INDIVIDUAL");
        table.addRule();
        table.addRow("Number of collisions", bestIndividual.getUnsolvedAssignments().size());
        table.addRule();
        table.addRow("Max difference in group", bestIndividual.getMaxDifferenceBetweenGroups());
        table.addRule();
        table.addRow("Sum of differences between groups", bestIndividual.getTotalDifferenceBetweenGroups());
        table.addRule();
        table.addRow("Max variance", bestIndividual.getMaxVariance());
        table.addRule();
        table.addRow("Total variance", bestIndividual.getTotalVariance());
        table.addRule();
        table.addRow("Total number of free slots", bestIndividual.getTotalNumberOfFreeSlots());
        table.addRule();
        table.addRow("Total days with 2 hours or less", bestIndividual.getTotalDaysWithLowNumOfClasses());
        table.addRule();


        System.out.println();
        System.out.println(table.render());

        printConflictiveStudents(bestIndividual);
        System.out.println();
    }

    /**
     * Prints a table containing the students that have conflicting assignments of the individual passed as parameter.
     *
     * @param bestIndividual individual from which the conflictive students must be shown.
     */
    private void printConflictiveStudents(Individual bestIndividual) {
        AsciiTable conflictiveStudents = new AsciiTable();
        conflictiveStudents.addRule();
        conflictiveStudents.addRow("CONFLICTIVE STUDENT", "CONFLICTIVE SUBJECT");
        conflictiveStudents.addRule();
        for (Assignment unsolved : bestIndividual.getUnsolvedAssignments()) {
            Student s = unsolved.getStudent();
            String student = s.getName() + " " + s.getFirstSurname() + " " + s.getSecondSurname();
            conflictiveStudents.addRow(student, unsolved.getSubjectClass().getSubjectName());
            conflictiveStudents.addRule();
        }
        System.out.println(conflictiveStudents.render());
    }

    /**
     * Initializes the fitness values evolution chart.
     */
    private void initializeFitnessChart() {
        this.fitnessChart = QuickChart.getChart("Fitness evolution", "Generations", "Fitness", "Best fitness", new double[]{0}, new double[]{0});
        fitnessChart.addSeries("Mean fitness", new double[]{0});
        this.fitnessChartWrapper = new SwingWrapper<XYChart>(fitnessChart);
        fitnessChartWrapper.displayChart();
    }

    /**
     * Initializes the collisions evolution chart.
     */
    private void initializeCollisionsChart() {
        this.collisionsChart = QuickChart.getChart("Mean number of collisions", "Generations", "Collisions", "Number of collisions", new double[]{0}, new double[]{0});
        this.collisionsChartWrapper = new SwingWrapper<XYChart>(collisionsChart);
        collisionsChartWrapper.displayChart();
    }

    /**
     * Initializes all the charts for watching the status of the algorithm.
     */
    public void initializeCharts() {
        initializeFitnessChart();
        initializeCollisionsChart();
        initializeVarianceChart();
    }

    /**
     * Initializes the variance evolution chart.
     */
    private void initializeVarianceChart() {
        this.variancesChart = QuickChart.getChart("Variance between groups evolution in best individual", "Generations", "Total variance", "Total variance", new double[]{0}, new double[]{0});
        this.variancesChartWrapper = new SwingWrapper<XYChart>(variancesChart);
        variancesChartWrapper.displayChart();
    }

    /**
     * Updates the charts with the values obtained in the last generation.
     *
     * @param population population created in the last generation.
     * @param generation number of the last generation.
     */
    public void updateCharts(Population population, int generation) {
        updateListsValues(generation, population);
        repaintCharts();
    }

    public void groupDifferencesDistributionToFile(Population population) {
        Individual bestIndividual = population.getIndividuals().get(0);
        GreedyAlgorithm.getInstance().execute(bestIndividual);
        try {
            PrintWriter writer = new PrintWriter("groupDiff.csv", "UTF-8");
            for (Subject subject : planification.getSubjects().values()) {
                for (SubjectClass subjectClass : subject.getSubjectClasses()) {
                    writer.println(subjectClass.getMaximumDiferenceBetweenGroups());
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * Repaint again the chars with the updated information.
     */
    private void repaintCharts() {
        double[] generations = generationNumber.parallelStream().mapToDouble(Double::doubleValue).toArray();
        double[] fitnesses = bestFitnessList.parallelStream().mapToDouble(Double::doubleValue).toArray();
        double[] meanFitnesses = meanFitnessList.parallelStream().mapToDouble(Double::doubleValue).toArray();
        double[] colls = meanCollisionsList.parallelStream().mapToDouble(Double::doubleValue).toArray();
        double[] variances = meanVarianceList.parallelStream().mapToDouble(Double::doubleValue).toArray();
        fitnessChart.updateXYSeries("Best fitness", generations, fitnesses, null);
        fitnessChart.updateXYSeries("Mean fitness", generations, meanFitnesses, null);
        fitnessChartWrapper.repaintChart();

        collisionsChart.updateXYSeries("Number of collisions", generations, colls, null);
        collisionsChartWrapper.repaintChart();

        variancesChart.updateXYSeries("Total variance", generations, variances, null);
        variancesChartWrapper.repaintChart();
    }

    /**
     * Update the list of values for each one of the charts.
     *
     * @param generation number of the generation.
     * @param population population created in the generation.
     */
    private void updateListsValues(double generation, Population population) {
        generationNumber.add(generation);
        bestFitnessList.add(population.getBestFitness());
        meanFitnessList.add(population.getMeanFitness());
        meanCollisionsList.add(population.getMeanNumberOfCollisions());
        meanVarianceList.add(population.getMeanTotalVariance());
        meanMaxDifferenceBetweenGroupsList.add(population.getMeanMaxDifferenceBetweenGroups());
        meanSumOfDifferencesBetweenGroupsList.add(population.getMeanSumOfDifferencesBetweenGroups());
        meanTotalNumberOfFreeSlotsList.add(population.getMeanTotalNumberOfFreeSlots());
        meantotalDaysWithLessThan2HoursList.add(population.getMeanTotalDaysWithLessThan2Hours());
    }

    /**
     * Stores all the information about the evolution of the algorithm's execution in a CSV file.
     *
     * @param geneticParameters parameters used in the genetic algorithm.
     * @param executionTime     total execution time.
     * @param processID         process identifier.
     */
    public void executionInfoToFile(GeneticParameters geneticParameters, long executionTime, int processID) {
        String filename = "m-" + geneticParameters.getMutationProb() + "_c-" + geneticParameters.getCrossoverProb() + "_ps-"
                + geneticParameters.getPopulationSize() + "_ng-"
                + geneticParameters.getNumberOfGenerations() + "-ID"
                + processID + ".txt";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
            writer.println("Mutation probability: " + geneticParameters.getMutationProb());
            writer.println("Crossover probability: " + geneticParameters.getCrossoverProb());
            writer.println("Population size: " + geneticParameters.getPopulationSize());
            writer.println("Number of generations: " + geneticParameters.getNumberOfGenerations());
            writer.println("Execution time: " + executionTime);
            writer.println();
            writer.println("GENERATION;BEST_FITNESS;MEAN_FITNESS;MEAN_COLLISIONS;MEAN_VARIANCE;MEAN_MAX_DIFF;MEAN_TOTAL_DIFF;MEAN_SUM_DIFF;MEAN_FREE_SLOTS;MEAN_LOW_DAYS");
            for (int i = 0; i < generationNumber.size(); i++) {
                writer.println(generationNumber.get(i) + ";"
                        + bestFitnessList.get(i)
                        + ";" + meanFitnessList.get(i)
                        + ";" + meanCollisionsList.get(i)
                        + ";" + meanVarianceList.get(i)
                        + ";" + meanMaxDifferenceBetweenGroupsList.get(i)
                        + ";" + meanSumOfDifferencesBetweenGroupsList.get(i)
                        + ";" + meanTotalNumberOfFreeSlotsList.get(i)
                        + ";" + meantotalDaysWithLessThan2HoursList.get(i));
            }
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the files with the final results of the algorithm.
     *
     * @param population last population evaluated by the algorithm.
     */
    public void generateAlgorithmResults(Population population, GeneticParameters parameters) {
        Individual bestIndividual = population.getIndividuals().get(0);
        GreedyAlgorithm.getInstance().execute(bestIndividual);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy_HH.mm");
        String folderPath = "results-" + format.format(new Date());
        new File(folderPath).mkdirs();
        generateAlgorithResumeFile(bestIndividual, folderPath);
        generateSubjectFiles(folderPath);
        generateStudentFiles(folderPath);
        excelReporter.generateExcelFile(bestIndividual, parameters, folderPath);
        generateUnsolvedAssignmentsFile(bestIndividual, folderPath);
        System.out.println("RESULTS GENERATED");
    }



    /**
     * Generates a summary txt file from an individual of the population.
     *
     * @param bestIndividual individual of the population.
     */
    private void generateAlgorithResumeFile(Individual bestIndividual, String folderPath) {
        try {
            PrintWriter writer = new PrintWriter(folderPath + "/summary.txt", "UTF-8");
            AsciiTable table = new AsciiTable();
            table.addRule();
            table.addRow(null, "BEST INDIVIDUAL");
            table.addRule();
            table.addRow("Number of collisions", bestIndividual.getUnsolvedAssignments().size());
            table.addRule();
            table.addRow("Max difference in group", bestIndividual.getMaxDifferenceBetweenGroups());
            table.addRule();
            table.addRow("Sum of differences between groups", bestIndividual.getTotalDifferenceBetweenGroups());
            table.addRule();
            table.addRow("Max variance", bestIndividual.getMaxVariance());
            table.addRule();
            table.addRow("Total variance", bestIndividual.getTotalVariance());
            table.addRule();
            table.addRow("Total number of free slots", bestIndividual.getTotalNumberOfFreeSlots());
            table.addRule();
            table.addRow("Total days with 2 hours or less", bestIndividual.getTotalDaysWithLowNumOfClasses());
            table.addRule();
            writer.println(table.render());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * Generates a txt file containing the unsolved assignments of an individual of the population.
     *
     * @param individual individual of the population.
     * @param folderPath
     */
    private void generateUnsolvedAssignmentsFile(Individual individual, String folderPath) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(folderPath + "/unsolvedAssignments.txt", "UTF-8");
            AsciiTable unsolvedTable = new AsciiTable();
            unsolvedTable.addRule();
            unsolvedTable.addRow("STUDENT", "SUBJECT CLASS");
            unsolvedTable.addRule();
            for (Assignment assignment : individual.getUnsolvedAssignments()) {
                unsolvedTable.addRow(assignment.getStudent().getCompleteName(), assignment.getSubjectClass().getSubjectName());
                unsolvedTable.addRule();
            }
            writer.println(unsolvedTable.render());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate a file for each student containing his final assignments and timetable.
     */
    private void generateStudentFiles(String folderPath) {
        new File(folderPath + "/students").mkdirs();
        for (Student student : planification.getStudents()) {
            try {
                PrintWriter writer = new PrintWriter(folderPath + "/students/" + student.getId() + ".txt", "UTF-8");
                writer.println("NAME: " + student.getCompleteName());
                writer.println("ID: " + student.getId());
                writer.println();
                writer.println("TIMETABLE:");
                writer.println();
                writer.println(student.getStudentTimetable());
                writer.println();
                AsciiTable assignations = new AsciiTable();
                assignations.addRule();
                assignations.addRow("SUBJECT CLASS", "GROUP");
                assignations.addRule();
                for (Assignment assignment : student.getAssignments()) {
                    Group g = assignment.getGroup();
                    String group = "NO GROUP";
                    if (g != null) {
                        group = g.getGroupId();
                    }
                    assignations.addRow(assignment.getSubjectClass().getSubjectName(), group);
                    assignations.addRule();
                }
                writer.println("STUDENT ASSIGNATIONS");
                writer.println();
                writer.println(assignations.render());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate a file for each subject containing the number of students in each one of the subject classes
     * and groups.
     */
    private void generateSubjectFiles(String folderPath) {
        new File(folderPath + "/subjects").mkdirs();
        for (Subject subject : planification.getSubjects().values()) {
            try {
                PrintWriter writer = new PrintWriter(folderPath + "/subjects/" + subject.getSubjectId() + ".txt", "UTF-8");
                writer.println("SUBJECT: " + subject.getSubjectName());
                writer.println("COURSE: " + subject.getCourse());
                writer.println();
                writer.println("*******************SUBJECT CLASSES*********************");
                writer.println();
                for (SubjectClass subjectClass : subject.getSubjectClasses()) {
                    writer.println("\tCLASS: " + subjectClass.getSubjectName());
                    writer.println("\tTOTAL NUMBER OF STUDENTS: " + subjectClass.getTotalNumberOfStudents());
                    writer.println("\tSTUDENT DISTRIBUTION IN GROUPS: ");
                    AsciiTable numberOfStudentsTable = new AsciiTable();
                    numberOfStudentsTable.addRule();
                    numberOfStudentsTable.addRow("GROUP ID", "NUMBER OF STUDENTS");
                    numberOfStudentsTable.addRule();
                    for (Group group : subjectClass.getGroups()) {
                        numberOfStudentsTable.addRow(group.getGroupId(), group.getNumberOfStudents());
                        numberOfStudentsTable.addRule();
                    }
                    writer.println();
                    writer.println(numberOfStudentsTable.render());
                    writer.println();
                    writer.println();

                }
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * Gnerates a CSV files showing the information of the best individual generated.
     *
     * @param individual best individual in the population.
     */
    public void bestIndividualToFile(Individual individual) {
        try {
            PrintWriter output = new PrintWriter(new FileWriter("best.csv", true));
            output.println(individual.getNumberOfCollisions() + ";" + individual.getTotalVariance() + ";" + individual.getMaxDifferenceBetweenGroups() + ";" + individual.getTotalNumberOfFreeSlots() + ";" + individual.getTotalDaysWithLowNumOfClasses());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
