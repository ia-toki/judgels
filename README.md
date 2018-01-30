# jophiel

> by **Ikatan Alumni Tim Olimpiade Komputer Indonesia (TOKI)**.

[![Build Status](https://img.shields.io/travis/judgels-dev/jophiel/master.svg)](https://travis-ci.org/judgels-dev/jophiel)
[![GitHub Release](https://img.shields.io/github/tag/judgels-dev/jophiel.svg)](https://github.com/judgels-dev/jophiel/releases)
[![Docker Pulls](https://img.shields.io/docker/pulls/judgels/jophiel.svg)](https://hub.docker.com/r/judgels/jophiel)
[![license](https://img.shields.io/github/license/judgels-dev/jophiel.svg)](https://github.com/judgels-dev/jophiel/blob/master/LICENSE.txt)

User account management and authentication for the [Judgment Angels](https://github.com/judgels-dev/judgels) platform.

## Stack

- [Dropwizard](http://www.dropwizard.io/)

## Development

### Running locally

1. Edit the config file at `jophiel-dist/var/conf/jophiel.yml` as necessary.
1. Start the development server:

        ./gradlew run

1. Go to http://localhost:9001 to see the app in action.

### Running the tests

    ./gradlew check

### Building docker image

    ./scripts/build_docker_image.sh <TAG>

The docker image will be built as `judgels/jophiel:<TAG>`.

## License

GNU GPL version 2