docker network create --driver bridge spark_network

docker run -t -i -p 50075:50075 -d --network=spark_network --name=slave1 sparkimg
docker run -t -i -p 50076:50075 -d --network=spark_network --name=slave2 sparkimg
docker run -t -i -p 50077:50075 -d --network=spark_network --name=slave3 sparkimg
docker run -t -i -p 54310:54310 -p 50070:50070 -p 50060:50060 -p 9870:9870 -p 8080:8080 -p 50030:50030 -p 8088:8088 -p 19888:19888  -p 8030:8030 -p 8031:8031 -p 8032:8032 -p 8033:8033 -p 8040:8040 -p 8042:8042 --network=spark_network --name=master -v $PWD/data:/data sparkimg