/**
 *
 *  @author Kurzau Kiryl S24911
 *
 */

package zad1;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {
    public static String passed(String from, String to){
        String response = "";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", new Locale("pl"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE) 'godz.' HH:mm", new Locale("pl"));
        try {
            if (!from.contains("T") && !to.contains("T")) {
                LocalDate start = LocalDate.parse(from);
                LocalDate end = LocalDate.parse(to);
                long daysBetween = ChronoUnit.DAYS.between(start, end);
                response += "Od "+
                        dateFormatter.format(start) + " do "
                        + dateFormatter.format(end) + "\n";
                response += "- mija: " + daysBetween+" " +(daysBetween!=1? "dni":"dzień") + ", " +
                        "tygodni " + Math.round(daysBetween / 7.0 *100)/100.0 + "\n";
                response += "- kalendarzowo: " + getDateCalendar(start,end);

            } else {
                LocalDateTime start = LocalDateTime.parse(from);
                LocalDateTime end = LocalDateTime.parse(to);
                ZonedDateTime zdt1 = ZonedDateTime.of(start, ZoneId.of("Europe/Warsaw"));
                ZonedDateTime zdt2 = ZonedDateTime.of(end, ZoneId.of("Europe/Warsaw"));
                long daysBetween = ChronoUnit.DAYS.between(zdt1, zdt2);
                long hours = ChronoUnit.HOURS.between(zdt1, zdt2);
                long minutes = ChronoUnit.MINUTES.between(zdt1, zdt2);
                response += "Od "+
                        dateTimeFormatter.format(start) + " do "
                        + dateTimeFormatter.format(end) + "\n";
                response += "- mija: " + daysBetween+" " +(daysBetween!=1? "dni":"dzień") + ", " +
                        "tygodni " + Math.round(daysBetween / 7.0 *100)/100.0 + "\n";
                response +="- godzin: " + hours + ", " + "minut: " + minutes + "\n";
                response += "- kalendarzowo: " + getDateCalendar(start.toLocalDate(),end.toLocalDate());
            }
        }catch(Exception e){
            return "***"+e;
        }


        return  response;

    }


    public static String getDateCalendar(LocalDate from, LocalDate to){
        Period period = Period.between(from, to);
        String response = "";
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        if (years!=0){
            response+=years + " " +(years>1? "lata":"rok");
            if(months != 0 ){
                response+=", ";
            }
        }
        if (months!=0){
            response+=months + " " +(months>1? "miesiące" : "miesiąc");
            if (days!=0){
                response+=", ";
            }
        }
        if(days!=0){
            response+=days + " " +(days>1? "dni" : "dzień") + " ";
        }
        return response;
    }
}
