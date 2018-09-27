#!/bin/bash
./build-maven.sh
cd ..
cd workspace/parent
mkdir target
mvn surefire:test > target/test-results-console-out.txt
cd ../..
cd scripts
./maven-site.sh

