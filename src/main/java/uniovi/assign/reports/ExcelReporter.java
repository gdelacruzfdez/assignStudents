package uniovi.assign.reports;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uniovi.assign.genetic.GeneticParameters;
import uniovi.assign.genetic.population.Individual;
import uniovi.assign.model.Assignment;
import uniovi.assign.model.Planification;
import uniovi.assign.model.Student;
import uniovi.assign.model.Subject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author gdelacru
 */
public class ExcelReporter {


    public static final List<String> SUBJECTS_ORDER_S1 = Arrays.asList("AL", "Cal", "Emp", "FI", "IP", "AC", "Com", "CPM", "ED", "TEC",
            "DS", "IPS", "RI", "SEW", "CVVS", "IR", "SI", "IA",
            "IFA", "IAE", "RAA", "SIW", "SEV", "SDM", "SR"
    );

    public static final List<String> SUBJECTS_ORDER_S2 = Arrays.asList(
            "AMD", "Est", "FCR", "MP", "OyE", "Alg", "BD", "CN", "SO", "TPP",
            "ASR", "ASW", "DLP", "SSI", "SDI", "ASLEPI", "DPPI"

    );

    public static final String[] TYPE_OF_CLASSES = {
            "THEORY", "SEMINAR", "LAB"
    };
    private Planification planification;

    public ExcelReporter(Planification planification) {
        this.planification = planification;
    }

    /**
     * Generates an excel file containing the results of the
     * best solution found during the execution of the algorithm.
     *
     * @param bestIndividual
     * @param parameters     parameters of the Genetic Algorithm.
     * @param folderPath
     */
    public void generateExcelFile(Individual bestIndividual, GeneticParameters parameters, String folderPath) {
        String excelFileName = folderPath + "/excelResults.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();

        generateExcelSummarySheet(bestIndividual, workbook);
        generateExcelAssignmentsSheet(parameters, workbook);
        generateExcelUnsolvedAssignmentsSheet(bestIndividual, workbook);

        createExcelFile(excelFileName, workbook);
    }


    private void generateExcelSummarySheet(Individual bestIndividual, XSSFWorkbook workbook) {
        XSSFSheet summarySheet = workbook.createSheet("Summary");
        int rowNum = 0;
        Row header = summarySheet.createRow(rowNum++);
        summarySheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        header.createCell(0).setCellValue("BEST INDIVIDUAL");
        addTableRow(summarySheet, rowNum++, "Number of collisions", "" + bestIndividual.getUnsolvedAssignments().size());
        addTableRow(summarySheet, rowNum++, "Max difference in group", "" + bestIndividual.getMaxDifferenceBetweenGroups());
        addTableRow(summarySheet, rowNum++, "Sum of differences between groups", "" + bestIndividual.getTotalDifferenceBetweenGroups());
        addTableRow(summarySheet, rowNum++, "Max variance", "" + bestIndividual.getMaxVariance());
        addTableRow(summarySheet, rowNum++, "Total variance", "" + bestIndividual.getTotalVariance());
        addTableRow(summarySheet, rowNum++, "Total number of free slots", "" + bestIndividual.getTotalNumberOfFreeSlots());
        addTableRow(summarySheet, rowNum++, "Total days with 2 hours or less", "" + bestIndividual.getTotalDaysWithLowNumOfClasses());
        summarySheet.autoSizeColumn(0);
        summarySheet.autoSizeColumn(1);
    }

    private void generateExcelUnsolvedAssignmentsSheet(Individual bestIndividual, XSSFWorkbook workbook) {
        XSSFSheet unsolvedAssignmentsSheet = workbook.createSheet("Unsolved Assignments");
        int rowNum = 0;
        Row header = unsolvedAssignmentsSheet.createRow(rowNum++);
        unsolvedAssignmentsSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        header.createCell(0).setCellValue("UNSOLVED ASSIGNMENTS");
        for (Assignment unsolvedAssignment : bestIndividual.getUnsolvedAssignments()) {
            String studentName = unsolvedAssignment.getStudent().getCompleteName();
            String subjectName = unsolvedAssignment.getSubjectClass().getSubjectName();
            String studentID = unsolvedAssignment.getStudent().getId();
            addTableRow(unsolvedAssignmentsSheet, rowNum++, studentID, studentName, subjectName);
        }
        unsolvedAssignmentsSheet.autoSizeColumn(0);
        unsolvedAssignmentsSheet.autoSizeColumn(1);
    }

