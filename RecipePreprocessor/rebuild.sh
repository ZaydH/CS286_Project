#!/bin/bash

export MAVEN_JAR_NAME=preprocessor-1.0-SNAPSHOT-jar-with-dependencies.jar
export FINAL_JAR_NAME=Preprocessor.jar

cd preprocessor
mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Denforcer.skip=true
cd ..
cp preprocessor/target/$MAVEN_JAR_NAME $FINAL_JAR_NAME

