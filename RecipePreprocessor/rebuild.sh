#!/bin/bash

export JAR_NAME=Preprocessor

rm -rf bin 
mkdir bin  

javac -d bin -cp gson-2.4.jar src/RecipePreprocessor/Preprocessor.java
jar -cvfm $JAR_NAME.jar MANIFEST.MF -C bin/ . 
