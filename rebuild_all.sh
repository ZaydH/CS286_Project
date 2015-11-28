#!/bin/bash

export OOZIE_DIR=oozie_flow
export DATA_DIR=data
export PREPROCESSOR_DIR=RecipePreprocessor

export NAIVE_BAYES_DIR=NaiveBayesClassifier
#export KNN_DIR=KNN_Recipe
#export KNN_JAR=knn.jar
export KNN_DIR=KNN_Project
export KNN_JAR_NAME=KNN.jar

export ENSEMBLE_DIR=EnsembleClassifier
export ACCURACY_CALC_DIR=AccuracyCalculator

# Remake the oozie lib directory from scratch.
rm -rf /home/$USER/CS286_Project/$OOZIE_DIR/lib 
mkdir /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the processor jar into the oozie lib directory.
echo "Copying the recipe preprocessor."
cp /home/$USER/CS286_Project/$PREPROCESSOR_DIR/Preprocessor.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy over the source data set.
echo "Recreating the directory: /user/${USER}/${DATA_DIR}"
rm -rf /user/$USER/$DATA_DIR
mkdir -p /user/$USER/$DATA_DIR
cp /home/$USER/CS286_Project/RecipePreprocessor/train.json /user/$USER/$DATA_DIR

# Copy over the ensemble testing data
#rm -rf /user/$USER/data/mvdm
#cp -r /home/$USER/CS286_Project/data/mvdm /user/$USER/$DATA_DIR
#mkdir -p /user/$USER/$DATA_DIR/mvdm
#cp -r /home/$USER/CS286_Project/data/overlap /user/$USER/$DATA_DIR
#mkdir -p /user/$USER/$DATA_DIR/overlap # Debug only
#cp -r /home/$USER/CS286_Project/data/naive_bayes /user/$USER/$DATA_DIR


# Copy the naive bayes jar into the oozie lib directory.
echo "Copying the Naive Bayes JAR to the oozie library directory."
cp /home/$USER/CS286_Project/$NAIVE_BAYES_DIR/naive_bayes.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the KNN jar into the oozie lib directory.
echo "Copying the KNN JAR to the oozie library directory."
cp /home/$USER/CS286_Project/$KNN_DIR/$KNN_JAR_NAME /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the ensemble jar into the oozie lib directory.
echo "Copying the ensemble JAR file to the oozie library directory."
cp /home/$USER/CS286_Project/$ENSEMBLE_DIR/ensemble.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Copy the accuracy calculator jar into the oozie lib directory.
echo "Copying the accuracy calculator JAR file to the oozie library directory."
cp /home/$USER/CS286_Project/$ACCURACY_CALC_DIR/accuracy_calc.jar /home/$USER/CS286_Project/$OOZIE_DIR/lib

# Last Step - Copy the local Oozie flow directory to the /user/user01 directory.
echo "Copying the oozie library directory to MapR-FS."
hadoop fs -rmr /user/$USER/$OOZIE_DIR
hadoop fs -copyFromLocal /home/$USER/CS286_Project/$OOZIE_DIR /user/$USER/

echo "Rebuild completed."
