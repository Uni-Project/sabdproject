package utils;

public class DetectionParser {

    public static Detection parse(String line) {
        Detection detection = null;

        String[] CSVvalues = line.split(",");
        detection = new Detection(
                CSVvalues[0],
                CSVvalues[1],
                CSVvalues[2]
        );

        return detection;
    }
}
