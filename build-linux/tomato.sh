#!/bin/bash

java -jar -splash:${TOMATO_HOME}/splash.png \
-Dfile.encoding=UTF-8 \
${TOMATO_HOME}/tomato-${TOMATO_TAG}.jar
