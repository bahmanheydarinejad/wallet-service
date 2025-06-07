#!/bin/bash
set -e  # Exit on error

echo "Starting JAR build process..."

echo "Cloning branch '${GIT_BRANCH}' from ${GIT_REPO_URL}"
rm -rf /app-source
git clone --branch "${GIT_BRANCH}" "${GIT_REPO_URL}" /app-source
echo "Clone completed."

cd /app-source

if [ -f "./.mvn/wrapper/settings.xml" ]; then
  echo "Using project-specific settings.xml"
  mvn clean package -s ./.mvn/wrapper/settings.xml
else
  echo "Using default settings"
  mvn clean package
fi

# Get build timestamp
BUILD_TIMESTAMP=$(date +%Y%m%d-%H%M%S)

# Find the generated jar (only non-*-sources/test jars)
JAR_FILE=$(find ./target -maxdepth 1 -type f -name "*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "No JAR file found!"
  exit 1
fi

# Extract base name and append timestamp
BASE_NAME=$(basename "$JAR_FILE" .jar)
NEW_JAR_NAME="${BASE_NAME}-${BUILD_TIMESTAMP}.jar"

echo "Copying JAR to /artifacts as $NEW_JAR_NAME"
cp "$JAR_FILE" "/artifacts/$NEW_JAR_NAME"

echo "Cleaning up source code"
rm -rf /app-source

echo "JAR build process completed successfully."
