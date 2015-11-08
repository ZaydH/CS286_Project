#!/bin/bash

export DATASET=/home/user01/CS286_Project/RecipePreprocessor/train.json

export TRAINING_SET_DIR=/home/user01/CS286_Project/RecipePreprocessor/training_set
export TRAINING_SET_FILENAME=training_set.txt

export TEST_SET_DIR=/home/user01/CS286_Project/RecipePreprocessor/test_set/
export NUMBER_TEST_SET_FILES=10

export CUISINE_FILE_DIR=/home/user01/CS286_Project/RecipePreprocessor/cuisines/
export CUISINE_FILE_NAME=cuisines.txt

export JAR_FILE=preprocessor/target/preprocessor-1.0-SNAPSHOT-jar-with-dependencies.jar

# Clear the training set file
rm -rf $TRAINING_SET_DIR
mkdir -p $TRAINING_SET_DIR

# Clear the test set files
rm -rf $TEST_SET_DIR
mkdir -p $TEST_SET_DIR

# Clear the cuisine set file
rm -rf $CUISINE_FILE_DIR
mkdir -p $CUISINE_FILE_DIR

java -jar $JAR_FILE $DATASET $TRAINING_SET_DIR $TEST_SET_DIR $NUMBER_TEST_SET_FILES $CUISINE_FILE_DIR
java -jar $JAR_FILE /user/user01/data/train.json /user/user01/data/training_set/ /user/user01/data/test_set/ 10 /user/user01/data/cuisines/
