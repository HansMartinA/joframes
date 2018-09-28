#!/bin/bash
./init.sh
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
cd ../..
cd scripts
./maven-site.sh

