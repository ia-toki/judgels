# sealtiel

> by **Ikatan Alumni Tim Olimpiade Komputer Indonesia (TOKI)**.

[![Build Status](https://img.shields.io/travis/judgels-dev/sealtiel/master.svg)](https://travis-ci.org/judgels-dev/sealtiel)
[![GitHub Release](https://img.shields.io/github/tag/judgels-dev/sealtiel.svg)](https://github.com/judgels-dev/sealtiel/releases)
[![Docker Pulls](https://img.shields.io/docker/pulls/judgels/sealtiel.svg)](https://hub.docker.com/r/judgels/sealtiel)
[![license](https://img.shields.io/github/license/judgels-dev/sealtiel.svg)](https://github.com/judgels-dev/sealtiel/blob/master/LICENSE.txt)

Grading request-response messaging broker for the [Judgment Angels](https://github.com/judgels-dev/judgels) platform.

## Stack

- [Dropwizard](http://www.dropwizard.io/)
- [RabbitMQ](https://www.rabbitmq.com/)

## Development

### Running locally

1. Install [RabbitMQ](https://www.rabbitmq.com/download.html).
1. Run RabbitMQ server:

        sudo rabbitmq-server

1. Edit the config file at `sealtiel-dist/var/conf/sealtiel.yml` as necessary.
1. Start the development server:

        ./gradlew run

### Running the tests

    ./gradlew check

### Building docker image

    ./scripts/build_docker_image.sh <TAG>

The docker image will be built as `judgels/sealtiel:<TAG>`.

## License

GNU GPL version 2