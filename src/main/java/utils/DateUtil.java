package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtil {

    // List of all date formats that we want to parse.
    // Add your own format here.
    private static List<SimpleDateFormat>
    dateFormats = new ArrayList<SimpleDateFormat>() {{
        add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        add(new SimpleDateFormat("yyyy-mm-DD hh:mm:ss"));
    }};

    public static Date checkDate(String input) {
        Date date = null;

        if(input == null) {
            return null;
        }
        for (SimpleDateFormat format : dateFormats) {
            try {
                format.setLenient(false);
                date = format.parse(input);
                return date;
            } catch (ParseException e) {
                //Shhh.. try other formats
            }
        }

        return null;
    }
}
