#!/usr/bin/env bash

# This script is intended to install the minimum requirement to start
# developing judgels in ubuntu22.04

set -eu -o pipefail

: ${JUDGELS_HOME:=$(cd "$(dirname $0)"/..; pwd)}
export JUDGELS_HOME

if [[ -t 1 ]] # if on an interactive terminal
then
  echo "This script will clobber some system settings. Are you sure you want to"
  echo -n "continue? "
  while true
  do
    read -p "[yes/no] " ANSWER
    ANSWER=$(echo "$ANSWER" | tr /a-z/ /A-Z/)
    if [[ $ANSWER = YES ]]
    then
      break
    elif [[ $ANSWER = NO ]]
    then
      echo "OK, Bye!"
      exit 1
    fi
  done
fi

set -x

# install tools via apt-get
UBUNTU_JAVA_VERSION="11"
sudo apt-get --yes install git ssh wget curl unzip vim-common \
  openjdk-${UBUNTU_JAVA_VERSION}-jdk openjdk-${UBUNTU_JAVA_VERSION}-source \
  openjdk-${UBUNTU_JAVA_VERSION}-dbg npm mysql-client

# install yarn
sudo npm install --global yarn

java --version
npm --version
yarn --version

# assemble judgels-server-app
cd $JUDGELS_HOME/judgels-backends/judgels-server-app
cp var/conf/judgels-server.yml.example var/conf/judgels-server.yml
../gradlew assemble
# extract sample data
mkdir -p var/data
tar -xf ../../seeds/problems.tar.gz -C var/data/
tar -xf ../../seeds/lessons.tar.gz -C var/data/
tar -xf ../../seeds/submissions.tar.gz -C var/data/

# build judgels-client
cd $JUDGELS_HOME/judgels-client
cp public/var/conf/judgels-client.js.example public/var/conf/judgels-client.js
yarn install


# mysql server still require its own setup.
# Installing in localhost ubuntu is trivial:
# sudo apt-get --yes mysql-server
#
# If using docker, a mysql container can be started as follow:
# docker run --name mysql -e MYSQL_ROOT_PASSWORD=judgels-root \
#   -e MYSQL_USER=judgels -e MYSQL_PASSWORD=judgels \
#   -e MYSQL_DATABASE=judgels -d mysql:8
#
# After mysql server is up, insert sample data into judgels db.
# mysql -h ${MYSQL_HOST} -u judgels -p judgels < $JUDGELS_HOME/seeds/judgels.sql
#
# Point judgels-server.yml to the mysql host/ip as needed.
# sed -i "s|jdbc:mysql://localhost|jdbc:mysql://${MYSQL_HOST}|g" \
#   $JUDGELS_HOME/judgels-backends/judgels-server-app/var/conf/judgels-server.yml
#
# The remaining instructions to start the app is available at:
# https://github.com/ia-toki/judgels/wiki/Dev's-Guide:-Running-from-source
