#!/bin/bash

export HADOOP_HOME=/opt/mapr/hadoop/hadoop-0.20.2
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native/Linux-amd64-64
export CLASSPATH=$HADOOP_HOME/lib/*:$HADOOP_HOME/*
export HADOOP_CLASSPATH=$CLASSPATH

export OUT_DIR=/user/$USER/data/classification_results

# Copy the data over for debug and delete the output directory
cp -rf  ../data /user/$USER/
hadoop fs -rmr $OUT_DIR 

# Run the MapReduce job normally.
#hadoop jar ensemble.jar Ensemble.EnsembleDriver -input /user/$USER/data/mvdm -input /user/$USER/data/naive_bayes -input /user/$USER/data/overlap -output /user/$USER/data/classification_results

hadoop jar ensemble.jar Ensemble.EnsembleDriver /user/$USER/data/overlap /user/$USER/data/classification_results
