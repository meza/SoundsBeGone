#!/bin/sh

VERSION=$1

echo "Replacing version with ${VERSION}"
sed -e "s/VERSION/${VERSION}/" -i gradle.properties
sed -e "s/VERSION_REPL/${VERSION}/" -i src/main/java/gg/meza/SoundsBeGone.java
sed -e "s/POSTHOG_API_KEY_REPL/${POSTHOG_API_KEY}/" -i src/client/java/gg/meza/analytics/Analytics.java

./gradlew build -x test # we've ran the tests earlier in the build
