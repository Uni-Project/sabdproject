package utils;

public class AttributesParser {

    public static Attributes parse(String line) {
        Attributes attributes = null;

        String[] CSVvalues = line.split(",");
        attributes = new Attributes(
                CSVvalues[0],
                CSVvalues[1],
                CSVvalues[2]
        );

        return attributes;
    }
}





