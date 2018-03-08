#!/bin/bash

set -ex

DIR="$(cd "$( dirname "${BASH_SOURCE[0]}")" && pwd)/.."

$DIR/../gradlew clean distTar
tar -xf $DIR/build/distributions/jophiel-* --strip-components=1 -C $DIR/build/distributions
rm $DIR/build/distributions/*.tgz
mv $DIR/build/distributions/var/conf/jophiel.yml.example $DIR/build/distributions/var/conf/jophiel.yml
cp $DIR/dockerfiles/jophiel/Dockerfile $DIR/build/
cp $DIR/dockerfiles/jophiel/.dockerignore $DIR/build/
