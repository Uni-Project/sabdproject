#!/usr/bin/env bash
$NIFI_HOME/bin/nifi.sh start
hdfs namenode -format
$HADOOP_HOME/sbin/start-dfs.sh
