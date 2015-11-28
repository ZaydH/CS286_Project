#!/bin/bash

export HADOOP_HOME=/opt/mapr/hadoop/hadoop-0.20.2
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native/Linux-amd64-64
export CLASSPATH=$HADOOP_HOME/*:$HADOOP_HOME/lib/* 
export HADOOP_CLASSPATH=$CLASSPATH
export JAR_NAME=KNN.jar

rm -rf classes
mkdir classes
rm -f $JAR_NAME

javac -d classes KNNMapper.java
javac -d classes KNNReducer.java
jar -cvf $JAR_NAME -C classes/ .
javac -classpath $CLASSPATH:$JAR_NAME -d classes KNNDriver.java
jar -uvf $JAR_NAME -C classes/ .

rm -rf classes
echo "KNN Jar built successfully"
