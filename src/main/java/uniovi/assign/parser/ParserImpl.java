package uniovi.assign.parser;

import com.poiji.bind.Poiji;
import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;
import org.apache.poi.util.IOUtils;
import uniovi.assign.genetic.GeneticParameters;
import uniovi.assign.model.Class;
import uniovi.assign.model.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ParserImpl implements Parser {

    /**
     * Method in charge of parsing the file that contains the information about the timetable
     * of the school groups.
     * <p>
     * If the file is not found or the format is incorrect, an error will be shown and the execution stops.
     *
     * @param pathToFile path to the file containing the information about the timetable of the school groups.
     * @return list of the school groups with the information about their classes.
     */
    public List<Group> parseScheduleFile(String pathToFile) {
        try {
            List<Class> clases = new ArrayList<>();
            CsvConfiguration config = new CsvConfiguration();
            config.setLineFilter(new HeaderAndFooterFilter(2, false, true));
            config.setFieldDelimiter(',');
            Deserializer deserializer = CsvIOFactory.createFactory(config, Class.class).createDeserializer();
            InputStream file = new FileInputStream(pathToFile);
            InputStreamReader reader = new InputStreamReader(file);
            deserializer.open(reader);
            while (deserializer.hasNext()) {
                Class clase = deserializer.next();
                if (clase.getGroupId() == null) {
                    continue;
                }
                clase.setPosition(clases.size());
                clase.generateDates();
                clases.add(clase);
            }

            deserializer.close(true);
            List<Class> simplifiedClasses = simplifyClassesTimetable(clases);

            List<Group> groups = simplifiedClasses.parallelStream().collect(Collectors.groupingBy(cl -> cl.getGroupId())).entrySet().parallelStream().map(entry -> new Group(entry.getKey(), entry.getValue())).collect(Collectors.toList());

            return groups;
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Enrollment file not found in path " + pathToFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: Enrollment file in path " + pathToFile + " has an incorrect format.");
            System.exit(1);
        }
        return null;
    }


    /**
     * Method that deletes all the duplicated classes parsed from the timetable CSV file.
     *
     * @param clasess list of classes parsed from the timetable CSV file.
     * @return simplified list of classes without duplicated classes.
     */
    private List<Class> simplifyClassesTimetable(List<Class> clasess) {
        return clasess.parallelStream().collect(Collectors.groupingBy(Class::getGroupId)).values().parallelStream().map(listClass -> {
            return listClass.parallelStream().collect(Collectors.groupingBy(Class::dateHash)).values().parallelStream().filter(li -> li.size() > 3).collect(Collectors.toList()).parallelStream().map(l -> l.get(0)).collect(Collectors.toList());
        }).flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Method in charge of parsing the file containing the information about the student enrollments.
     * <p>
     * If the file is not found or the format is incorrect, an error will be shown and the execution stops.
     *
     * @param pathToFile path to the file that contains the information about the student enrollments.
     * @return planification object containing the information of the students enrollments and the subjects.
     */
    @Override
    public Map<String, String> parseSubjectAbreviations(String pathToFile) {
        try {
            InputStream file = new FileInputStream(pathToFile);
            Map<String, String> ret = new HashMap<>();
            Scanner sc = new Scanner(file,"UTF-8");
            String[] splitted;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                splitted = line.split(";");
                ret.put(splitted[0], splitted[1]);
            }
            sc.close();
            return ret;
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Subject abreviations file not found in path " + pathToFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: Subject abreviations file in path " + pathToFile + " has an incorrect format.");
            System.exit(1);
        }
        return null;
    }

    /**
     * Method in charge of parsing the file containing the information about the student enrollments.
     * <p>
     * If the file is not found or the format is incorrect, an error will be shown and the execution stops.
     *
     * @param pathToFile path to the file that contains the information about the student enrollments.
     * @param semester   semester from which we want to obtain the planification.
     * @return planification object containing the information of the students enrollments and the subjects.
     */
    @Override
    public Planification parsePlanification(String pathToFile, String semester) {
        List<StudentListRow> studentListRows = new ArrayList<>();
        try {
            File tempFile = File.createTempFile("temp", ".xls");
            tempFile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(tempFile);
            InputStream fileInputStream = new FileInputStream(pathToFile);
            IOUtils.copy(fileInputStream, out);
            studentListRows = Poiji.fromExcel(tempFile, StudentListRow.class);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Schedule file not found in path " + pathToFile);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("ERROR: The format of the schedule file " + pathToFile + " is incorrect.");
            System.exit(1);
        }
        studentListRows = studentListRows.stream().filter(studentListRow -> studentListRow.getSemester().equals(semester)).collect(Collectors.toList());
        List<Student> students = new ArrayList<>();
        HashMap<String, Subject> subjects = new HashMap<>();

        for (StudentListRow studentListRow : studentListRows) {
            boolean assigned = false;
            for (Student student : students) {
                if (student.getId().equals(studentListRow.getId())) {
                    Subject subject = subjects.get(studentListRow.getSubjectId());
                    if (subject == null) {
                        subject = new Subject(studentListRow.getSubjectName(), studentListRow.getCourse(), studentListRow.getSemester(), studentListRow.getSubjectInEnglish());
                        subjects.put(subject.getSubjectId(), subject);
                    }
                    student.addSubject(subject);
                    assigned = true;
                    break;
                }
            }
            if (!assigned) {
                Student student = new Student(studentListRow);
                students.add(student);
                Subject subject = subjects.get(studentListRow.getSubjectId());
                if (subject == null) {
                    subject = new Subject(studentListRow.getSubjectName(), studentListRow.getCourse(), studentListRow.getSemester(), studentListRow.getSubjectInEnglish());
                    subjects.put(subject.getSubjectId(), subject);
                }
                student.addSubject(subject);
            }
        }

        return new Planification(students, subjects);
    }

    /**
     * Method in charge of generation the subject classes from the list of groups parsed from the timetable file.
     *
     * @param groups list of groups parsed from the timetable file.
     * @return list of existing subject classes.
     */
    public List<SubjectClass> generateSubjects(List<Group> groups) {
        List<SubjectClass> subjectClasses = new ArrayList<>();

        for (Group group : groups) {
            String[] parts = group.getGroupId().split("[.]");
            String subjectId = parts[0] + "." + parts[1];
            if (parts[2].contains("I")) {
                subjectId += ".I";
            }

            String finalSubjectId = subjectId;
            SubjectClass subjectClass = null;

            for (SubjectClass sub : subjectClasses) {
                if (sub.getSubjectName().equals(finalSubjectId)) {
                    subjectClass = sub;
                    break;
                }
            }

            if (subjectClass == null) {
                subjectClass = new SubjectClass(subjectId);
                subjectClasses.add(subjectClass);
            }
            subjectClass.addGroup(group);
            group.setSubjectClass(subjectClass);
        }

        return subjectClasses;
    }

    /**
     * Method in charge of parsing the configuration CSV files.
     * <p>
     * If the file is not found or the format is incorrect, an error will be shown and the execution stops.
     *
     * @param pathToFile path to a configuration CSV file.
     * @return map that contains the configuration described in the CSV file.
     */
    @Override
    public Map<String, List<String>> parseConfigurationCSV(String pathToFile) {
        Map<String, List<String>> collisions = new HashMap<>();
        InputStream file = null;
        try {
            file = new FileInputStream(pathToFile);

            Scanner sc = new Scanner(file);
            String[] splitted;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                splitted = line.split(";");
                for (int i = 0; i < splitted.length; i++) {
                    List<String> exceptions = new ArrayList<>();
                    String group = splitted[i];
                    for (int j = 0; j < splitted.length; j++) {
                        if (!group.equals(splitted[j])) {
                            exceptions.add(splitted[j]);
                        }
                    }
                    if (collisions.containsKey(group)) {
                        collisions.get(group).addAll(exceptions);
                    } else {
                        collisions.put(group, exceptions);
                    }

                }
            }
            sc.close();


            return collisions;
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Configuration CSV file not found in path " + pathToFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: Configuration CSV file in path " + pathToFile + " has an incorrect format.");
            System.exit(1);
        }
        return null;
    }

    /**
     * Method in charge of parsing the assignment preferences file.
     * <p>
     * If the file is not found or the format is incorrect, an error will be shown and the execution stops.
     *
     * @param pathToFile path to the CSV file containing the assignment preferences.
     * @param groupMap   map containing the existing groups.
     * @return a map in which the key represents a group and the value a list of groups that must also be assigned when the key group is assigned.
     */
    @Override
    public Map<Group, List<Group>> parseAssigmentPreferences(String pathToFile, Map<String, Group> groupMap) {
        Map<Group, List<Group>> assigmentPreferences = new HashMap<>();
        InputStream file = null;
        try {
            file = new FileInputStream(pathToFile);
            Scanner sc = new Scanner(file);
            String[] splitted;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                splitted = line.split(";");
                for (int i = 0; i < splitted.length; i++) {
                    List<Group> preferredGroups = new ArrayList<>();
                    String groupStr = splitted[i];
                    Group group = groupMap.get(groupStr);
                    for (int j = 0; j < splitted.length; j++) {
                        if (!groupStr.equals(splitted[j])) {
                            Group preferredGroup = groupMap.get(splitted[j]);
                            if (preferredGroup != null) {
                                preferredGroups.add(preferredGroup);
                            }
                        }
                    }
                    assigmentPreferences.put(group, preferredGroups);
                }
            }
            sc.close();


            return assigmentPreferences;
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Preferences CSV file not found in path " + pathToFile);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: Preferences CSV file in path " + pathToFile + " has an incorrect format.");
            System.exit(1);
        }
        return null;

    }

    /**
     * Method in charge of parsing the genetic algorithm params properties files.
     * <p>
     * If the file is not found or the format is incorrect, an error will be shown and the execution stops.
     *
     * @param pathToFile path to the properties file containing the genetic algorithm parameters.
     * @return geneticparameters object containing the genetic algorithm parameters.
     */
    @Override
    public GeneticParameters parseGeneticAlgorithmParams(String pathToFile) {
        Properties prop = new Properties();
        InputStream input = null;
        GeneticParameters parameters = new GeneticParameters();
        try {
            input = new FileInputStream(pathToFile);
            prop.load(input);

            if (prop.getProperty("mutationProb") != null) {
                System.out.println("Mutation Probability:" + parameters.getMutationProb());
                parameters.setMutationProb(Double.parseDouble(prop.getProperty("mutationProb")));
            } else {
                System.out.println("WARNING: mutationProb not provided. Default value " + parameters.getMutationProb() + " will be used");
            }

            if (prop.getProperty("crossoverProb") != null) {
                parameters.setCrossoverProb(Double.parseDouble(prop.getProperty("crossoverProb")));
                System.out.println("Crossover Probability:" + parameters.getCrossoverProb());
            } else {
                System.out.println("WARNING: crossoverProb not provided. Default value " + parameters.getCrossoverProb() + " will be used");
            }

            if (prop.getProperty("generations") != null) {
                parameters.setNumberOfGenerations(Integer.parseInt(prop.getProperty("generations")));
                System.out.println("Number of generations:" + parameters.getNumberOfGenerations());
            } else {
                System.out.println("WARNING: generations not provided. Default value " + parameters.getNumberOfGenerations() + " will be used");
            }

            if (prop.getProperty("populationSize") != null) {
                parameters.setPopulationSize(Integer.parseInt(prop.getProperty("populationSize")));
                System.out.println("Population Size:" + parameters.getPopulationSize());
            } else {
                System.out.println("WARNING: populationSize not provided. Default value " + parameters.getPopulationSize() + " will be used");
            }

            if (prop.getProperty("semester") != null) {
                parameters.setSemester(prop.getProperty("semester"));
                System.out.println("Semester evaluated:" + parameters.getSemester());
            } else {
                System.out.println("WARNING: semester not provided. Default value " + parameters.getSemester() + " will be used");
            }

        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Could not find file " + pathToFile + ". Default Genetic Parameter values will be used");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("ERROR: There was a problem parsing file " + pathToFile + ". Default Genetic Parameter values will be used");
            System.exit(1);
        }

        return parameters;


    }
}



