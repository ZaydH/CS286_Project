#!/bin/bash
export HADOOP_HOME=/opt/mapr/hadoop/hadoop-0.20.2
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native/Linux-amd64-64
export CLASSPATH=$HADOOP_HOME/lib/*:$HADOOP_HOME/*
export HADOOP_CLASSPATH=$CLASSPATH
export JAR_NAME=KNN.jar

rm -rf /user/$USER/projectnew/project/OUT
hadoop jar $JAR_NAME KNN_YashiKamboj.KNNDriver /user/user01/projectnew/project/training_set/training_set.txt 5 Overlap /user/user01/projectnew/project/test_set/ /user/$USER/projectnew/project/OUT 
