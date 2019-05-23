package queries;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import scala.Tuple4;
import utils.DetectionParser;

public class Query1 {

    public static void main(String[] args){

        final int PERCENT = 15;

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Hello World");
        JavaSparkContext spark = new JavaSparkContext(conf);

        spark.textFile("hdfs://master:54310/PreProcessed/weather_description/part-00000")
                .map(line -> DetectionParser.parse(line))
                .filter(detection -> detection.getMonth() == 3 ||
                                        detection.getMonth() == 4 ||
                                        detection.getMonth() == 5)
                .filter(description -> description.getWeather().equals("sky is clear"))
                .mapToPair(tuple -> new Tuple2<>(new Tuple4<>(tuple.getYear(), tuple.getMonth(), tuple.getDay(), tuple.getCity()), 1))
                .reduceByKey((tuple1, tuple2) -> tuple1+tuple2)
                .filter(days -> days._2 == 3)
                .coalesce(1).saveAsTextFile("hdfs://master:54310/query1_raw");

        spark.stop();

    }
}
