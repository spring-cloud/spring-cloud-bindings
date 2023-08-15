#!/usr/bin/env bash

set -euo pipefail

export BUILD_INFO_LOCATION=$(pwd)/repository/build-info.json

java -jar /concourse-release-scripts.jar publishToCentral 'RELEASE' "$BUILD_INFO_LOCATION" repository

echo "Sync complete"