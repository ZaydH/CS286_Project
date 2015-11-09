#!/bin/bash

export OOZIE_DIR=oozie_flow
export DATA_DIR=data
export PREPROCESSOR_DIR=RecipePreprocessor

export ENSEMBLE_DIR=EnsembleClassifier

export ACCURACY_CALC_DIR=AccuracyCalculator

# Remake the oozie lib directory from scratch.
rm -rf /home/user01/CS286_Project/$OOZIE_DIR/lib 
mkdir /home/user01/CS286_Project/$OOZIE_DIR/lib

# Copy the processor jar into the oozie lib directory.
cp /home/user01/CS286_Project/$PREPROCESSOR_DIR/Preprocessor.jar /home/user01/CS286_Project/$OOZIE_DIR/lib

# Copy over the source data set.
rm -rf /user/user01/$DATA_DIR
mkdir -p /user/user01/$DATA_DIR
cp /home/user01/CS286_Project/RecipePreprocessor/train.json /user/user01/$DATA_DIR

# Copy over the ensemble testing data
rm -rf /user/user01/data/mvdm
cp -r /home/user01/CS286_Project/data/mvdm /user/user01/$DATA_DIR
cp -r /home/user01/CS286_Project/data/overlap /user/user01/$DATA_DIR
cp -r /home/user01/CS286_Project/data/naive_bayes /user/user01/$DATA_DIR

# Copy the ensemble jar into the oozie lib directory.
cp /home/user01/CS286_Project/$ENSEMBLE_DIR/ensemble.jar /home/user01/CS286_Project/$OOZIE_DIR/lib

# Copy the accuracy calculator jar into the oozie lib directory.
cp /home/user01/CS286_Project/$ACCURACY_CALC_DIR/accuracy_calc.jar /home/user01/CS286_Project/$OOZIE_DIR/lib

# Last Step - Copy the local Oozie flow directory to the /user/user01 directory.
hadoop fs -rmr /user/user01/$OOZIE_DIR
hadoop fs -copyFromLocal /home/user01/CS286_Project/$OOZIE_DIR /user/user01/
