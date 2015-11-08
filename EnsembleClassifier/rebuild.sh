#!/bin/bash

export HADOOP_HOME=/opt/mapr/hadoop/hadoop-0.20.2
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native/Linux-amd64-64
export CLASSPATH=$HADOOP_HOME/*:$HADOOP_HOME/lib/* 
export HADOOP_CLASSPATH=$CLASSPATH

rm -rf classes
mkdir classes

javac -d classes EnsembleMapper.java
javac -d classes EnsembleReducer.java
jar -cvf Ensemble.jar -C classes/ .
javac -classpath $CLASSPATH:Iris.jar -d classes EnsembleDriver.java
jar -uvf Ensemble.jar -C classes/ .
