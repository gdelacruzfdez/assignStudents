package uniovi.assign.model;

import java.util.Calendar;
import java.util.Date;

public class SimplifiedDate {

    int dayOfTheWeek;
    int hour;
    int minutes;

    public SimplifiedDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        this.dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK) - 2;
        this.hour = c.get(Calendar.HOUR_OF_DAY);
        this.minutes = c.get(Calendar.MINUTE);
    }

    public SimplifiedDate(int dayOfTheWeek, int hour, int minutes) {
        this.dayOfTheWeek = dayOfTheWeek;
        this.hour = hour;
        this.minutes = minutes;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof SubjectClass)) return false;
        SimplifiedDate otherDate = (SimplifiedDate) other;
        return this.dayOfTheWeek == otherDate.dayOfTheWeek && this.hour == otherDate.hour && this.minutes == otherDate.minutes;
    }

    public boolean before(SimplifiedDate date) {
        if (dayOfTheWeek < date.dayOfTheWeek) {
            return true;
        } else if (dayOfTheWeek == date.dayOfTheWeek) {
            if (hour < date.hour) {
                return true;
            } else if (hour == date.hour) {
                return minutes < date.minutes;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean after(SimplifiedDate date) {
        if (dayOfTheWeek > date.dayOfTheWeek) {
            return true;
        } else if (dayOfTheWeek == date.dayOfTheWeek) {
            if (hour > date.hour) {
                return true;
            } else if (hour == date.hour) {
                return minutes > date.minutes;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean equals(SimplifiedDate date) {
        return dayOfTheWeek == date.dayOfTheWeek && hour == date.hour && minutes == date.minutes;
    }

    public String getDayOfWeekAsString() {
        String day = "";
        switch (dayOfTheWeek) {
            case 0:
                day = "Monday";
                break;
            case 1:
                day = "Tuesday";
                break;
            case 2:
                day = "Wednesday";
                break;
            case 3:
                day = "Thursday";
                break;
            case 4:
                day = "Friday";
                break;
            case 5:
                day = "Saturday";
                break;
            case 6:
                day = "Sunday";
                break;
        }
        return day;
    }

    @Override
    public String toString() {
        return getDayOfWeekAsString() + " " + hour + ":" + minutes;
    }

    @Override
    public int hashCode() {
        int result = dayOfTheWeek;
        result = 31 * result + hour;
        result = 31 * result + minutes;
        return result;
    }
}
