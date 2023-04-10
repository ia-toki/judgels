#!/bin/bash

set -ex

exec ./bin/gabriel-dist server var/conf/gabriel.yml
