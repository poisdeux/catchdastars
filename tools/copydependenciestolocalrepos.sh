#!/bin/bash
# Script to download dependencies from remote repositories to local repository
# using maven. Note: this requires maven 2.8 or higher
if [ ${#} -ne 1 ]
then
	echo "Usage: $0 /path/to/build.gradle"
  exit 1
fi

BUILDGRADLE=$1
if [ ! -r ${BUILDGRADLE} ]
then
	echo "Error: file ${BUILDGRADLE} not readable"
  exit 1
fi

if [ ! -f ${BUILDGRADLE} ] 
then
  echo "Error: file ${BUILDGRADLE} not a regular file"
  exit 1
fi

GDXVERSION=$(grep 'gdxVersion = ' ${BUILDGRADLE} | sed "s/.* = '\(.*\)'/\1/")
ROBOVMVERSION=$(grep 'roboVMVersion = ' ${BUILDGRADLE} | sed "s/.* = '\(.*\)'/\1/")
REPOS=$(grep 'maven { url' ${BUILDGRADLE} | sed 's/.*"\(.*\)".*/\1/' | tr '\n' ',')
DEPS=$(grep -e 'compile "' -e 'natives "' ${BUILDGRADLE} | sed -e 's/.*"\(.*\)"/\1/' -e "s/\$gdxVersion/${GDXVERSION}/" -e "s/\${roboVMVersion}/${ROBOVMVERSION}/")

for dependency in ${DEPS}
do
	echo Downloading ${dependency}
	if ! mvn -q org.apache.maven.plugins:maven-dependency-plugin:2.8:get -DremoteRepositories=${REPOS} -Dartifact=${dependency} > /dev/null 2>&1
  then
    echo "Failed to download ${dependency}"
	fi
done
