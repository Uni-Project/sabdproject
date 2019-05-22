package utils;

public class Validation {

    private static Double[] threshold(String type) {
        double valueMax;
        double valueMin;
        switch (type) {
            case "humidity":
                valueMax = 100.0; //umidità espressa in percentuale
                valueMin = 0.0;
                break;
            case "temperature":
                valueMin = 170.0;
                valueMax = 345.0;
                break;
            case "pressure":
                valueMin = 850.0;
                valueMax = 1100.0;
                break;
            default:
                return null;
        }
        return new Double[]{valueMin, valueMax};
    }

    public static Boolean validate(String type, String line) {
        String[] values = line.split(",");
        Double[] bound = Validation.threshold(type);

        //Validate datetime format
        if (DateUtil.checkDate(values[0]) == null) {
            return false;
        }

        //Check correct file
        if (bound == null) {
            //Check valid weather description
            if (type.equals("weather_description") && (!values[1].equals("null") && !values[1].equals("''")))
                return true;
            else
                return false;
        }

        //Check thresholds and values parsing
        try {
            Double value = Double.parseDouble(values[1]);
            if (value < bound[0] || value > bound[1])
                return false;

        } catch (NullPointerException | NumberFormatException ex) {
            return false;
        }

        return true;

    }
}