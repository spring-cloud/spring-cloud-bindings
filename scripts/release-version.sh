#!/usr/bin/env bash

set -euo pipefail

[[ -d "${PWD}"/maven && ! -d "${HOME}"/.m2 ]] && ln -s "${PWD}"/maven "${HOME}"/.m2

cd source

./mvnw versions:set -DremoveSnapshot=true -DgenerateBackupPoms=false
VERSION=$(./mvnw --quiet help:evaluate -DforceStdout -Dexpression=project.version)

git add pom.xml
git checkout -- .

git \
  -c user.name='Paketo Robot' \
  -c user.email='robot@paketo.io' \
  commit \
  --signoff \
  --message "v${VERSION} Release"

echo -n "${VERSION}" > ../version/version
