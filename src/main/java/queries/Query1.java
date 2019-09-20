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

        long startstart = System.currentTimeMillis();

        final int PERCENT = 18;

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query1");
        JavaSparkContext spark = new JavaSparkContext(conf);

        long start = 0, stop = 0;

        List<Long> tempi = new ArrayList<>();

        for (int i=0; i<20; i++) {

            start = System.currentTimeMillis();

            spark.textFile("hdfs://master:54310/PreProcessed/weather_description/part-00000")
                    .map(line -> DetectionParser.parse(line))
                    .filter(detection -> (detection.getMonth() == 3 ||
                            detection.getMonth() == 4 ||
                            detection.getMonth() == 5) && detection.getWeather().equals("sky is clear"))
                    //.filter(description -> description.getWeather().equals("sky is clear"))
                    .mapToPair(tuple -> new Tuple2<>(new Tuple4<>(tuple.getYear(), tuple.getMonth(), tuple.getDay(), tuple.getCity()), 1))
                    .reduceByKey((tuple1, tuple2) -> tuple1 + tuple2)
                    .filter(x -> x._2 >= PERCENT)
                    .mapToPair(tuple -> new Tuple2<>(new Tuple3<>(tuple._1._1(), tuple._1._2(), tuple._1._4()), 1))
                    .reduceByKey((tuple1, tuple2) -> tuple1 + tuple2)
                    .filter(tuple -> tuple._2 >= 15)
                    .mapToPair(tuple -> new Tuple2<>(new Tuple2<>(tuple._1._1(), tuple._1._3()), 1))
                    .reduceByKey((tuple1, tuple2) -> tuple1 + tuple2)
                    .filter(month -> month._2 == 3)
                    .mapToPair(tuple -> new Tuple2<>(tuple._1._1, tuple._1._2))
                    .reduceByKey((tuple1, tuple2) -> tuple1 + ", " + tuple2)
                    .coalesce(1)
                    .saveAsTextFile("hdfs://master:54310/query1_raw_" + i);


                /*
                .mapToPair(x -> new Tuple2<>(new Tuple3<>(x._1._1(), x._1._2(), x._1._4()),1))
                .reduceByKey((x, y) -> x+y)
                .filter(days -> days._2 == 3)
                .mapToPair(tuple -> new Tuple2<>(tuple._1._1(), tuple._1._3()))
                .reduceByKey((t1, t2) -> t1 + "," + t2)
                .saveAsTextFile("s3a://AKIAIF5HDGSPWSGT237A:DdzXIDxAfd3Z0VdU2mmS947kQSF0wsg2weaFs4lB@clusteremrprova/processati2");
                 */
            stop = System.currentTimeMillis();
            if (i==0)
                tempi.add(stop-startstart);
            else
                tempi.add(stop-start);
        }

        for (long t : tempi)
            System.out.println(t);


        /*
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
         */
        spark.stop();

    }

}
