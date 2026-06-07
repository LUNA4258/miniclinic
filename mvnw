#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF)
# under the Apache License, Version 2.0.
# ----------------------------------------------------------------------------

set -e

MAVEN_PROJECTBASEDIR="${0%/*}"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

DISTRIBUTION_URL=$(grep "distributionUrl" "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2)
FILENAME=$(basename "$DISTRIBUTION_URL" .zip)
DIST_CACHE="$HOME/.m2/wrapper/dists/$FILENAME"

find_maven() {
  for dir in "$DIST_CACHE"/*/; do
    if [ -f "${dir}bin/mvn" ]; then
      echo "${dir}bin/mvn"
      return
    fi
  done
}

MAVEN_EXEC=$(find_maven)

if [ -z "$MAVEN_EXEC" ]; then
  echo "Downloading Maven from $DISTRIBUTION_URL..."
  mkdir -p "$DIST_CACHE"
  TMPFILE=$(mktemp /tmp/maven-XXXXXX.zip)
  curl -fsSL "$DISTRIBUTION_URL" -o "$TMPFILE"
  unzip -q "$TMPFILE" -d "$DIST_CACHE"
  rm "$TMPFILE"
  MAVEN_EXEC=$(find_maven)
fi

if [ -z "$MAVEN_EXEC" ]; then
  echo "ERROR: Maven could not be downloaded or found."
  exit 1
fi

exec "$MAVEN_EXEC" "$@"
