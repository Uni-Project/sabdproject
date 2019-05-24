#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "preprocessing.Preprocess" --master "local" project1-1.0-SNAPSHOT.jar