    private void addTableRow(XSSFSheet summarySheet, int rowNum, String... values) {
        Row row = summarySheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i]);
        }
    }

    private void generateExcelAssignmentsSheet(GeneticParameters parameters, XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("Assignments");
        List<String> subjectOrder;
        if (parameters.getSemester().equals("S1")) {
            subjectOrder = SUBJECTS_ORDER_S1;
        } else {
            subjectOrder = SUBJECTS_ORDER_S2;
        }

        CellStyle firstCourse = workbook.createCellStyle();
        firstCourse.setFillBackgroundColor(IndexedColors.ORANGE.getIndex());
        CellStyle secondCourse = workbook.createCellStyle();
        secondCourse.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        CellStyle thirdCourse = workbook.createCellStyle();
        thirdCourse.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        CellStyle fourthCourse = workbook.createCellStyle();
        fourthCourse.setFillBackgroundColor(IndexedColors.BLUE.getIndex());

        int rowNum = 0;
        int colNum = 0;
        Row header = sheet.createRow(rowNum++);
        Cell headerId = header.createCell(colNum++);
        headerId.setCellValue("ID");
        Cell headerName = header.createCell(colNum++);
        headerName.setCellValue("NAME");
        for (String typeOfClass : TYPE_OF_CLASSES) {
            Cell type = header.createCell(colNum++);
            type.setCellValue(typeOfClass);
            for (String subjectStr : subjectOrder) {
                Subject subject = planification.getSubject(subjectStr);
                Cell subjectCell = header.createCell(colNum++);
                subjectCell.setCellValue(subject.getSubjectId());
                switch (subject.getCourse()) {
                    case "Primero":
                        subjectCell.setCellStyle(firstCourse);
                        break;
                    case "Segundo":
                        subjectCell.setCellStyle(secondCourse);
                        break;
                    case "Tercero":
                        subjectCell.setCellStyle(thirdCourse);
                        break;
                    case "Cuarto":
                        subjectCell.setCellStyle(fourthCourse);
                        break;
                }
            }
        }

        for (Student student : planification.getStudents()) {
            Row row = sheet.createRow(rowNum++);
            Cell id = row.createCell(0);
            id.setCellValue(student.getId());
            Cell name = row.createCell(1);
            name.setCellValue(student.getCompleteName());
            for (Assignment assignment : student.getAssignments()) {
                int subjectIndex = subjectOrder.indexOf(assignment.getSubjectClass().getSubject().getSubjectId().replace(".I", ""));
                int typeOfClassIndex = 0;
                if (assignment.getSubjectClass().getSubjectName().contains(".T")) {
                    typeOfClassIndex = 0;
                } else if (assignment.getSubjectClass().getSubjectName().contains(".S")) {
                    typeOfClassIndex = 1;
                } else if (assignment.getSubjectClass().getSubjectName().contains(".L")) {
                    typeOfClassIndex = 2;
                }
                Cell assignmentCell = row.createCell(subjectIndex + (typeOfClassIndex * subjectOrder.size()) + 2 + typeOfClassIndex + 1);
                if (assignment.getGroup() != null) {
                    String[] parts = assignment.getGroup().getGroupId().split("\\.");
                    assignmentCell.setCellValue(parts[parts.length - 1]);
                } else {
                    assignmentCell.setCellValue("UNSOLVED");
                }
            }
        }
        for (int i = 1; i <= colNum; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createExcelFile(String excelFileName, XSSFWorkbook workbook) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(excelFileName);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
