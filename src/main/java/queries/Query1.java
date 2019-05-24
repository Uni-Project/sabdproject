package queries;

import com.google.gson.Gson;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import utils.DetectionParser;
import utils.HDFSWriter;
import utils.Query1Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Query1 {

    public static void main(String[] args){

        final int PERCENT = 15;

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext spark = new JavaSparkContext(conf);

        JavaPairRDD<Integer, String> query1 = spark.textFile("hdfs://master:54310/PreProcessed/weather_description/part-00000")
                .map(line -> DetectionParser.parse(line))
                .filter(detection -> detection.getMonth() == 3 ||
                        detection.getMonth() == 4 ||
                        detection.getMonth() == 5)
                .filter(description -> description.getWeather().equals("sky is clear"))
                .mapToPair(tuple -> new Tuple2<>(new Tuple4<>(tuple.getYear(), tuple.getMonth(), tuple.getDay(), tuple.getCity()), 1))
                .reduceByKey((tuple1, tuple2) -> tuple1 + tuple2)
                .filter(x -> x._2 >= 15)
                .mapToPair(x -> new Tuple2<>(new Tuple3<>(x._1._1(), x._1._2(), x._1._4()),1))
                .reduceByKey((x, y) -> x+y)
                .filter(days -> days._2 == 3)
                .mapToPair(tuple -> new Tuple2<>(tuple._1._1(), tuple._1._3()))
                .reduceByKey((t1, t2) -> t1 + "," + t2)
                .cache();

        query1.coalesce(1).saveAsTextFile("hdfs://master:54310/query1_raw");

        long id = 1;
        List<Query1Result> results = new ArrayList<>();
        List<Tuple2<Integer, String>> query1Listed = query1.collect();

        for (int i=0; i<query1Listed.size(); i++) {
            Query1Result res = new Query1Result(id, query1Listed.get(i)._1, Arrays.asList(query1Listed.get(i)._2.split(",")));
            results.add(res);
            id++;
        }

        Gson jsonResult = new Gson();
        HDFSWriter.write(jsonResult.toJson(results), "hdfs://master:54310/query1/query1.json");

        spark.stop();

    }

}
