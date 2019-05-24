#!/bin/bash
$SPARK_HOME/bin/spark-submit --class "queries.Query2" --master "local" target/project1-1.0-SNAPSHOT.jar