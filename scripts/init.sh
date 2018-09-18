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
cp -r securibench-micro/src/securibench parent/test-tools/src/main/java
rm parent/test-tools/src/main/java/securibench/micro/basic/Basic40.java -f
cd ..
fi
cd scripts

