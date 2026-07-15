#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
chmod +x gradlew
./gradlew testDebugUnitTest assembleDebug --stacktrace
printf '\nAPK: app/build/outputs/apk/debug/app-debug.apk\n'
