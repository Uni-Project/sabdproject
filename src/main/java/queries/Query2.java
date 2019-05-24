package queries;

import com.google.gson.Gson;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.*;
import spire.random.rng.Serial;
import utils.AttributesParser;
import utils.DetectionParser;
import utils.HDFSWriter;

import java.io.Serializable;
import java.lang.Double;
import java.util.ArrayList;
import java.util.List;

public class Query2 {


    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query2");
        JavaSparkContext spark = new JavaSparkContext(conf);

        String[] files = {  "hdfs://master:54310/PreProcessed/humidity/part-00000",   //1 == humidity
                            "hdfs://master:54310/PreProcessed/temperature/part-00000",//2 == temperature
                            "hdfs://master:54310/PreProcessed/pressure/part-00000"};  //3 == pressure
        String[] fileName = {"humidity", "temperature", "pressure"};

        List<JavaPairRDD<Tuple3<String, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> RDDs = new ArrayList<>();

        JavaPairRDD<String,  String> attributes = spark.textFile("hdfs://master:54310/attributes/city_attributes.csv")
                .map(line -> AttributesParser.parse(line))
                .mapToPair(city -> new Tuple2<>(city.getCity(), city.getCountry()))
                .cache();


                for (int i=0; i< files.length; i++) {

                    RDDs.add(spark.textFile(files[i])
                            .map(line -> DetectionParser.parse(line))
                            .mapToPair(tuple -> new Tuple2<>(tuple.getCity(), new Tuple3<>(tuple.getYear(), tuple.getMonth(), tuple.getValue())))
                            .join(attributes)
                            .mapToPair(tuple -> new Tuple2<>(new Tuple3<>(tuple._2._2, tuple._2._1._1(), tuple._2._1._2()), new Tuple3<>(tuple._2._1._3(), tuple._2._1._3(), 1)))
                            .reduceByKey((tuple1, tuple2) -> new Tuple3<>(Math.max(tuple1._1(), tuple2._1()), Math.min(tuple1._2(), tuple2._2()), tuple1._3() + tuple2._3()))
                            .mapToPair(tuple -> new Tuple2<>(new Tuple3<>(tuple._1._1(), tuple._1._2(), tuple._1._3()), new Tuple5<>(tuple._2._1() - tuple._2._2(), Math.pow(tuple._2._1() - tuple._2._2(), 2), tuple._2._3(), tuple._2._1(), tuple._2._2())))
                            .reduceByKey((tuple1, tuple2) -> new Tuple5<>(tuple1._1() + tuple2._1(), tuple1._2() + tuple2._2(), tuple1._3(), tuple1._4(), tuple1._5()))
                            .mapToPair(tuple -> {
                                Double mean = tuple._2._1() / tuple._2._3();
                                Double sq_mean = tuple._2._2() / tuple._2._3();
                                Double std = Math.sqrt(sq_mean - mean * mean);
                                Double max = tuple._2._4();
                                Double min = tuple._2._5();
                                return new Tuple2<>(new Tuple3<>(tuple._1._1(), tuple._1._2(), tuple._1._3()), new Tuple4<>(mean, std, min, max));
                            })
                            .reduceByKey((tuple1, tuple2) -> new Tuple4<>(tuple1._1() + tuple2._1(), tuple1._2() + tuple2._2(), tuple2._3(), tuple2._4()))
                            .cache());
                }


                for (int k=0; k<files.length; k++)
                    RDDs.get(k).coalesce(1).saveAsTextFile("hdfs://master:54310/query2_raw");


                long id = 1;
                List<Query2Result> results = new ArrayList<>();

                for (int i=0; i<RDDs.size(); i++) {
                    for (Tuple2<Tuple3<String, Integer, Integer>, Tuple4<Double, Double, Double, Double>> row : RDDs.get(i).collect()) {
                        Query2Result res = new Query2Result(id, row._1._1(), row._1._2(), row._1._3(), row._2._4(), row._2._3(), row._2._1(), row._2._1());
                        results.add(res);
                        id++;
                    }

                    Gson jsonResult = new Gson();
                    HDFSWriter.write(jsonResult.toJson(results), "hdfs://master:54310/query2/" + fileName[i] + ".json");

                    id = 1;
                }

        spark.stop();

    }

    private static class Query2Result implements Serializable {
        private long id;

        private String country;
        private int year;
        private int month;

        private double max;
        private double min;
        private double stdev;
        private double mean;

        public Query2Result(long id, String country, int year, int month, double max, double min, double stdev, double mean) {
            this.id = id;
            this.country = country;
            this.year = year;
            this.month = month;
            this.max = max;
            this.min = min;
            this.stdev = stdev;
            this.mean = mean;
        }
    }

}



