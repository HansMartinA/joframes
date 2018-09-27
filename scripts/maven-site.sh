#!/bin/bash
cd ..
cd workspace/parent
mvn site -DskipTests=true
cp -r ./api/target/site ./target/site/site
mv ./target/site/site ./target/site/api
cp -r ./api-external/target/site ./target/site/site
mv ./target/site/site ./target/site/api-external
cp -r ./api-test-data/target/site ./target/site/site
mv ./target/site/site ./target/site/api-test-data
cp -r ./ast-acha/target/site ./target/site/site
mv ./target/site/site ./target/site/ast-acha
cp -r ./ast-ap/target/site ./target/site/site
mv ./target/site/site ./target/site/ast-ap
cp -r ./ast-base/target/site ./target/site/site
mv ./target/site/site ./target/site/ast-base
cp -r ./language-syntax/target/site ./target/site/site
mv ./target/site/site ./target/site/language-syntax
cp -r ./shrike/target/site ./target/site/site
mv ./target/site/site ./target/site/shrike
cp -r ./test-tools/target/site ./target/site/site
mv ./target/site/site ./target/site/test-tools
cp -r ./ui-cli/target/site ./target/site/site
mv ./target/site/site ./target/site/ui-cli
cp -r ./ui-gui/target/site ./target/site/site
mv ./target/site/site ./target/site/ui-gui
cd ../..
cd scripts

