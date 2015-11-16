#!/bin/bash

export OOZIE_DIR=oozie_flow
export DATA_DIR=data
export PREPROCESSOR_DIR=RecipePreprocessor

export ENSEMBLE_DIR=EnsembleClassifier

export ACCURACY_CALC_DIR=AccuracyCalculator

# Remake the oozie lib directory from scratch.
rm -rf /home/$USER/CS286_Project/$OOZIE_DIR/lib 
mkdir /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the processor jar into the oozie lib directory.
cp /home/$USER/CS286_Project/$PREPROCESSOR_DIR/Preprocessor.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy over the source data set.
hadoop fs -rmr /user/$USER/$DATA_DIR
hadoop fs -mkdir -p /user/$USER/$DATA_DIR
hadoop fs -copyFromLocal /home/$USER/CS286_Project/RecipePreprocessor/train.json /user/$USER/$DATA_DIR

# Copy over the ensemble testing data
hadoop fs -rmr /user/$USER/data/mvdm
hadoop fs -copyFromLocal /home/$USER/CS286_Project/data/mvdm /user/$USER/$DATA_DIR
hadoop fs -copyFromLocal /home/$USER/CS286_Project/data/overlap /user/$USER/$DATA_DIR
hadoop fs -copyFromLocal /home/$USER/CS286_Project/data/naive_bayes /user/$USER/$DATA_DIR

# Copy the ensemble jar into the oozie lib directory.
cp /home/$USER/CS286_Project/$ENSEMBLE_DIR/ensemble.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the accuracy calculator jar into the oozie lib directory.
cp /home/$USER/CS286_Project/$ACCURACY_CALC_DIR/accuracy_calc.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Last Step - Copy the local Oozie flow directory to the /user/user01 directory.
hadoop fs -rmr /user/$USER/$OOZIE_DIR
hadoop fs -copyFromLocal /home/$USER/CS286_Project/$OOZIE_DIR /user/$USER/
