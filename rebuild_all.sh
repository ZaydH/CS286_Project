#!/bin/bash

export OOZIE_DIR=oozie_flow
export PREPROCESSOR_DIR=RecipePreprocessor

# Remake the oozie lib directory from scratch.
rm -rf $OOZIE_DIR/lib 
mkdir $OOZIE_DIR/lib

# Copy the processor jar into the oozie lib directory.
cp $PREPROCESSOR_DIR/Preprocessor.jar $OOZIE_DIR/lib
