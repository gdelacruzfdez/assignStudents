package uniovi.assign.model;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents the model of a class of a group that has an start and finish date.
 */
@CsvDataType()
public class Class {

    @CsvField(pos = 1)
    private String groupId;

    private Date CSVStartDate;

    private Date CSVEndDate;

    @CsvField(pos = 2)
    private String strStartDate;
    @CsvField(pos = 3)
    private String strStartTime;
    @CsvField(pos = 4)
    private String strEndDate;
    @CsvField(pos = 5)
    private String strEndTime;

    private SimplifiedDate startDate;

    private SimplifiedDate endDate;

    private int position;

    public String getGroupId() {
        return groupId;
    }

    public Date getCSVStartDate() {
        return CSVStartDate;
    }

    public Date getCSVEndDate() {
        return CSVEndDate;
    }

    public SimplifiedDate getStartDate() {
        return startDate;
    }

    public SimplifiedDate getEndDate() {
        return endDate;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Generates simplified dates for the dates read in the csv file received as input.
     */
    public void generateSimplifiedDates() {
        this.startDate = new SimplifiedDate(this.CSVStartDate);
        this.endDate = new SimplifiedDate(this.CSVEndDate);
    }

    /**
     * Calculates the duration of the class in minutes.
     *
     * @return duration of the class in minutes.
     */
    public int getClassDurationInMinutes() {
        return (((endDate.dayOfTheWeek - 1) * 24 * 60) + endDate.hour * 60 + endDate.minutes) - (((startDate.dayOfTheWeek - 1) * 24 * 60) + startDate.hour * 60 + startDate.minutes);
    }

    /**
     * Hash for a class based in its groupID and the start and end dates.
     *
     * @return hash of the object based in its groupID and the start and end dates.
     */
    public int dateHash() {
        int result = groupId.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        return result;
    }

    public void generateDates() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy:H.mm");

        try {
            this.CSVStartDate = format.parse(strStartDate.trim() + ":" + strStartTime.trim());
            this.CSVEndDate = format.parse(strEndDate.trim() + ":" + strEndTime.trim());
            generateSimplifiedDates();
        } catch (ParseException e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ERROR: Error parsing date from Google Calendar File: \n");
            stringBuilder.append("Group ID:" + groupId + "\n");
            stringBuilder.append("Start Date:" + strStartDate + "\n");
            stringBuilder.append("Start Time:" + strStartTime + "\n");
            stringBuilder.append("End Date:" + strEndDate + "\n");
            stringBuilder.append("End Time:" + strEndTime + "\n");
            System.exit(1);
            System.out.println();
        }
    }

}
