#!/bin/bash
./init.sh
./maven-build.sh
cd ..
cd workspace/joframes-parent
mkdir target
mvn surefire:test > target/test-results-console-out.txt
cd ../..
cd scripts
./maven-site.sh

