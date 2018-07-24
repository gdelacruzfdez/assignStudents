package uniovi.assign.parser;

import uniovi.assign.genetic.GeneticParameters;
import uniovi.assign.model.Group;
import uniovi.assign.model.Planification;
import uniovi.assign.model.SubjectClass;

import java.util.List;
import java.util.Map;

/**
 * Interface that must implement all the classes in charge of parsing the input files of the system.
 */
public interface Parser {

    /**
     * Method in charge of parsing the file that contains the information about the timetable
     * of the school groups.
     *
     * @param pathToFile path to the file containing the information about the timetable of the school groups.
     * @return list of the school groups with the information about their classes.
     */
    List<Group> parseScheduleFile(String pathToFile);

    /**
     * Method in charge of parsing the mapping between the complete name of the subjects an its abbreviations.
     *
     * @param pathToFile path to the file that contains the mapping between the complete name of the subjects an its abbreviations.
     * @return map containing the complete name of the subject as a key and its abbreviation as a value.
     */
    Map<String, String> parseSubjectAbreviations(String pathToFile);

    /**
     * Method in charge of parsing the file containing the information about the student enrollments.
     *
     * @param pathToFile path to the file that contains the information about the student enrollments.
     * @param semester   semester from which we want to obtain the planification.
     * @return planification object containing the information of the students enrollments and the subjects.
     */
    Planification parsePlanification(String pathToFile, String semester);

    /**
     * Method in charge of generation the subject classes from the list of groups parsed from the timetable file.
     *
     * @param groups list of groups parsed from the timetable file.
     * @return list of existing subject classes.
     */
    List<SubjectClass> generateSubjects(List<Group> groups);

    /**
     * Method in charge of parsing the configuration CSV files.
     *
     * @param pathToFile path to a configuration CSV file.
     * @return map that contains the configuration described in the CSV file.
     */
    Map<String, List<String>> parseConfigurationCSV(String pathToFile);

    /**
     * Method in charge of parsing the assignment preferences file.
     *
     * @param pathToFile path to the CSV file containing the assignment preferences.
     * @param groupMap   map containing the existing groups.
     * @return a map in which the key represents a group and the value a list of groups that must also be assigned when the key group is assigned.
     */
    Map<Group, List<Group>> parseAssigmentPreferences(String pathToFile, Map<String, Group> groupMap);

    /**
     * Method in charge of parsing the genetic algorithm params properties files.
     *
     * @param pathToFile path to the properties file containing the genetic algorithm parameters.
     * @return geneticparameters object containing the genetic algorithm parameters.
     */
    GeneticParameters parseGeneticAlgorithmParams(String pathToFile);


}
