#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")/.."

if command -v /usr/libexec/java_home >/dev/null 2>&1; then
  export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17)}"
fi

if [ -x ./gradlew ]; then
  gradle_cmd=(./gradlew)
else
  gradle_cmd=(sh ./gradlew)
fi

echo "[verify] 0/4 dependency resolution"
"${gradle_cmd[@]}" --no-daemon dependencies --configuration runtimeClasspath >/dev/null

echo "[verify] 1/4 build"
"${gradle_cmd[@]}" --no-daemon clean assemble

echo "[verify] 2/4 format"
"${gradle_cmd[@]}" --no-daemon spotlessCheck

echo "[verify] 3/4 tests"
"${gradle_cmd[@]}" --no-daemon test

echo "[verify] ALL GREEN"
