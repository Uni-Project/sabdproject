package preprocessing;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import utils.FileUtils;
import utils.Validation;

import java.util.ArrayList;
import java.util.List;

public class Preprocess {

    public static void main(String[] args) {

        String regexPattern = "\\.(?=[^\\.]+$)";
        String outputhFolder = "hdfs://master:54310/PreProcessed";

        //old code...it was a porting from local files to hdfs files
        //-------------------------------------------------------
        List<FileUtils> files = new ArrayList<>();
        files.add(new FileUtils("humidity.csv", "hdfs://master:54310/dataset/humidity.csv"));
        files.add(new FileUtils("pressure.csv", "hdfs://master:54310/dataset/pressure.csv"));
        files.add(new FileUtils("temperature.csv", "hdfs://master:54310/dataset/temperature.csv"));
        files.add(new FileUtils("weather_description.csv", "hdfs://master:54310/dataset/weather_description.csv"));
        //-------------------------------------------------------

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
        for (int i=0; i<files.size(); i++) {
            Dataset<Row> loaded = spark.read()
                    .format("csv")
                    .option("header", "true")
                    .load(files.get(i).getPath());

            columns = loaded.columns();

            Dataset<Row> splitted = null;
            for (int j=1; j<columns.length; j++) {
                splitted = loaded.select(columns[0], columns[j])
                        .withColumn("City", functions.lit(columns[j]))
                        .withColumnRenamed(columns[j], files.get(i).getName().split(regexPattern)[0]);

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
                    .filter(line -> Validation.validate(files.get(j).getName().split(regexPattern)[0], line))
                    .coalesce(1, true)
                    .saveAsTextFile(outputhFolder + "/" + files.get(i).getName().split(regexPattern)[0]);
            tmp = null;
        }

        spark.stop();
    }
}
