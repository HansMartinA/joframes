#!/bin/bash
cd ..
cd workspace/joframes-parent
mvn site -DskipTests=true
cp -r ./joframes-api/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-api
cp -r ./joframes-api-external/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-api-external
cp -r ./joframes-api-test-data/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-api-test-data
cp -r ./joframes-ast-acha/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-ast-acha
cp -r ./joframes-ast-ap/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-ast-ap
cp -r ./joframes-ast-base/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-ast-base
cp -r ./joframes-language-syntax/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-language-syntax
cp -r ./joframes-shrike/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-shrike
cp -r ./joframes-tests/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-tests
cp -r ./joframes-ui-cli/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-ui-cli
cp -r ./joframes-ui-gui/target/site ./target/site/site
mv ./target/site/site ./target/site/joframes-ui-gui
cd ../..
cd scripts

