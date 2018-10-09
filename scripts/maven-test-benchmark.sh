#!/bin/bash
./init.sh
./maven-build.sh
cd ..
cd workspace/joframes-parent
mvn surefire:test
mvn surefire:test
mvn surefire:test
mvn surefire:test
mvn surefire:test
mvn surefire:test
mvn surefire:test
mvn surefire:test
mvn surefire:test
mvn surefire:test
cd ../..
cd scripts

