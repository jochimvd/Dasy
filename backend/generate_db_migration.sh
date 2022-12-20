#!/bin/bash

set -e

DEFAULT="change-me"
DATE_WITH_TIME=$(date "+%Y%m%d%H%M%S")

# Check if migration label is provided
if [ -z "$1" ]; then
  MIGRATION_LABEL=$DEFAULT
else
  MIGRATION_LABEL=$1
fi

# Get the directory that the script is in
SCRIPT_DIR=$(dirname "$0")

cd "${SCRIPT_DIR}"

mvn compile
mvn liquibase:diff -Dliquibase.diffChangeLogFile=src/main/resources/db/changelog/changes/${DATE_WITH_TIME}-${MIGRATION_LABEL}.xml

echo "  - include:" >> src/main/resources/db/changelog/db.changelog-master.yaml
echo "      file: db/changelog/changes/${DATE_WITH_TIME}-${MIGRATION_LABEL}.xml" >> src/main/resources/db/changelog/db.changelog-master.yaml

# Remove leftover hibernate indices
rm -r xyz.vandijck.safety.backend.entity*
