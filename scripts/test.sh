#!/usr/bin/env bash

set -euo pipefail

[[ -d "${PWD}"/maven && ! -d "${HOME}"/.m2 ]] && ln -s "${PWD}"/maven "${HOME}"/.m2

cd source

./mvnw test
