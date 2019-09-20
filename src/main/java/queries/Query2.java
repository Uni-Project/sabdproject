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
import utils.Query2Result;

import java.io.Serializable;
import java.lang.Double;
import java.lang.Long;
import java.util.ArrayList;
import java.util.List;
import org.apache.spark.util.StatCounter;

public class Query2 {


    public static void main(String[] args) {

        long startstart = System.currentTimeMillis();
        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query2");
        JavaSparkContext spark = new JavaSparkContext(conf);

        String[] files = {  "hdfs://master:54310/PreProcessed/humidity/part-00000",   //1 == humidity
                "hdfs://master:54310/PreProcessed/temperature/part-00000",//2 == temperature
                "hdfs://master:54310/PreProcessed/pressure/part-00000"};
        String[] fileName = {"humidity", "temperature", "pressure"};

        List<JavaPairRDD<Tuple3<String, Integer, Integer>, Tuple4<Double, Double, Double, Double>>> RDDs = new ArrayList<>();

        long start =0, stop=0;
        List<Long> tempi = new ArrayList<>();

        for (int j=0; j<20; j++) {
            start = System.currentTimeMillis();

        JavaPairRDD<String, String> attributes = spark.textFile("hdfs://master:54310/attributes/city_attributes.csv")
                .map(line -> AttributesParser.parse(line))
                .mapToPair(city -> new Tuple2<>(city.getCity(), city.getCountry()))
                .cache();



            for (int i = 0; i < files.length; i++) {

            spark.textFile(files[i])
                    .map(line -> DetectionParser.parse(line))
                    .mapToPair(tuple -> new Tuple2<>(tuple.getCity(), new Tuple3<>(tuple.getYear(), tuple.getMonth(), tuple.getValue())))
                    .join(attributes)
                    .mapToPair(tuple -> new Tuple2<>(new Tuple3<>(tuple._2._2, tuple._2._1._1(), tuple._2._1._2()), tuple._2._1._3()))
                    .aggregateByKey(
                            new StatCounter(),
                            StatCounter::merge,
                            StatCounter::merge)
                        .map(tuple -> new Tuple2<>(tuple._1, new Tuple4<>(tuple._2.stdev(), tuple._2.mean(), tuple._2.max(), tuple._2.min())))
                    .coalesce(1).saveAsTextFile("hdfs://master:54310/query2_raw_" + j);
            }
            stop = System.currentTimeMillis();
            if (j==0)
                tempi.add(stop-startstart);
            else
                tempi.add(stop-start);


                    /*
                    .reduceByKey((tuple1, tuple2) -> new Tuple4<>(Math.max(tuple1._1(), tuple2._1()), Math.min(tuple1._2(), tuple2._2()), tuple1._3() + tuple2._3(), tuple1._4() + tuple2._4()))
                    .mapToPair(tuple -> {
                        Double mean = tuple._2._4() / tuple._2._3();
                        Double max = tuple._2._1();
                        Double min = tuple._2._2();
                        Integer count = tuple._2._3();
                        return new Tuple2<>(tuple._1, new Tuple4<>(min, max, mean, count));
                    })*/








                            /*
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
                            .saveAsTextFile("query2" + fileName[i]);
                }

                             */


                /*
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

                 */

        }

        for (long t : tempi)
            System.out.println(t);

        spark.stop();

    }
}



