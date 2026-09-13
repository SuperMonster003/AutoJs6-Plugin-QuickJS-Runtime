#!/usr/bin/env bash
set -euo pipefail

page_size="$(adb shell 'getconf PAGE_SIZE 2>/dev/null || getconf PAGESIZE 2>/dev/null' | tr -d '\r')"
test "$page_size" = "16384"
adb install -r -t ci-apks/debug/app-x86_64-debug.apk
adb install -r -t ci-apks/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r \
  -e requiredApiLevel 35 \
  io.github.supermonster003.autojs6.plugin.quickjs.runtime.test/androidx.test.runner.AndroidJUnitRunner \
  | tee instrumentation-16kb.txt
if grep -Eq 'FAILURES|INSTRUMENTATION_FAILED|shortMsg=|Process crashed' instrumentation-16kb.txt; then
    exit 1
fi
grep -Eq '^OK \([1-9][0-9]* tests?\)' instrumentation-16kb.txt
