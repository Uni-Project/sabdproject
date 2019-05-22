package preprocessing;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import utils.FileUtils;
import utils.Validation;

import java.io.File;

public class Preprocess {

    public static void main(String[] args) {

        String regexPattern = "\\.(?=[^\\.]+$)";
        String outputhFolder = "PreprocOutput";
        String inputFolder = "/Users/simone/Projects/proj/src/main/java/data/querydataset/";
        File[] files = FileUtils.checkFileNames(inputFolder);
        if (files.length == 0) return; //exit if there isn't files

        SparkSession spark = SparkSession.builder()
                .appName("Preprocessing and Validation")
                .master("local")
                .config("com.databricks.spark.csv", "true")
                .config("spark.debug.maxToStringFields", "10000")
                .getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");

        /*
         *   Preprocessing: PART 1
         *   Adjust dataset to improve future queries
         */
        Dataset<Row> tmp = null;
        String[] columns;
        for (int i=0; i<files.length; i++) {
            Dataset<Row> loaded = spark.read()
                    .format("csv")
                    .option("header", "true")
                    .load(files[i].getAbsolutePath());

            columns = loaded.columns();

            Dataset<Row> splitted = null;
            for (int j=1; j<columns.length; j++) {
                splitted = loaded.select(columns[0], columns[j])
                        .withColumn("City", functions.lit(columns[j]))
                        .withColumnRenamed(columns[j], files[i].getName().split(regexPattern)[0]);

                if (tmp == null)
                    tmp = splitted;
                else
                    tmp = tmp.union(splitted);
            }

            /*
             *   Preprocessing: PART 2
             *   Validate dataset
             */

            int j = i;
            tmp.toJavaRDD()
                    .map(line -> line.mkString(",")) //delete brackets
                    .filter(line -> Validation.validate(files[j].getName().split(regexPattern)[0], line))
                    .coalesce(1, true)
                    .saveAsTextFile(outputhFolder + "/" + files[i].getName().split(regexPattern)[0]);
            tmp = null;
        }

        spark.stop();
    }
}
