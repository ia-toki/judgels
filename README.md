# raphael

> by **Ikatan Alumni Tim Olimpiade Komputer Indonesia (TOKI)**.

[![Build Status](https://img.shields.io/travis/judgels-dev/raphael/master.svg)](https://travis-ci.org/judgels-dev/raphael)
[![GitHub Release](https://img.shields.io/github/tag/judgels-dev/raphael.svg)](https://github.com/judgels-dev/raphael/releases)
[![Docker Pulls](https://img.shields.io/docker/pulls/judgels/raphael.svg)](https://hub.docker.com/r/judgels/raphael)
[![license](https://img.shields.io/github/license/judgels-dev/raphael.svg)](https://github.com/judgels-dev/raphael/blob/master/LICENSE.txt)

Serves the client-side frontend app for the [Judgment Angels](https://github.com/judgels-dev/judgels) platform.

## Stack

- TypeScript
- React using [CRA with TypeScript](https://github.com/wmonk/create-react-app-typescript)
- [Blueprint](http://blueprintjs.com/) UI

## Development

### Running locally

1. Install [Yarn](https://yarnpkg.com).
1. Download dependencies:

        yarn

1. Edit the config file at `public/var/conf/raphael.js` as necessary.    
1. Start the development server:

        yarn start

1. Go to http://localhost:3000 to see the app in action.

### Running the tests

    yarn test
    
### Building docker image

    ./scripts/build_docker_image.sh <TAG>
    
The docker image will be built as `judgels/raphael:<TAG>`.
    
## License

GNU GPL version 2