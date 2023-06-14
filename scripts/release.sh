#!/bin/sh

VERSION=$1

echo "Replacing version with ${VERSION}"
sed -e "s/VERSION/${VERSION}/" -i gradle.properties

./gradlew build -x test # we've ran the tests earlier in the build
