#!/bin/bash

export HADOOP_HOME=/opt/mapr/hadoop/hadoop-0.20.2
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native/Linux-amd64-64
export CLASSPATH=$HADOOP_HOME/*:$HADOOP_HOME/lib/* 
export HADOOP_CLASSPATH=$CLASSPATH

export JAR_NAME=Ensemble.jar

export CLASSES_DIR=/home/user01/CS286_Project/EnsembleClassifier/classes

javac -d $CLASSES_DIR EnsembleMapper.java
javac -d $CLASSES_DIR EnsembleReducer.java
jar -cvf $ENSEMBLE_JAR -C $CLASSES_DIR/ .
javac -classpath $CLASSPATH:Ensemble.jar -d $CLASSES_DIR EnsembleDriver.java
jar -uvf $ENSEMBLE_JAR -C $CLASSES_DIR/ .
