#!/bin/bash

export DATASET=/home/user01/CS286_Project/RecipePreprocessor/train.json

export TRAINING_SET_DIR=/home/user01/CS286_PROJECT/RecipePreprocessor/
export TRAINING_SET_FILENAME=training_set.txt

export TEST_SET_DIR=/home/user01/CS286_Project/RecipePreprocessor/test_set/
export NUMBER_TEST_SET_FILES=10

export CUISINE_FILE_DIR=/home/user01/CS286_Project/RecipePreprocessor/cuisines/
export CUISINE_FILE_NAME=cuisines.txt

# Clear the training set file
rm -rf $TRAINING_SET_DIR
mkdir -p $TRAINING_SET_DIR

# Clear the test set files
rm -rf $TEST_SET_DIR
mkdir -p $TEST_SET_DIR

# Clear the cuisine set file
rm -rf $CUISINE_FILE_DIR
mkdir -p $CUISINE_FILE_DIR

java -cp gson-2.4.jar -jar Preprocessor.jar $DATASET $TRAINING_SET_DIR$TRAINING_SET_FILENAME $TEST_SET_DIR $NUMBER_TEST_SET_FILES $CUISINE_FILE_DIR$CUISINE_FILE_NAME
