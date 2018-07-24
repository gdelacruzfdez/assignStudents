package uniovi.assign;

import uniovi.assign.genetic.GeneticAlgorithm;
import uniovi.assign.genetic.GeneticParameters;
import uniovi.assign.greedy.CollisionMatrix;
import uniovi.assign.greedy.GreedyAlgorithm;
import uniovi.assign.model.Group;
import uniovi.assign.model.Planification;
import uniovi.assign.model.Subject;
import uniovi.assign.model.SubjectClass;
import uniovi.assign.parser.Parser;
import uniovi.assign.parser.ParserImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Main class of the system. It is the class that contains the main method for launching the system.
 */
public class Main {

    public static void main(String[] args) {

        if (args.length == 2) {

            String enrollmentFile = args[0];
            String scheduleFile = args[1];

            Parser parser = new ParserImpl();
            GeneticParameters geneticParameters = parser.parseGeneticAlgorithmParams("geneticParameters.properties");

            List<Group> groups = parser.parseScheduleFile(scheduleFile);
            Map<String, Group> groupMap = groups.parallelStream().collect(Collectors.toMap(Group::getGroupId, group -> group));
            Subject.subjectCodes = parser.parseSubjectAbreviations("subjectAbv");

            Planification planification = parser.parsePlanification(enrollmentFile, geneticParameters.getSemester());
            List<SubjectClass> subjectClasses = parser.generateSubjects(groups);

            planification.assignClassesToSubjects(subjectClasses);

            Map<String, List<String>> collisionExceptions = parser.parseConfigurationCSV("collisionExceptions");
            Map<String, List<String>> mandatoryCollisions = parser.parseConfigurationCSV("mandatoryCollisions");
            Map<Group, List<Group>> assigmentPreferences = parser.parseAssigmentPreferences("preferences", groupMap);

            CollisionMatrix collisionMatrix = new CollisionMatrix(groups, collisionExceptions, mandatoryCollisions);

            GreedyAlgorithm algorithm = GreedyAlgorithm.getInstance();


            algorithm.initialize(collisionMatrix, groups, assigmentPreferences, planification);

            AtomicBoolean generateResults = new AtomicBoolean();
            InputThread inputThread = new InputThread(generateResults);
            inputThread.start();
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(planification, geneticParameters, generateResults);
            geneticAlgorithm.execute();
            inputThread.interrupt();
        } else {
            System.err.println("ERROR: Input parameters missing. Try executing java -jar assignStudents <enrollmentFile> <scheduleFile>");
            System.exit(1);
        }


    }
}