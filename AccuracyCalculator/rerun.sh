#!/bin/bash

export HADOOP_HOME=/opt/mapr/hadoop/hadoop-0.20.2
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native/Linux-amd64-64
export CLASSPATH=$HADOOP_HOME/lib/*:$HADOOP_HOME/*
export HADOOP_CLASSPATH=$CLASSPATH

# Define the input and output directories
export IN_DIR=/user/$USER/data/classification_results
export OUT_DIR=/user/$USER/data/accuracy

# Define Jar and Driver class information
export JAR_NAME=accuracy_calc.jar
export DRIVER_CLASS=Accuracy.AccuracyCalcDriver

# Delete the output directory
hadoop fs -rmr $OUT_DIR 

# Run the MapReduce job normally.
hadoop jar $JAR_NAME $DRIVER_CLASS $IN_DIR $OUT_DIR
