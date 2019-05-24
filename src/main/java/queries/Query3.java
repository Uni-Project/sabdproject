package queries;

import com.google.gson.Gson;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import utils.AttributesParser;
import utils.DetectionParser;
import utils.HDFSWriter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Query3 {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local")
                .setAppName("Query3");
        JavaSparkContext spark = new JavaSparkContext(conf);


        JavaPairRDD<String,  Tuple2<String, Integer>> attributes = spark.textFile("hdfs://master:54310/attributes/city_attributes.csv")
                .map(line -> AttributesParser.parse(line))
                .mapToPair(city -> new Tuple2<>(city.getCity(), new Tuple2<>(city.getCountry(), city.getUtc())))
                .cache();

        JavaPairRDD<String, Tuple2<List<Integer>, List<Tuple2<String, Double>>>> query3 = spark.textFile("hdfs://master:54310/PreProcessed/temperature/part-00000")
                .map(line -> DetectionParser.parse(line))
                .filter(x -> x.getYear() >= 2016 || x.getYear() <= 2017 &&
                        (x.getMonth() >= 1 && x.getMonth() <= 4) &&
                        (x.getMonth() >= 6 && x.getMonth() <= 9))
                .mapToPair(tuple -> new Tuple2<>(tuple.getCity(), new Tuple4<>(tuple.getYear(), tuple.getMonth(), tuple.getHour(), tuple.getValue())))
                .join(attributes)
                .mapToPair(tuple -> new Tuple2<>(new Tuple3<>(tuple._2._2._1, tuple._1, tuple._2._1._1()), new Tuple3<>(tuple._2._1._2(), (tuple._2._1._3() - tuple._2._2._2) % 24, tuple._2()._1._4()))) // <nazione, città, anno, mese>, <(ora-utc)%24, value>
                .filter(x -> x._2._2() >= 12 && x._2._2() <= 15)
                .mapToPair(x -> {
                    if (x._2._1() <= 4)
                        return new Tuple2<>(new Tuple4<>(x._1._1(), x._1._2(), x._1._3(), 0), new Tuple2<>(x._2._3(), 1)); //nazione, città, anno, id_mesi>,<temperatura,1>
                    else
                        return new Tuple2<>(new Tuple4<>(x._1._1(), x._1._2(), x._1._3(), 1), new Tuple2<>(x._2._3(), 1));
                })
                .reduceByKey((t1, t2) -> new Tuple2<>(t1._1 + t2._1, t1._2 + t2._2))
                .mapToPair(x -> {
                    Double mean = x._2._1 / x._2._2;
                    return new Tuple2<>(new Tuple3<>(x._1._1(), x._1._2(), x._1._3()), mean);
                })
                .reduceByKey((t1, t2) -> Math.abs(t1 - t2))
                .mapToPair(x -> new Tuple2<>(new Tuple2<>(x._1._1(), x._1._3()), new Tuple2<>(x._1._2(), x._2()))) //<nazione, anno>, <città, diff>
                .groupByKey()
                .map(x -> {
                    List<Tuple2<String, Double>> list = new ArrayList<>();
                    for (Tuple2 i : x._2) {
                        list.add(i);
                    }
                    list.sort(new Comparator<Tuple2<String, Double>>() {
                        @Override
                        public int compare(Tuple2<String, Double> o1, Tuple2<String, Double> o2) {
                            if (o1._2 <= o2._2) {
                                return 1;
                            }
                            return -1;
                        }
                    });
                    return new Tuple2<>(x._1, list);
                })
                .mapToPair(x -> {
                    List<Integer> year = new ArrayList<>();
                    year.add(x._1._2);
                    return new Tuple2<>(x._1._1, new Tuple2<>(year, x._2));
                })
                .reduceByKey((t1, t2) -> {
                    List<Integer> index = new ArrayList<>();
                    List<Tuple2<String, Double>> top3 = new ArrayList<>();
                    if (t1._1.get(0) == 2017) {
                        index = getIndex(t1._2, t2._2);
                    } else index = getIndex(t2._2, t1._2);
                    top3.add(t1._2.get(0));
                    top3.add(t1._2.get(1));
                    top3.add(t1._2.get(2));
                    return new Tuple2<>(index, top3);
                }).cache();

        query3.coalesce(1).saveAsTextFile("hdfs://master:54310/query3_raw");


        long id = 1;
        List<Query3Result> results = new ArrayList<>();
        List<Tuple2<String, Tuple2<List<Integer>, List<Tuple2<String, Double>>>>> query3Listed = query3.collect();
        for (Tuple2<String, Tuple2<List<Integer>, List<Tuple2<String, Double>>>> row : query3Listed) {
            for (int i=0; i<=row._2._2.size(); i++) {
                Query3Result res = new Query3Result(id, row._2._1.get(i), i, row._1, row._2._2.get(i)._1, row._2._2.get(i)._2);
                results.add(res);
                id++;
            }
        }
        Gson jsonResult = new Gson();
        HDFSWriter.write(jsonResult.toJson(results), "hdfs://master:54310/query3.json");


        spark.stop();

    }

    public static List<Integer> getIndex(List<Tuple2<String, Double>> l1, List<Tuple2<String, Double>> l2){

        List<Integer> indici = new ArrayList<Integer>();
        for(int i = 0; i < 3; i++) {
            Tuple2<String, Double> t1 = l1.get(i);
            for(int j = 0; j < l2.size(); j++) {
                Tuple2<String, Double> t2 = l2.get(j);
                if(t1._1.equals(t2._1)) {
                    indici.add(l2.indexOf(t2)+1);
                    System.out.println(indici);
                }
            }
        }
        return indici;
    }

    private static class Query3Result implements Serializable {
        private long id;

        private int position2016;
        private int position2017;
        private String country;
        private String city;
        private double value;

        public Query3Result(long id, int position2016, int position2017, String country, String city, double value) {
            this.id = id;
            this.position2016 = position2016;
            this.position2017 = position2017;
            this.country = country;
            this.city = city;
            this.value = value;
        }
    }
}
