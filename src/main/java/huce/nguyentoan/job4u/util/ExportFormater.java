package huce.nguyentoan.job4u.util;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class ExportFormater {
    public static Function<Object, String> STRING =
            value -> value == null
                    ? ""
                    : value.toString();


    public static Function<Object, String> NUMBER =
            value -> value == null
                    ? ""
                    : String.format("%,.0f", value);


    public static Function<Object, String> DATE =
            value -> value == null
                    ? ""
                    : DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .withZone(ZoneId.systemDefault())
                    .format((Instant) value);
}
