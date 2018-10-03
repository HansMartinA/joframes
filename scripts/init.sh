#!/bin/bash
cd ..
if
test -e workspace/joana/dist;
then
echo "Skip building of WALA and Joana.";
else
git submodule init
git submodule update
cd workspace/joana
git submodule init
git submodule update
cd contrib/wala
mvn package -DskipTests=true -e
cd ../..
ant
cd ..
echo "Built WALA and Joana."
cp -r securibench-micro/src/securibench joframes-parent/joframes-tests/src/main/java
rm joframes-parent/joframes-tests/src/main/java/securibench/micro/basic/Basic40.java -f
cd OSIP/src
mvn package -DskipTests=true
cd ../../..
fi
cd scripts

