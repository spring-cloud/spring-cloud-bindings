#!/usr/bin/env bash

set -euo pipefail

[[ -d "${PWD}"/maven && ! -d "${HOME}"/.m2 ]] && ln -s "${PWD}"/maven "${HOME}"/.m2

REPOSITORY="${PWD}"/repository

cd source

./mvnw deploy -Dmaven.test.skip=true -DaltDeploymentRepository="local::default::file://${REPOSITORY}"
