# SABD Project - Part1

## Configure

To start main container:
```sh
cd project_dir/
sh build.sh
sh start_all.sh
```

To start HBase container
```sh
cd project_dir/
sh build_hbase.sh
sh start_hbase.sh
```


Then, move to config folder and launch the script to create HBASE tables
```sh
sh init-hbase.sh
```

On the main container, activate all services
```sh
cd /data
sh start_services.sh
```

Now, on your broser browse [http://localhost:8080/nifi](http://localhost:8080/nifi) and select all the flows, then press "play" button: it will start saving the dataset on HDFS.
You can also browse [http://localhost:9780](http://localhost:9780) for HDFS web interface.

now you can start preprocessing
```sh
cd /data
sh start_preprocessing.sh
```

It will take some time for preprocessing task: all the files in the dataset will be transformed and validated.
Now, you can run queries:
```sh
cd /data
sh start_query1.sh
sh start_query2.sh
sh start_query3.sh
```

You can type `exit ` or `ctrl+c` to exit from containers, and then you can remove them:
```sh
cd project_dir/
sh kill_hbase.sh
sh kill_all.sh
```
