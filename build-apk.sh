#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if ! command -v java >/dev/null 2>&1; then
  echo "ERROR: Install JDK 17 first." >&2; exit 1
fi
if [[ -z "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  echo "ERROR: Set ANDROID_HOME or ANDROID_SDK_ROOT to your Android SDK path." >&2; exit 1
fi
if ! command -v gradle >/dev/null 2>&1; then
  echo "ERROR: Gradle is not installed. Open this folder in Android Studio and use its Gradle sync/build, or install Gradle 8.9." >&2; exit 1
fi
gradle --no-daemon assembleDebug
echo
echo "APK output: app/build/outputs/apk/debug/app-debug.apk"
