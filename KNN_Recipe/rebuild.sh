#!/bin/bash

export HADOOP_HOME=/opt/mapr/hadoop/hadoop-0.20.2
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native/Linux-amd64-64
export CLASSPATH=$HADOOP_HOME/*:$HADOOP_HOME/lib/*
export HADOOP_CLASSPATH=$CLASSPATH

export JAR_NAME=knn.jar

rm -rf classes
mkdir classes
rm -f $JAR_NAME

# Build the jar for this MapReduce flow
javac -d classes src/zayd_hammoudeh/KnnRecipeMapper.java
javac -d classes src/zayd_hammoudeh/KnnRecipeReducer.java
jar -cvf $JAR_NAME -C classes/ .
javac -classpath $CLASSPATH:$JAR_NAME -d classes src/zayd_hammoudeh/KnnRecipeDriver.java
jar -uvf $JAR_NAME -C classes/ .

# Remove the classes folder.
rm -rf classes

