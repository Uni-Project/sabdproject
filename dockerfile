FROM effeerre/hadoop

# add spark 2.4.3 (latest version)
RUN wget http://it.apache.contactlab.it/spark/spark-2.4.3/spark-2.4.3-bin-hadoop2.7.tgz ; tar -zxf spark-2.4.3-bin-hadoop2.7.tgz -C /usr/local/ ; rm spark-2.4.3-bin-hadoop2.7.tgz
RUN cd /usr/local && ln -s ./spark-2.4.3-bin-hadoop2.7 spark
ENV SPARK_HOME /usr/local/spark
ENV PATH $PATH:$SPARK_HOME/bin
RUN mkdir $SPARK_HOME/yarn-remote-client

# add nifi 1.9.2 (latest version)
RUN wget http://it.apache.contactlab.it/nifi/1.9.2/nifi-1.9.2-bin.tar.gz; tar -zxf nifi-1.9.2-bin.tar.gz -C /usr/local/ ; rm nifi-1.9.2-bin.tar.gz
RUN cd /usr/local && ln -s ./nifi-1.9.2 nifi
ENV NIFI_HOME /usr/local/nifi
ADD data/flow.xml.gz $NIFI_HOME/conf

# add scala 2.12.8
RUN wget http://www.scala-lang.org/files/archive/scala-2.12.8.tgz ; tar -zxf scala-2.12.8.tgz -C /usr/local/ ; rm scala-2.12.8.tgz
RUN cd /usr/local && ln -s ./scala-2.12.8 scala
ENV SCALA_HOME /usr/local/scala
ENV PATH $PATH:$SCALA_HOME/bin

# add hbase
RUN wget http://it.apache.contactlab.it/hbase/2.1.4/hbase-2.1.4-bin.tar.gz; tar -zxf hbase-2.1.4-bin.tar.gz -C /usr/local/ ; rm hbase-2.1.4-bin.tar.gz
RUN cd /usr/local && ln -s ./hbase-2.1.4 hbase
ENV HBASE_HOME /usr/local/hbase
RUN cd $HBASE_HOME/conf; mv hbase-site.xml hbase-site.xml-old;
ADD data/hbase-site.xml /user/local/hbase/conf
RUN cd $HBASE_HOME/conf; echo "export JAVA_HOME=$JAVA_HOME" >> hbase-env.sh;


# expose port for nifi web interface
EXPOSE 8080

#Yarn ports
EXPOSE 8030 8031 8032 8033 8040 8042 8088 
#Hbase ports
EXPOSE 16010

# #Other ports
EXPOSE 49707 2122 53411 7077 56302 54310 9870 50070