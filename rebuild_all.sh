#!/bin/bash

export OOZIE_DIR=oozie_flow
export DATA_DIR=data
export PREPROCESSOR_DIR=RecipePreprocessor

# Remake the oozie lib directory from scratch.
rm -rf /home/user01/CS286_Project/$OOZIE_DIR/lib 
mkdir /home/user01/CS286_Project/$OOZIE_DIR/lib

# Copy the processor jar into the oozie lib directory.
cp /home/user01/CS286_Project/$PREPROCESSOR_DIR/Preprocessor.jar /home/user01/CS286_Project/$OOZIE_DIR/lib

hadoop fs -rmr /user/user01/$OOZIE_DIR
hadoop fs -copyFromLocal /home/user01/CS286_Project/$OOZIE_DIR /user/user01/
mkdir -p /user/user01/$DATA_DIR/training_set
mkdir -p /user/user01/$DATA_DIR/test_set
mkdir -p /user/user01/$DATA_DIR/cuisines

rm -f /user/user01/$DATA_DIR/train.json
cp /home/user01/CS286_Project/RecipePreprocessor/train.json /user/user01/$DATA_DIR
