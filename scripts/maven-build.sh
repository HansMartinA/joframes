#!/bin/bash
cd ..
cd workspace/joana/contrib/wala/com.ibm.wala.core/target
mvn install:install-file -Dfile=./com.ibm.wala.core-1.4.4-SNAPSHOT.jar -DgroupId=edu.kit.ipd -DartifactId=wala.core -Dversion=-1 -Dpackaging=jar
cd ../..
cd com.ibm.wala.shrike/target
mvn install:install-file -Dfile=./com.ibm.wala.shrike-1.4.4-SNAPSHOT.jar -DgroupId=edu.kit.ipd -DartifactId=wala.shrike -Dversion=-1 -Dpackaging=jar
cd ../..
cd com.ibm.wala.util/target
mvn install:install-file -Dfile=./com.ibm.wala.util-1.4.4-SNAPSHOT.jar -DgroupId=edu.kit.ipd -DartifactId=wala.util -Dversion=-1 -Dpackaging=jar
cd ../../../..
cd dist
mvn install:install-file -Dfile=./joana.ui.ifc.wala.console.jar -DgroupId=edu.kit.ipd -DartifactId=joana-ui-ifc -Dversion=-1 -Dpackaging=jar
cd ../..
cd joframes-parent
mvn clean install -DskipTests=true
cd ..
cp OSIP/src/osip-monitoring-controller/target/osip-monitoring-controller-1.1-with-dependencies.jar joframes-parent/joframes-tests/target/osip-monitoring-controller-1.1-with-dependencies.jar
cp OSIP/src/osip-simulation-controller/target/osip-simulation-controller-1.1-with-dependencies.jar joframes-parent/joframes-tests/target/osip-simulation-controller-1.1-with-dependencies.jar
cd ..
cd scripts

