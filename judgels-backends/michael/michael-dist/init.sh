#!/bin/bash

set -ex

exec ./bin/michael-dist server var/conf/michael.yml
