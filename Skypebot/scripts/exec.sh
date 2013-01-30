#!/bin/bash

echo
echo
echo "------------------------<>------------------------"
echo "Internal exec - called by mvn exec:exec"
echo "------------------------<>------------------------"
echo
echo
mvn package
cp target/skypebot-0.1-SNAPSHOT.jar ./
java -jar skypebot-0.1-SNAPSHOT.jar
