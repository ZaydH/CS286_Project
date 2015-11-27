#!/bin/bash

export OOZIE_DIR=oozie_flow
export DATA_DIR=data
export PREPROCESSOR_DIR=RecipePreprocessor

export NAIVE_BAYES_DIR=NaiveBayesClassifier
export KNN_DIR=KNN_Recipe

export ENSEMBLE_DIR=EnsembleClassifier
export ACCURACY_CALC_DIR=AccuracyCalculator

# Remake the oozie lib directory from scratch.
echo "Clear the local Oozie lib directory."
rm -rf /home/$USER/CS286_Project/$OOZIE_DIR/lib
mkdir /home/$USER/CS286_Project/$OOZIE_DIR/lib 

# Copy the processor jar into the oozie lib directory.
echo "Copy the preprocessor standard Java jar to the local lib directory."
cp /home/$USER/CS286_Project/$PREPROCESSOR_DIR/Preprocessor.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy over the source data set.
echo "Recreate the data directory for the entire oozie."
hadoop fs -rmr /user/$USER/$DATA_DIR >/dev/null
hadoop fs -mkdir -p /user/$USER/$DATA_DIR
hadoop fs -copyFromLocal /home/$USER/CS286_Project/RecipePreprocessor/train.json /user/$USER/$DATA_DIR

# Copy over the ensemble testing data
#echo "For debug purposes, copy dummy test data to the program output directories."
#echo "NOTE: ONCE THE CLASSIFICATION STEPS ARE WORKING, THIS STEP SHOULD BE COMMENTED OUT."
#hadoop fs -copyFromLocal /home/$USER/CS286_Project/data/mvdm /user/$USER/$DATA_DIR
#hadoop fs -mkdir /user/$USER/$DATA_DIR/mvdm
#hadoop fs -copyFromLocal /home/$USER/CS286_Project/data/overlap /user/$USER/$DATA_DIR
#hadoop fs -mkdir /user/$USER/$DATA_DIR/overlap
#hadoop fs -copyFromLocal /home/$USER/CS286_Project/data/naive_bayes /user/$USER/$DATA_DIR

# Copy the naive Bayes jar into the oozie lib directory.
echo "Copy the Naive Bayes JAR to the oozie library directory."
cp /home/$USER/CS286_Project/$NAIVE_BAYES_DIR/naive_bayes.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the KNN jar into the oozie lib directory.
echo "Copy the KNN JAR to the oozie library directory."
cp /home/$USER/CS286_Project/$KNN_DIR/knn.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the ensemble jar into the oozie lib directory.
echo "Copy the ensemble JAR to the local Oozie lib directory."
cp /home/$USER/CS286_Project/$ENSEMBLE_DIR/ensemble.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the accuracy calculator jar into the oozie lib directory.
echo "Copy the accuracy calculator JAR to the local Oozie lib directory."
cp /home/$USER/CS286_Project/$ACCURACY_CALC_DIR/accuracy_calc.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib


# Last Step - Copy the local Oozie flow directory to the /user/user01 directory.
echo "Copy the oozie program to /user/${USER}/${OOZIE_DIR}"
hadoop fs -rmr /user/$USER/$OOZIE_DIR >/dev/null
hadoop fs -copyFromLocal /home/$USER/CS286_Project/$OOZIE_DIR /user/$USER/
echo "Building of Oozie flow is completed."
