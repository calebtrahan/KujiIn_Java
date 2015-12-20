package kujiin.util.lib;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static String formatlengthshort(int sec) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if (sec >= 3600) {
            hours = sec / 3600;
            sec -= hours * 3600;
        }
        if (sec >= 60) {
            minutes = sec / 60;
            sec -= minutes * 60;
        }
        seconds = sec;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    public static LocalDate converttolocaldate(String dateformatted) {
        return LocalDate.parse(dateformatted, dateFormat);
    }
    public static String convertfromlocaldatetostring(LocalDate localdate) {
        return localdate.format(dateFormat);
    }
    public static String getformattedtime() {
        return "";
    }
    public static Double convertminutestodecimalhours(int mins) {
        int hours = mins / 60;
        int minutes = mins % 60;
        double decimalhours = minutes / 60;
        decimalhours += hours;
        return decimalhours;
    }
    public static int convertdecimalhourstominutes(double decimalhours) {
        Double minutes = 60 * decimalhours;
        return minutes.intValue();
    }

}