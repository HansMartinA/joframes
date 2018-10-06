#!/bin/bash
./init.sh
./maven-build.sh
cd ..
cd workspace/joframes-parent
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@default-test
mvn surefire:test@performance-test
cd ../..
cd scripts

